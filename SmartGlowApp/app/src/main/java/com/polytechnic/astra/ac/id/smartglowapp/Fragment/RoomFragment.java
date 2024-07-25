package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.polytechnic.astra.ac.id.smartglowapp.Model.Lampu;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.HomeViewModel;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LampuViewModel;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class RoomFragment extends Fragment {

    private ListView roomListView;
    private RecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;
    private RoomViewModel mRoomViewModel;
    private HomeViewModel mHomeViewModel;
    private LampuViewModel mLampuViewModel;
    private TextView txtHouseName, txtHouseAddress, txtOwner;
    private List<Ruangan> ruanganList;
    private String userName, houseName, houseAddress;

    public RoomFragment() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_room_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btn_add_room) {
            navigateToAddRoom();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mLampuViewModel = new ViewModelProvider(this).get(LampuViewModel.class);
        setHasOptionsMenu(true);

        // Load rooms using RoomViewModel
        Bundle args = getArguments();
        if (args != null) {
            Rumah rumah = (Rumah) args.getSerializable("rumah");
            if (rumah != null) {
                String houseId = rumah.getRumahId();
                mRoomViewModel.loadRooms(houseId);
            } else {
                    Toast.makeText(requireContext(), "Tidak ada data ruangan ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        roomRecyclerView = view.findViewById(R.id.room_list);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        roomAdapter = new RoomAdapter();
        roomRecyclerView.setAdapter(roomAdapter);

        txtHouseName = view.findViewById(R.id.txt_room_name);
        txtHouseAddress = view.findViewById(R.id.txt_house_name);
        txtOwner = view.findViewById(R.id.txt_owner);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Rumah rumah = (Rumah) args.getSerializable("rumah");
            mHomeViewModel.setHouses(rumah);
            if (rumah != null) {
                houseName = rumah.getNama();
                houseAddress = rumah.getAlamat_rumah();
                userName = (String) args.getString("owner");

                txtHouseName.setText(houseName);
                txtHouseAddress.setText(houseAddress);
                txtOwner.setText(userName);

                mRoomViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<Ruangan>>() {
                    @Override
                    public void onChanged(List<Ruangan> rooms) {
                        if (rooms != null) {
                            roomAdapter.setRoomsList(rooms);
                        } else {
                            roomAdapter.setRoomsList(new ArrayList<>());
                            Toast.makeText(requireContext(), "No rooms found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mLampuViewModel.getLamps().observe(getViewLifecycleOwner(), new Observer<List<Lampu>>() {
                    @Override
                    public void onChanged(List<Lampu> lamps) {
                        if (lamps != null) {
                            roomAdapter.setLampsList(lamps);
                        }
                    }
                });

            }
        }

        return view;
    }

    private void navigateToAddRoom() {
        mHomeViewModel.getHouse().observe(getViewLifecycleOwner(), new Observer<Rumah>() {
            @Override
            public void onChanged(Rumah rumahDipilih) {
                System.out.println(rumahDipilih.getAlamat_rumah()+"inii");
                System.out.println(rumahDipilih.getRumahId()+"inii");
                System.out.println(rumahDipilih.getCreadby()+"inii");
                System.out.println(rumahDipilih.getNama()+"inii");
                if (rumahDipilih != null) {
                    AddRoomFragment fragment = new AddRoomFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("rumah", rumahDipilih);
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);  // Pastikan ID container benar
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    private void navigateToUpdateRoom(Ruangan ruangan) {
        UpdateRoomFragment fragment = new UpdateRoomFragment();
        Bundle args = new Bundle();
        args.putSerializable("ruangan", ruangan);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToLampuFragment(Ruangan house) {
        LampuFragment roomFragment = new LampuFragment();
        Bundle args = new Bundle();
        args.putSerializable("ruangan", house);
        args.putString("owner",userName);
        args.putString("houseName",houseName);
        roomFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, roomFragment)
                .addToBackStack(null)
                .commit();
        System.out.println("INI MAU KE LAMPU");
    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

        private List<Ruangan> activeRoomList = new ArrayList<>();
        private List<Ruangan> roomList = new ArrayList<>();
        private List<Lampu> lampList = new ArrayList<>();
        private List<Lampu> activeLampList = new ArrayList<>();

        @NonNull
        @Override
        public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_room_list, parent, false);
            return new RoomHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomHolder holder, int position) {
            Ruangan roomActive = activeRoomList.get(position);
            holder.bind(roomActive);
        }

        @Override
        public int getItemCount() {
            return activeRoomList.size();
        }

        public void setRoomsList(List<Ruangan> houseList) {
            this.roomList.clear();
            this.roomList.addAll(houseList);
            filterActiveRooms();
            notifyDataSetChanged();
        }

        public void setLampsList(List<Lampu> lamps) {
            this.lampList.clear();
            this.lampList.addAll(lamps);
            filterActiveLamps();
            notifyDataSetChanged();
        }

        private void filterActiveRooms() {
            activeRoomList.clear();
            for (Ruangan house : roomList) {
                if ("Aktif".equals(house.getStatus())) {
                    activeRoomList.add(house);
                }
            }
        }

        private void filterActiveLamps() {
            activeLampList.clear();
            for (Lampu lampu : lampList) {
                if ("Aktif".equals(lampu.getStatus())) {
                    activeLampList.add(lampu);
                }
            }
        }

        private int getLampCountForRoom(String roomId) {
            int count = 0;
            for (Lampu lamp : activeLampList) {
                if (lamp.getRuanganId().equals(roomId)) {
                    count++;
                }
            }
            System.out.println("Room ID: " + roomId + " has " + count + " active lamps.");
            return count;
        }

        public class RoomHolder extends RecyclerView.ViewHolder {

            private TextView roomName, lampCount;
            private ImageView showDetails;
            private LinearLayout editRoom;

            public RoomHolder(@NonNull View itemView) {
                super(itemView);
                lampCount = itemView.findViewById(R.id.txtDataDetail1);
                roomName = itemView.findViewById(R.id.txtData1);
                editRoom = itemView.findViewById(R.id.editRoom);
                showDetails = itemView.findViewById(R.id.btn_show);

                showDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Ruangan rumah = activeRoomList.get(getAdapterPosition());
                        navigateToLampuFragment(rumah);
                    }
                });

                editRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Ruangan ruangan = activeRoomList.get(getAdapterPosition());
                        navigateToUpdateRoom(ruangan);
                    }
                });

            }
                public void bind(Ruangan ruangan) {
                    roomName.setText(ruangan.getNama());
                    System.out.println(ruangan.getRuanganId());
                    //lampCount.setText("There are "+String.valueOf(getLampCountForRoom(ruangan.getRuanganId())+" lamp."));
                }
        }
    }

}
