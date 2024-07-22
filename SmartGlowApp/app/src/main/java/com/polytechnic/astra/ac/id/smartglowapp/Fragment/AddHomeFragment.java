package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;

import java.util.Arrays;
import java.util.List;

public class AddHomeFragment extends Fragment implements OnMapReadyCallback {

    private EditText editTextName, editTextAlamat;
    private Button buttonSave, mBtnGetLocation;
    private DatabaseReference databaseUsers;

    private boolean isLocationObtained = false;

    private String userId;
    private String creadby;
    private GoogleMap mGoogleMap;

    private MapView mMapView;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int REQUEST_LOCATION_SETTINGS = 1001;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private double mLaTitude, mLongTitude;

    private static final String TAG = "AddHomeFragment";

    private PlacesClient placesClient;

    public AddHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/rumah");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
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
        View view = inflater.inflate(R.layout.fragment_add_house, container, false);

        // Initialize Firebase Database reference
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/rumah");

        // Initialize EditText
        mBtnGetLocation = view.findViewById(R.id.btn_get_location);
        editTextName = view.findViewById(R.id.editTextName);
        editTextAlamat = view.findViewById(R.id.editAlamatAja);
        buttonSave = view.findViewById(R.id.buttonSave);

        mMapView = view.findViewById(R.id.maps_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("house_id");
            creadby = arguments.getString("creadby");
            if (creadby != null && !creadby.isEmpty()) {
                Toast.makeText(requireContext(), creadby, Toast.LENGTH_SHORT).show();
                Log.d("AddHomeFragment", "Creadby: " + creadby);
            } else {
                Toast.makeText(requireContext(), "Creadby is null or empty", Toast.LENGTH_SHORT).show();
                Log.e("AddHomeFragment", "Creadby is null or empty");
            }

            if (userId != null) {
                // Load existing user data if editing
                databaseUsers.child(userId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Rumah user = dataSnapshot.getValue(Rumah.class);
                        if (user != null) {
                            editTextName.setText(user.getNama());
                            editTextAlamat.setText(user.getAlamat_rumah());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to load user.", Toast.LENGTH_SHORT).show();
                        Log.e("AddHomeFragment", "Failed to load user: " + databaseError.getMessage());
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "Arguments are null", Toast.LENGTH_SHORT).show();
            Log.e("AddHomeFragment", "Arguments are null");
        }

        mBtnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationSettings();
                isLocationObtained = true;
            }
        });





        // Initialize Save Button
        buttonSave.setOnClickListener(v -> confirmSave());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        LatLng location = new LatLng(-6.2088, 106.8456);
        mLaTitude = location.latitude;
        mLongTitude = location.longitude;
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
    }

    private void geocodeAddress(String address) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.builder(address, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Address Location"));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to geocode address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("AddHomeFragment", "Failed to geocode address", e);
        });
    }

    private void saveHome() {
        String name = editTextName.getText().toString().trim();
        String alamat = editTextAlamat.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(alamat) || !isLocationObtained) {
            Toast.makeText(requireContext(), "Please fill in all fields and obtain location", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseUsers.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                long userCount = dataSnapshot.getChildrenCount();
                String newUserId = "rumah_id_" + (userCount + 1);

                if (creadby == null) {
                    creadby = "Unknown"; // Handle null value for creadby
                }

                Rumah user = new Rumah(newUserId, name, alamat,mLaTitude, mLongTitude, "Aktif", creadby);
                databaseUsers.child(newUserId).setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(requireContext(), "Home saved", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(requireContext(), "Failed to save home: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to get user count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
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
                mFusedLocationProviderClient.removeLocationUpdates(this);
            }
        }, Looper.getMainLooper());
    }

    private void confirmSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menyimpan data ini?")
                .setPositiveButton("Ya", (dialog, which) -> saveHome())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void confirmCancel() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Batal?")
                .setMessage("Apakah Anda yakin ingin membatalkan dan membuang perubahan?")
                .setPositiveButton("Ya", (dialog, which) -> requireActivity().getSupportFragmentManager().popBackStack())
                .setNegativeButton("Tidak", null)
                .show();
    }
}
