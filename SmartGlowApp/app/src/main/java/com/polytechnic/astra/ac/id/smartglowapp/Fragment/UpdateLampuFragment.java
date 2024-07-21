package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Lampu;
import com.polytechnic.astra.ac.id.smartglowapp.R;

import yuku.ambilwarna.AmbilWarnaDialog;

public class UpdateLampuFragment extends Fragment {

    private EditText editTextName, editJumlahPin, editPinAkhir;
    private Button buttonSave, buttonPickColor;
    private DatabaseReference databaseRooms;
    private String perangkatId;
    private View colorPreview;
    private int currentColor;
    private Integer red = 0, blue = 0, green = 0, pin = 0;
    private String houseId;
    private String createdBy;// Assuming you have houseId passed from previous fragment

    public UpdateLampuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/lampu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_perangkat, container, false);

        // Initialize Firebase Database reference
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/lampu");

        // Initialize EditText and Buttons
        editTextName = view.findViewById(R.id.editTextName);
//        editSerialNumber = view.findViewById(R.id.editSerialNumber);
        editJumlahPin = view.findViewById(R.id.editJumlahPin);
        editPinAkhir = view.findViewById(R.id.editPinAkhir);
        buttonPickColor = view.findViewById(R.id.button_pick_color);
        colorPreview = view.findViewById(R.id.color_preview);
        buttonSave = view.findViewById(R.id.buttonSave);

        Bundle arguments = getArguments();
        if (arguments != null) {
            Lampu rumah = (Lampu) arguments.getSerializable("lampu");

            houseId = rumah.getRuanganId();
            System.out.println(rumah.getRuanganId());
            createdBy = rumah.getCreadby();
            System.out.println(createdBy);
            perangkatId = rumah.getLampuId();
            System.out.println(perangkatId);

            if (perangkatId != null) {
                // Load existing perangkat data if editing
                loadPerangkatData(perangkatId);
            }

            // Set default color
            currentColor = Color.WHITE;

            buttonPickColor.setOnClickListener(v -> openColorPicker());

            // Set OnClickListener for Save Button
            buttonSave.setOnClickListener(v -> savePerangkat());
        }

        return view;
    }

    private void updateColorPreview() {
        colorPreview.setBackgroundColor(currentColor);
        red = Color.red(currentColor);
        green = Color.green(currentColor);
        blue = Color.blue(currentColor);
        Toast.makeText(getActivity(), "RGB: (" + red + ", " + green + ", " + blue + ")", Toast.LENGTH_SHORT).show();
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(getContext(), currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // action on cancel
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                updateColorPreview();
            }
        });
        colorPicker.show();
    }
    private void loadPerangkatData(String perangkatId) {
        databaseRooms.child(perangkatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lampu perangkat = dataSnapshot.getValue(Lampu.class);
                if (perangkat != null) {
                    editTextName.setText(perangkat.getNama());
//                    editSerialNumber.setText(perangkat.getSerial_number());
                    editJumlahPin.setText(String.valueOf(perangkat.getPin_awal()));
                    editPinAkhir.setText(String.valueOf(perangkat.getPin_akhir()));

                    // Set RGB values from Firebase
                    red = perangkat.getRed();
                    green = perangkat.getGreen();
                    blue = perangkat.getBlue();
                    currentColor = Color.rgb(red, green, blue);

                    // Update the color preview
                    updateColorPreview();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load perangkat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePerangkat() {
        String name = editTextName.getText().toString().trim();
//        String serial = editSerialNumber.getText().toString().trim();
        String pinStr = editJumlahPin.getText().toString().trim();
        String pinEnd = editPinAkhir.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pinStr)) {
            Toast.makeText(getActivity(), "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int jumlahPin;
        int pin_akhir;
        try {
            jumlahPin = Integer.parseInt(pinStr);
            pin_akhir = Integer.parseInt(pinEnd);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid pin number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (perangkatId == null) {
            // Membaca data house untuk mendapatkan jumlah house saat ini
            databaseRooms.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long houseCount = dataSnapshot.getChildrenCount();
                    String newhouseId = String.valueOf(houseCount + 1); // Generate ID berdasarkan jumlah house + 1
                    String bro = "lampu_id_" + newhouseId;

                    Lampu perangkat = new Lampu(bro, houseId, name,"Aktif","off", createdBy, red, green, blue, jumlahPin, pin_akhir);
                    // Simpan house ke Firebase Database
                    databaseRooms.child(bro).setValue(perangkat, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@NonNull DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(getActivity(), "Perangkat saved", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack(); // Kembali ke fragment sebelumnya
                            } else {
                                Toast.makeText(getActivity(), "Failed to save Perangkat: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Failed to get Perangkat count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Lampu perangkat = new Lampu(perangkatId, houseId, name, "Aktif", "off", createdBy, red, green, blue, jumlahPin, pin_akhir);
            databaseRooms.child(perangkatId).setValue(perangkat, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Toast.makeText(getActivity(), "Perangkat updated", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), "Failed to update perangkat: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
