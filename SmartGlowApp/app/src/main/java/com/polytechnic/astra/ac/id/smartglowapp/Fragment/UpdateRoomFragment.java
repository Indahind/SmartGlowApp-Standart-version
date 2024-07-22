package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class UpdateRoomFragment extends Fragment {

    private EditText editTextName;
    private Button buttonSave, buttonDelete;
    private DatabaseReference databaseRooms, databaseLamp;
    private Ruangan room;

    public UpdateRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = (Ruangan) getArguments().getSerializable("ruangan");
        }
        // Initialize Firebase Database reference
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");
        databaseLamp = FirebaseDatabase.getInstance().getReference("smart_home/lampu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_room, container, false);

        // Initialize EditText fields and Save Button
        editTextName = view.findViewById(R.id.editTextName);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        if (room != null) {
            editTextName.setText(room.getNama());
        }

        // Save button click listener
        buttonSave.setOnClickListener(v -> confirmUpdate());

        buttonDelete.setOnClickListener(v -> confirmDelete());

        return view;
    }

    private void updateRoom() {
        String name = editTextName.getText().toString().trim();

        if (room != null) {
            room.setNama(name);

            databaseRooms.child(room.getRuanganId()).setValue(room, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(requireContext(), "Room updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update house: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Room data is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndDeleteRoom() {
        if (room != null) {
            // Query untuk memeriksa apakah ada lampu yang menyala di ruangan ini
            databaseLamp.orderByChild("ruanganId").equalTo(room.getRuanganId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean lampsOn = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Lampu lamp = snapshot.getValue(Lampu.class);
                        if (lamp != null && "on".equals(lamp.getStatus_lampu())) {
                            lampsOn = true;
                            break;
                        }
                    }

                    if (lampsOn) {
                        Toast.makeText(requireContext(), "The light is on and cannot be removed", Toast.LENGTH_SHORT).show();
                    } else {
                        markRoomAsDeleted();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Failed to check lamps: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Room data is null", Toast.LENGTH_SHORT).show();
        }
    }


    private void markRoomAsDeleted() {
        if (room != null) {
            room.setStatus("Tidak Aktif");

            databaseRooms.child(room.getRuanganId()).setValue(room, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(requireContext(), "Room marked as deleted successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to mark house as deleted: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Room data is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menyimpan data ini?")
                .setPositiveButton("Ya", (dialog, which) -> updateRoom())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);
        builder.setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu yakin untuk menghapus data ini?")
                .setPositiveButton("Ya", (dialog, which) -> checkAndDeleteRoom())
                .setNegativeButton("Tidak", null)
                .show();
    }
}
