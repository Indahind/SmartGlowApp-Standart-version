package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.polytechnic.astra.ac.id.smartglowapp.Adapter.HouseAdapter;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.HomeViewModel;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LoginViewModel;

import java.util.List;

public class HomeFragment extends Fragment {

    private ListView houseListView;
    private HouseAdapter houseAdapter;
    private Button addButton;
    private TextView userWelcome;
    private HomeViewModel homeViewModel;
    private LoginViewModel loginViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_bro, container, false);

        houseListView = view.findViewById(R.id.house_list);
        userWelcome = view.findViewById(R.id.user_welcome);
        addButton = view.findViewById(R.id.add_house_button);

        loginViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    String userId = user.getUserId();
                    String userName = user.getNama();

                    userWelcome.setText("Welcome, " + userName);

                    homeViewModel.loadHouses(userId);

                    homeViewModel.getHouses().observe(getViewLifecycleOwner(), new Observer<List<Rumah>>() {
                        @Override
                        public void onChanged(List<Rumah> houses) {
                            if (houses != null && !houses.isEmpty()) {
                                houseAdapter = new HouseAdapter(requireContext(), houses, userId, userName);
                                houseListView.setAdapter(houseAdapter);
                            } else {
                                Toast.makeText(requireContext(), "Tidak ada data rumah ditemukan.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loginViewModel.setLoggedInUser(user);
                            AddEditHomeFragment fragment = new AddEditHomeFragment();
                            Bundle args = new Bundle();
                            args.putString("creadby", userId);
                            args.putString("owner", userName);
                            fragment.setArguments(args);

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_home, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                }
            }
        });
        return view;
    }
}
