package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class AddUserFragment extends Fragment {

    private EditText editTextName, editTextEmail, editTextPhone, editTextUsername, editTextPassword;
    private Button buttonSave;
    private DatabaseReference databaseUsers;
    private String userId;

    public static AddUserFragment newInstance(String userId) {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database reference
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        // Initialize EditText
        editTextName = view.findViewById(R.id.editTextName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);
        buttonSave = view.findViewById(R.id.buttonSave);

        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
            if (userId != null) {
                // Load existing user data if editing
                databaseUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            editTextName.setText(user.getNama());
                            editTextEmail.setText(user.getEmail());
                            editTextPhone.setText(user.getNoTelpon());
                            editTextUsername.setText(user.getUsername());
                            editTextPassword.setText(user.getPassword());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Failed to load user.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        buttonSave.setOnClickListener(v -> saveUser());

        return view;
    }

    private void saveUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("\\d{13,}")) {
                Toast.makeText(getActivity(), "Please enter a valid phone number with at least 13 digits.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == null) {
                // Check if username already exists
                databaseUsers.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getActivity(), "Username already exist.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Username does not exist, proceed to save the new user
                            databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    long userCount = dataSnapshot.getChildrenCount();
                                    String newUserId = String.valueOf(userCount + 1); // Generate ID based on user count + 1
                                    String bro = "user_id_" + newUserId;

                                    User user = new User(bro, name, email, phone, username, "Aktif", password);
                                    // Save user to Firebase Database
                                    databaseUsers.child(bro).setValue(user).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "User saved", Toast.LENGTH_SHORT).show();
                                            // Close fragment after saving
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to save user.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getActivity(), "Failed to get user count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Failed to check username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                User user = new User(userId, name, email, phone, username, "Aktif", password);
                // Update user in Firebase Database
                databaseUsers.child(userId).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "User updated", Toast.LENGTH_SHORT).show();
                        // Close fragment after saving
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), "Failed to update user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Please fill in all field.", Toast.LENGTH_SHORT).show();
        }
    }
}
