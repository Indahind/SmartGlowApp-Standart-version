package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class AddHomeFragment extends Fragment {

    private EditText editTextName, editTextAlamat;
    private Button buttonSave;
    private DatabaseReference databaseUsers;
    private String userId;
    private String creadby;

    public AddHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/rumah");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_house, container, false);

        // Initialize Firebase Database reference
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/rumah");

        // Initialize EditText
        editTextName = view.findViewById(R.id.editTextName);
        editTextAlamat = view.findViewById(R.id.editAlamatAja);
        buttonSave = view.findViewById(R.id.buttonSave);

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

        // Initialize Save Button
        buttonSave.setOnClickListener(v -> confirmSave());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void saveHome() {
        String name = editTextName.getText().toString().trim();
        String alamat = editTextAlamat.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(alamat)) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

                Rumah user = new Rumah(newUserId, name, alamat, "Aktif", creadby);
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
