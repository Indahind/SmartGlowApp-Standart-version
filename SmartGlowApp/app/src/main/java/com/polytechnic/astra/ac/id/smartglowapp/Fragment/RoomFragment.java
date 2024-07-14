package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Adapter.RoomAdapter;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {

    private ListView houseListView;
    private RoomAdapter houseAdapter;
    private RoomViewModel mRoomViewModel;

    private List<Ruangan> ruanganList;
    private Button addButton;
    private DatabaseReference databaseUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_room_list, container, false);

        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");

        houseListView = view.findViewById(R.id.room_list);
        ruanganList = new ArrayList<>();
        addButton = view.findViewById(R.id.add_room_button);

        // Initialize RoomViewModel
        mRoomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String houseId = args.getString("house_id");
            String creadby = args.getString("creadby");
            String houseName = args.getString("house_name");
            String houseAddress = args.getString("house_address");
            String owner = args.getString("owner");

            // Set data to views in fragment
            TextView txtHouseName = view.findViewById(R.id.txt_house_name);
            TextView txtHouseAddress = view.findViewById(R.id.txt_house_address);
            TextView txtOwner = view.findViewById(R.id.txt_owner);

            txtHouseName.setText(houseName);
            txtHouseAddress.setText(houseAddress);
            txtOwner.setText(owner);

            // Load rooms using RoomViewModel
            mRoomViewModel.loadRooms(houseId);

            // Observe the LiveData from RoomViewModel
            mRoomViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<Ruangan>>() {
                @Override
                public void onChanged(List<Ruangan> rooms) {
                    if (rooms != null && !rooms.isEmpty()) {
                        houseAdapter = new RoomAdapter(requireContext(), rooms, creadby);
                        houseListView.setAdapter(houseAdapter);
                    } else {
                        Toast.makeText(requireContext(), "Tidak ada data ruangan ditemukan.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mRoomViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            // Query to filter rooms based on houseId
            Query query = databaseUsers.orderByChild("house_id").equalTo(houseId);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ruanganList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Ruangan ruangan = postSnapshot.getValue(Ruangan.class);
                        ruanganList.add(ruangan);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddEditRuanganFragment fragment = new AddEditRuanganFragment();
                    Bundle args = new Bundle();
                    args.putString("userId", creadby);
                    args.putString("house_id", houseId);
                    fragment.setArguments(args);

                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_addRoom, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        return view;
    }
}
