package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.HomeViewModel;

public class UpdateHomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private EditText editTextName, editTextAlamat;
    private Button buttonSave, buttonDelete;
    private DatabaseReference databaseHouses, databaseLamps, databaseRooms;
    private Rumah rumah;
    private GoogleMap mGoogleMap;
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLaTitude, mLaTitudeTemp, mLongTitude, mLongTitudeTemp;
    private static final int REQUEST_LOCATION_SETTINGS = 1001;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String ARG_ALAMAT = "home";
    private static final String TAG = "UpdateHomeFragment";
    private boolean lightOnFound = false;

    public UpdateHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rumah = (Rumah) getArguments().getSerializable("rumah");
        }
        // Initialize Firebase Database reference
        databaseHouses = FirebaseDatabase.getInstance().getReference("smart_home/rumah");
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");
        databaseLamps = FirebaseDatabase.getInstance().getReference("smart_home/lampu");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_house, container, false);

        // Initialize EditText fields and Save Button
        editTextName = view.findViewById(R.id.editTextName);
        editTextAlamat = view.findViewById(R.id.editAlamatAja);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        mMapView = view.findViewById(R.id.maps_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        if (rumah != null) {
            editTextName.setText(rumah.getNama());
            editTextAlamat.setText(rumah.getAlamat_rumah());
        }

        if (rumah != null) {
            setAlamatData(rumah);
        }

        // Save button click listener
        buttonSave.setOnClickListener(v -> confirmUpdate());

        buttonDelete.setOnClickListener(v -> confirmDelete());

        return view;
    }


    private void updateHouse() {
        String name = editTextName.getText().toString().trim();
        String alamat = editTextAlamat.getText().toString().trim();
        double updateLat = 0;
        double updateLong = 0;


        if (rumah != null || mLaTitude != mLaTitudeTemp && mLongTitude != mLongTitudeTemp) {
            updateLat = mLaTitude;
            updateLong = mLongTitude;

            rumah.setNama(name);
            rumah.setAlamat_rumah(alamat);
            rumah.setLatitude(updateLat);
            rumah.setLongtitude(updateLong);

            databaseHouses.child(rumah.getRumahId()).setValue(rumah, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(requireContext(), "House updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update house: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "House data is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void markHouseAsDeleted() {
        if (rumah != null) {
            databaseRooms.orderByChild("rumahId").equalTo(rumah.getRumahId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot roomsSnapshot) {
                    if (!roomsSnapshot.exists()) {
                        proceedWithHouseDeletion();
                        return;
                    }

                    checkLightsInRooms(roomsSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Failed to retrieve rooms: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "House data is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLightsInRooms(@NonNull DataSnapshot roomsSnapshot) {
        for (DataSnapshot roomSnapshot : roomsSnapshot.getChildren()) {
            String roomId = roomSnapshot.child("ruanganId").getValue(String.class);

            if (roomId != null) {
                databaseLamps.orderByChild("ruanganId").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot lightsSnapshot) {
                        for (DataSnapshot lightSnapshot : lightsSnapshot.getChildren()) {
                            String lightStatus = lightSnapshot.child("status_lampu").getValue(String.class);
                            if ("on".equals(lightStatus)) {
                                lightOnFound = true;
                                Toast.makeText(requireContext(), "The light is on and the house cannot be removed", Toast.LENGTH_SHORT).show();
                                return; // Exit the method if any light is on
                            }
                        }

                        if (!lightOnFound && !lightsSnapshot.exists()) {
                            proceedWithHouseDeletion();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Failed to check lights status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void proceedWithHouseDeletion() {
        rumah.setStatus("Tidak Aktif");

        databaseHouses.child(rumah.getRumahId()).setValue(rumah, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(requireContext(), "House marked as deleted successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(), "Failed to mark house as deleted: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void confirmUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menyimpan data ini?")
                .setPositiveButton("Ya", (dialog, which) -> updateHouse())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> markHouseAsDeleted())
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLastLocation();
            }
        });

        task.addOnFailureListener(requireActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(requireActivity(), REQUEST_LOCATION_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, sendEx.getMessage());
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(requireActivity(), "Tidak dapat mendapatkan lokasi terkini.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.addMarker(new MarkerOptions().position(currentLocation));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    mLaTitude = currentLocation.latitude;
                    mLongTitude = currentLocation.longitude;
                }
                mFusedLocationClient.removeLocationUpdates(this);
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        LatLng location = new LatLng(rumah.getLatitude(), rumah.getLongtitude());
        Log.d("Map Ready", String.valueOf(location));
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

    }

    @SuppressLint("SetTextI18n")
    private void setAlamatData(Rumah mRumah) {
        mLaTitude = mRumah.getLatitude();
        mLongTitude = mRumah.getLongtitude();
        editTextName.setText(mRumah.getNama());
        editTextAlamat.setText(mRumah.getAlamat_rumah());
        Log.d("Angela White", mRumah.toString());
    }


}
