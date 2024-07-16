package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class UpdateRoomFragment extends Fragment {

    private EditText editTextName;
    private Button buttonSave, buttonDelete;
    private DatabaseReference databaseHouses;
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
        databaseHouses = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");
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
        buttonSave.setOnClickListener(v -> updateRoom());

        buttonDelete.setOnClickListener(v -> markRoomAsDeleted());

        return view;
    }

    private void updateRoom() {
        String name = editTextName.getText().toString().trim();

        if (room != null) {
            room.setNama(name);

            databaseHouses.child(room.getRuanganId()).setValue(room, new DatabaseReference.CompletionListener() {
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

    private void markRoomAsDeleted() {
        if (room != null) {
            room.setStatus("Tidak Aktif");

            databaseHouses.child(room.getRuanganId()).setValue(room, new DatabaseReference.CompletionListener() {
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
}
