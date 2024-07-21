package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.HomeViewModel;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HouseAdapter houseAdapter;
    private RecyclerView houseRecyclerView;
    private HomeViewModel homeViewModel;
    private LoginViewModel loginViewModel;
    private TextView userWelcome;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btn_add_house) {
            navigateToAddHouse();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        setHasOptionsMenu(true);

        // Load initial house data
        loginViewModel.getLoggedInUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    String userId = user.getUserId();
                    homeViewModel.loadHouses(userId);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        houseRecyclerView = view.findViewById(R.id.house_list);
        houseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        userWelcome = view.findViewById(R.id.user_welcome);

        houseAdapter = new HouseAdapter();
        houseRecyclerView.setAdapter(houseAdapter);

        loginViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    String userName = user.getNama();
                    userWelcome.setText("Welcome, " + userName);

                    homeViewModel.getHouses().observe(getViewLifecycleOwner(), new Observer<List<Rumah>>() {
                        @Override
                        public void onChanged(List<Rumah> houses) {
                            if (houses != null) {
                                houseAdapter.setHouseList(houses);
                            } else {
                                houseAdapter.setHouseList(new ArrayList<>());
                                Toast.makeText(requireContext(), "No houses found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

    private void navigateToAddHouse() {
        loginViewModel.getLoggedInUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    AddHomeFragment fragment = new AddHomeFragment();
                    Bundle args = new Bundle();
                    args.putString("creadby", user.getUserId());
                    args.putString("owner", user.getNama());
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    private void navigateToRoomFragment(Rumah house) {
        RoomFragment roomFragment = new RoomFragment();
        Bundle args = new Bundle();
        args.putSerializable("rumah", house);
        roomFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, roomFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToUpdateHouse(Rumah house) {
        UpdateHomeFragment fragment = new UpdateHomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("rumah", house);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseHolder> {

        private List<Rumah> houseList = new ArrayList<>();
        private List<Rumah> activeHouseList = new ArrayList<>();

        @NonNull
        @Override
        public HouseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_list, parent, false);
            return new HouseHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HouseHolder holder, int position) {
            //Rumah house = houseList.get(position);
            Rumah houseActive = activeHouseList.get(position);
            holder.bind(houseActive);
        }

        @Override
        public int getItemCount() {
            //return houseList.size();
            return activeHouseList.size();
        }

        public void setHouseList(List<Rumah> houseList) {
            this.houseList.clear();
            this.houseList.addAll(houseList);
            //this.activeHouseList.clear();
            //this.activeHouseList.addAll(houseList);
            filterActiveHouses();
            notifyDataSetChanged();
        }

        private void filterActiveHouses() {
            activeHouseList.clear();
            for (Rumah house : houseList) {
                if ("Aktif".equals(house.getStatus())) {
                    activeHouseList.add(house);
                }
            }
        }

        public class HouseHolder extends RecyclerView.ViewHolder {

            private TextView houseName, houseAddress;
            private ImageView showDetails;
            private LinearLayout editHouse;

            public HouseHolder(@NonNull View itemView) {
                super(itemView);
                houseName = itemView.findViewById(R.id.txtData1);
                houseAddress = itemView.findViewById(R.id.txtDataDetail1);
                showDetails = itemView.findViewById(R.id.btn_show);
                editHouse = itemView.findViewById(R.id.editHouse);


                showDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Rumah rumah = houseList.get(getAdapterPosition());
                        Rumah rumah = activeHouseList.get(getAdapterPosition());
                        navigateToRoomFragment(rumah);
                    }
                });

                editHouse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Rumah rumah = houseList.get(getAdapterPosition());
                        Rumah rumah = activeHouseList.get(getAdapterPosition());
                        navigateToUpdateHouse(rumah);
                    }
                });
            }

            public void bind(Rumah house) {
                houseName.setText(house.getNama());
                houseAddress.setText(house.getAlamat_rumah());
            }
        }

    }

}
