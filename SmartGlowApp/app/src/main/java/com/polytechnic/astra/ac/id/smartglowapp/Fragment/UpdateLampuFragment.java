package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.app.AlertDialog;
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
    private Button buttonSave, buttonPickColor, buttonDelete;
    private DatabaseReference databaseLamp;
    private String perangkatId;
    private View colorPreview;
    private int currentColor;
    private Integer red = 0, blue = 0, green = 0, pin = 0;
    private String ruanganId;
    private String createdBy;
    private Lampu lampu;

    public UpdateLampuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lampu = (Lampu) getArguments().getSerializable("perangkat");
        }
        databaseLamp = FirebaseDatabase.getInstance().getReference("smart_home/perangkat");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_perangkat, container, false);

        // Initialize EditText and Buttons
        editTextName = view.findViewById(R.id.editTextName);
//        editSerialNumber = view.findViewById(R.id.editSerialNumber);
        editJumlahPin = view.findViewById(R.id.editJumlahPin);
        editPinAkhir = view.findViewById(R.id.editPinAkhir);
        buttonPickColor = view.findViewById(R.id.button_pick_color);
        colorPreview = view.findViewById(R.id.color_preview);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        Bundle arguments = getArguments();
        if (arguments != null) {
            Lampu rumah = (Lampu) arguments.getSerializable("perangkat");

            ruanganId = rumah.getRuanganId();
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
            buttonSave.setOnClickListener(v -> confirmUpdate());
            buttonDelete.setOnClickListener(v -> confirmDelete());
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
        databaseLamp.child(perangkatId).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void updateLampu() {
        String name = editTextName.getText().toString().trim();
        String pinStr = editJumlahPin.getText().toString().trim();
        String pinEnd = editPinAkhir.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pinStr)) {
            Toast.makeText(getActivity(), "Please enter all fields and pick a color", Toast.LENGTH_SHORT).show();
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

        if ((jumlahPin < 0 || jumlahPin > 144 )|| (pin_akhir < 0 || pin_akhir > 144 )){
            Toast.makeText(getActivity(), "Pin hanya boleh antara 0 sampai 144", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseLamp.orderByChild("ruanganId").equalTo(ruanganId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean overlap = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Lampu existingLampu = snapshot.getValue(Lampu.class);
                    if (existingLampu != null && !existingLampu.getLampuId().equals(perangkatId)) {
                        int existingPinStart = existingLampu.getPin_awal();
                        int existingPinEnd = existingLampu.getPin_akhir();

                        // Check for overlap condition
                        if ((jumlahPin >= existingPinStart && jumlahPin <= existingPinEnd) ||
                                (pin_akhir >= existingPinStart && pin_akhir <= existingPinEnd) ||
                                (jumlahPin <= existingPinStart && pin_akhir >= existingPinEnd)) {
                            overlap = true;
                            break;
                        }
                    }
                }

                if (overlap) {
                    Toast.makeText(getActivity(), "Pin range overlaps with existing lamps", Toast.LENGTH_SHORT).show();
                } else {
                    Lampu perangkat = new Lampu(perangkatId, ruanganId, name, "Aktif", "off", createdBy, red, green, blue, jumlahPin, pin_akhir);
                    databaseLamp.child(perangkatId).setValue(perangkat, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Toast.makeText(getActivity(), "Perangkat updated", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update perangkat: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to check existing lamps: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markLampAsDeleted() {
        if (lampu != null) {
            // Cek status lampu
            if ("on".equals(lampu.getStatus_lampu())) {
                // Jika lampu sedang menyala, tampilkan Toast dan tidak hapus
                Toast.makeText(requireContext(), "The light is on and cannot be removed", Toast.LENGTH_SHORT).show();
            } else {
                // Jika lampu tidak menyala, lanjutkan dengan proses penghapusan
                lampu.setStatus("Tidak Aktif");

                databaseLamp.child(lampu.getLampuId()).setValue(lampu, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(requireContext(), "Room marked as deleted successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(requireContext(), "Failed to mark room as deleted: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "Room data is null", Toast.LENGTH_SHORT).show();
        }
    }


    private void confirmUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menyimpan data ini?")
                .setPositiveButton("Ya", (dialog, which) -> updateLampu())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> markLampAsDeleted())
                .setNegativeButton("Tidak", null)
                .show();
    }
}
