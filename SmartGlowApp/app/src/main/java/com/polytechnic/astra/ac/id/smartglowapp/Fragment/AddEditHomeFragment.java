package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LoginViewModel;

public class AddEditHomeFragment extends Fragment {

    private EditText editTextName, editTextAlamat;
    private Button buttonSave;
    private DatabaseReference databaseUsers;
    private String userId;
    private String creadby;
    private LoginViewModel loginViewModel;

    public AddEditHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_edit_rumah, container, false);

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
                Log.d("AddEditHomeFragment", "Creadby: " + creadby);
            } else {
                Toast.makeText(requireContext(), "Creadby is null or empty", Toast.LENGTH_SHORT).show();
                Log.e("AddEditHomeFragment", "Creadby is null or empty");
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
                        Log.e("AddEditHomeFragment", "Failed to load user: " + databaseError.getMessage());
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "Arguments are null", Toast.LENGTH_SHORT).show();
            Log.e("AddEditHomeFragment", "Arguments are null");
        }

        // Initialize Save Button
        buttonSave.setOnClickListener(v -> saveUser());

        return view;
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String alamat = editTextAlamat.getText().toString().trim();

        if (!TextUtils.isEmpty(name)) {
            if (userId == null) {
                databaseUsers.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        long userCount = dataSnapshot.getChildrenCount();
                        String newUserId = String.valueOf(userCount + 1);
                        String bro = "rumah_id_" + newUserId;

                        if (creadby == null) {
                            creadby = "Unknown"; // Handle null value for creadby
                        }

                        Rumah user = new Rumah(bro, name, alamat, "Aktif", creadby);
                        databaseUsers.child(bro).setValue(user, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    Toast.makeText(requireContext(), "User saved", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    Toast.makeText(requireContext(), "Failed to save user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to get user count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Rumah user = new Rumah(userId, name, alamat, "Aktif", creadby);
                databaseUsers.child(userId).setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(requireContext(), "User updated", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update user: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
        }
    }

}
