package com.polytechnic.astra.ac.id.smartglowapp.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.polytechnic.astra.ac.id.smartglowapp.Fragment.LoginFragment;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            LoginFragment fragmentLogin = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_login, fragmentLogin)
                    .commit();
        }

        //Clear Field edittext email dan password
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment loginFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_login);
                if (loginFragment instanceof LoginFragment) {
                    ((LoginFragment) loginFragment).clearFields();
                }
            }
        });
    }
}
