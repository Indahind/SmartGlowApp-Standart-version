package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class UpdateUserFragment extends Fragment {

    private EditText editTextName, editTextEmail, editTextPhone, editTextUsername, editTextPassword;
    private Button buttonSave, buttonDelete;
    private DatabaseReference databaseHouses;

    private User user;

    private static final String TAG = "EditUserFragment";

    public UpdateUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("users");
            System.out.println(user.getNama());
        }

        // Initialize Firebase Database reference
        databaseHouses = FirebaseDatabase.getInstance().getReference("smart_home/users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_user, container, false);

        // Initialize EditText fields and Save Button
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        if (user != null) {
            editTextName.setText(user.getNama());
            editTextEmail.setText(user.getEmail());
            editTextPhone.setText(user.getNoTelpon());
            editTextUsername.setText(user.getUsername());
            editTextPassword.setText(user.getPassword());
        }

        // Save button click listener
        buttonSave.setOnClickListener(v -> confirmUpdate());

        buttonDelete.setOnClickListener(v -> confirmDelete());

        return view;
    }


    private void updateHouse() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity(), "Please enter a valid email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\d{13,}")) {
            Toast.makeText(getActivity(), "Please enter a valid phone number with at least 13 digits.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null ) {

            user.setNama(name);
            user.setEmail(email);
            user.setNoTelpon(phone);
            user.setUsername(username);
            user.setPassword(password);
            user.setStatus("Aktif");

            databaseHouses.child(user.getUserId()).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update User: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "User data is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        LoginFragment fragment = new LoginFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void markHouseAsDeleted() {
        if (user != null) {
            user.setStatus("Tidak Aktif");

            databaseHouses.child(user.getUserId()).setValue(user, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        Toast.makeText(requireContext(), "User marked as deleted successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                        navigateToLogin();
                    } else {
                        Toast.makeText(requireContext(), "Failed to mark User as deleted: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "User data is null", Toast.LENGTH_SHORT).show();
        }
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
}
