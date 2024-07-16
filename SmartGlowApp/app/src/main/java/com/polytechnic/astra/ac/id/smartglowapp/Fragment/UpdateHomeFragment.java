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
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class UpdateHomeFragment extends Fragment {

    private EditText editTextName, editTextAlamat;
    private Button buttonSave, buttonDelete;
    private DatabaseReference databaseHouses;
    private Rumah rumah;

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

        if (rumah != null) {
            editTextName.setText(rumah.getNama());
            editTextAlamat.setText(rumah.getAlamat_rumah());
        }

        // Save button click listener
        buttonSave.setOnClickListener(v -> updateHouse());

        buttonDelete.setOnClickListener(v -> markHouseAsDeleted());

        return view;
    }

    private void updateHouse() {
        String name = editTextName.getText().toString().trim();
        String alamat = editTextAlamat.getText().toString().trim();

        if (rumah != null) {
            rumah.setNama(name);
            rumah.setAlamat_rumah(alamat);

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
        } else {
            Toast.makeText(requireContext(), "House data is null", Toast.LENGTH_SHORT).show();
        }
    }
}
