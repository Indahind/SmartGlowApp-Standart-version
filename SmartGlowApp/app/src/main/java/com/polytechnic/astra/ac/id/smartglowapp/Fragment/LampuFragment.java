package com.polytechnic.astra.ac.id.smartglowapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Lampu;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.R;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.LampuViewModel;
import com.polytechnic.astra.ac.id.smartglowapp.ViewModel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class LampuFragment extends Fragment {

    private ListView lampuListView;
    private RecyclerView lampuRecyclerView;
    private LampuAdapter lampuAdapter;
    private LampuViewModel mLampuViewModel;
    private RoomViewModel mRoomViewModel;
    private TextView txtRoomName, txtHouseName, txt_owner;
    private List<Lampu> lampuList;

    public LampuFragment() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_device_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btn_add_device) {
            navigateToAddLampu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

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
                mLampuViewModel.loadLams(houseId);
            } else {
                Toast.makeText(requireContext(), "Tidak ada data lampu ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perangkat_list, container, false);

        lampuRecyclerView = view.findViewById(R.id.device_list);
        lampuRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lampuAdapter = new LampuAdapter();
        lampuRecyclerView.setAdapter(lampuAdapter);

        txt_owner = view.findViewById(R.id.txt_owner);
        txtRoomName = view.findViewById(R.id.txt_room_name);
        txtHouseName = view.findViewById(R.id.txt_house_name);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Ruangan ruangan = (Ruangan) args.getSerializable("ruangan");
            String owner = (String) args.getString("owner");
            String houseName = (String) args.getString("houseName");

            mRoomViewModel.setRooms(ruangan);
            if (ruangan != null) {
                String roomName = ruangan.getNama();

                txt_owner.setText(owner);
                txtRoomName.setText(roomName);
                txtHouseName.setText(houseName);

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
    private void navigateToAddLampu() {
        mRoomViewModel.getRoom().observe(getViewLifecycleOwner(), new Observer<Ruangan>() {
            @Override
            public void onChanged(Ruangan rumahDipilih) {
                System.out.println(rumahDipilih.getRumahId()+"inii");
                System.out.println(rumahDipilih.getCreadby()+"inii");
                System.out.println(rumahDipilih.getNama()+"inii");
                if (rumahDipilih != null) {
                    AddLampuFragment fragment = new AddLampuFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("ruangan", rumahDipilih);
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }
    //
    private void navigateToUpdateLampu(Lampu lampu) {
        UpdateLampuFragment fragment = new UpdateLampuFragment();
        Bundle args = new Bundle();
        args.putSerializable("perangkat", lampu);
        fragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private class LampuAdapter extends RecyclerView.Adapter<LampuAdapter.LampHolder> {

        private List<Lampu> activeLampList = new ArrayList<>();
        private List<Lampu> lampList = new ArrayList<>();
        private DatabaseReference databaseReference;

        public LampuAdapter() {
            this.databaseReference = FirebaseDatabase.getInstance().getReference("smart_home/perangkat");
        }

        @NonNull
        @Override
        public LampHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lampu_list, parent, false);
            return new LampHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LampHolder holder, int position) {
            Lampu lampActive = activeLampList.get(position);
            holder.bind(lampActive);
        }

        @Override
        public int getItemCount() {
            return activeLampList.size();
        }

        public void setRoomsList(List<Lampu> houseList) {
            this.lampList.clear();
            this.lampList.addAll(houseList);
            filterActiveLamps();
            notifyDataSetChanged();
        }

        private void filterActiveLamps() {
            activeLampList.clear();
            for (Lampu house : lampList) {
                if ("Aktif".equals(house.getStatus())) {
                    activeLampList.add(house);
                }
            }
        }

        public class LampHolder extends RecyclerView.ViewHolder {

            private ImageView imgDevice;
            private TextView txtData;
            private TextView txtDataDetail;
            private Switch btnDevice;
            private LinearLayout editRoom;

            public LampHolder(@NonNull View itemView) {
                super(itemView);
                editRoom = itemView.findViewById(R.id.editLampu);

                imgDevice = itemView.findViewById(R.id.imgDevice1);
                txtData = itemView.findViewById(R.id.txtData1);
                txtDataDetail = itemView.findViewById(R.id.txtDataDetail1);
                btnDevice = itemView.findViewById(R.id.switchbutton);

                editRoom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Lampu rumah = activeLampList.get(getAdapterPosition());
                        navigateToUpdateLampu(rumah);
                    }
                });
            }

            public void bind(Lampu lampu) {
                txtData.setText(lampu.getNama());
                txtDataDetail.setText(lampu.getStatus_lampu());
                btnDevice.setChecked(lampu.getStatus_lampu().equals("on"));

                btnDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String status = isChecked ? "on" : "off";
                        lampu.setStatus_lampu(status);
                        databaseReference.child(lampu.getLampuId()).child("status_lampu").setValue(status);
                    }
                });
            }
        }
    }

}
