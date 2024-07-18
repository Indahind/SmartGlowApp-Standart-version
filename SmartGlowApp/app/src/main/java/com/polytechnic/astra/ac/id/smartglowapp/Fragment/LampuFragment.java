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

public class LampuFragment extends Fragment {

    private ListView lampuListView;
    private RecyclerView lampuRecyclerView;
    private RoomAdapter lampuAdapter;
    private LampuViewModel mLampuViewModel;
    private RoomViewModel mRoomViewModel;
    private TextView txtHouseName, txtHouseAddress;
    private List<Lampu> lampuList;

    public LampuFragment() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_room, menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.btn_add_room) {
//            navigateToAddRoom();
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRoomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        mLampuViewModel = new ViewModelProvider(this).get(LampuViewModel.class);
        setHasOptionsMenu(true);

        // Load rooms using RoomViewModel
        Bundle args = getArguments();
        if (args != null) {
            Ruangan rumah = (Ruangan) args.getSerializable("ruangan");
            if (rumah != null) {
                String houseId = rumah.getRuanganId();
                mLampuViewModel.loadRooms(houseId);
            } else {
                    Toast.makeText(requireContext(), "Tidak ada data ruangan ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_perangkat_list, container, false);

        lampuRecyclerView = view.findViewById(R.id.room_list);
        lampuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lampuAdapter = new RoomAdapter();
        lampuRecyclerView.setAdapter(lampuAdapter);

        txtHouseName = view.findViewById(R.id.txt_house_name);
        txtHouseAddress = view.findViewById(R.id.txt_house_address);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Ruangan rumah = (Ruangan) args.getSerializable("ruangan");
            mRoomViewModel.setRooms(rumah);
            if (rumah != null) {
                String houseName = rumah.getNama();
                String houseAddress = rumah.getRuanganId();

                txtHouseName.setText(houseName);
                txtHouseAddress.setText(houseAddress);

                mLampuViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<Lampu>>() {
                    @Override
                    public void onChanged(List<Lampu> rooms) {
                        if (rooms != null) {
                            lampuAdapter.setRoomsList(rooms);
                        } else {
                            lampuAdapter.setRoomsList(new ArrayList<>());
                            Toast.makeText(requireContext(), "No rooms found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        return view;
    }
//
//    private void navigateToAddRoom() {
//        mHomeViewModel.getHouse().observe(getViewLifecycleOwner(), new Observer<Rumah>() {
//            @Override
//            public void onChanged(Rumah rumahDipilih) {
//                System.out.println(rumahDipilih.getAlamat_rumah()+"inii");
//                System.out.println(rumahDipilih.getRumahId()+"inii");
//                System.out.println(rumahDipilih.getCreadby()+"inii");
//                System.out.println(rumahDipilih.getNama()+"inii");
//                if (rumahDipilih != null) {
//                    AddRoomFragment fragment = new AddRoomFragment();
//                    Bundle args = new Bundle();
//                    args.putSerializable("rumah", rumahDipilih);
//                    fragment.setArguments(args);
//
//                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_home, fragment);  // Pastikan ID container benar
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                }
//            }
//        });
//    }
//
//    private void navigateToUpdateRoom(Ruangan ruangan) {
//        UpdateRoomFragment fragment = new UpdateRoomFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("ruangan", ruangan);
//        fragment.setArguments(args);
//
//        getParentFragmentManager().beginTransaction()
//                .replace(R.id.fragment_home, fragment)
//                .addToBackStack(null)
//                .commit();
//    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

        private List<Lampu> activeRoomList = new ArrayList<>();
        private List<Lampu> roomList = new ArrayList<>();

        @NonNull
        @Override
        public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lampu_list, parent, false);
            return new RoomHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomHolder holder, int position) {
            Lampu houseActive = activeRoomList.get(position);
            holder.bind(houseActive);
        }

        @Override
        public int getItemCount() {
            return activeRoomList.size();
        }

        public void setRoomsList(List<Lampu> houseList) {
            this.roomList.clear();
            this.roomList.addAll(houseList);
            filterActiveHouses();
            notifyDataSetChanged();
        }

        private void filterActiveHouses() {
            activeRoomList.clear();
            for (Lampu house : roomList) {
                if ("Aktif".equals(house.getStatus())) {
                    activeRoomList.add(house);
                }
            }
        }


        public class RoomHolder extends RecyclerView.ViewHolder {

            private TextView roomName, roomId;
            private ImageView showDetails;
            private LinearLayout editRoom;

            public RoomHolder(@NonNull View itemView) {
                super(itemView);
//                roomId = itemView.findViewById(R.id.txtDataDetail1);
//                roomName = itemView.findViewById(R.id.txtData1);
//                editRoom = itemView.findViewById(R.id.editRoom);
//                showDetails = itemView.findViewById(R.id.btn_show);

//                showDetails.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Lampu rumah = activeRoomList.get(getAdapterPosition());
//                        //navigateToRoomFragment(rumah);
//                    }
//                });
//
//                editRoom.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Lampu rumah = activeRoomList.get(getAdapterPosition());
//                        navigateToUpdateRoom(rumah);
//                    }
//                });

            }
                public void bind(Lampu house) {
                    roomName.setText(house.getNama());
                    roomId.setText(house.getRuanganId());
                }
        }
    }

}
