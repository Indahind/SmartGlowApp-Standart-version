package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LoginViewModel;

public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton, donthaveaccount;
    private DatabaseReference database;
    private LoginViewModel loginViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_activity_new, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        donthaveaccount = view.findViewById(R.id.registerButton);

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("smart_home").child("users");

        // Initialize LoginViewModel
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {
                    loginUser(username, password);
                } else {
                    Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        donthaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddEditUserFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_addUser, new AddUserFragment());
                fragmentTransaction.addToBackStack(null); // Optional: add to back stack
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void loginUser(String username, String password) {
        database.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null && user.getStatus().equals("Aktif")) {
                                    if (user.getPassword().equals(password)) {
                                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                                        // Save logged-in user information to ViewModel
                                        loginViewModel.setLoggedInUser(user);

                                        // Navigate to HomeFragment
                                        FragmentManager fragmentManager = getParentFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        HomeFragment homeFragment = new HomeFragment();
                                        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                                        fragmentTransaction.addToBackStack(null);  // Optional: Add to back stack if needed
                                        fragmentTransaction.commit();

                                        clearFields(); // Clear fields after successful login
                                    } else {
                                        Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
    }
}