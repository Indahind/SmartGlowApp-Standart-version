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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Lampu;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class AddLampujFragment extends Fragment {

    private EditText editTextLampuName;
    private EditText editTextLampuSerial;
    private EditText editTextLampuPIN;
    private Button buttonSave;
    private DatabaseReference databaseRooms;
    private String roomId;
    private String lampuId;
    private String houseId;
    private String userId; // Assuming you have houseId passed from previous fragment

    public AddLampujFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/lampu");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_edit_perangkat, container, false);

        // Initialize Firebase Database reference
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/lampu");

        // Initialize EditText
        editTextLampuName = view.findViewById(R.id.editTextName);
        editTextLampuSerial = view.findViewById(R.id.editSerialNumber);
        editTextLampuPIN = view.findViewById(R.id.editJumlahPin);
        buttonSave = view.findViewById(R.id.buttonSave);

        Bundle arguments = getArguments();
        if (arguments != null) {
            Ruangan rumah = (Ruangan) arguments.getSerializable("rumah");

            houseId = rumah.getRumahId();
            System.out.println(rumah.getRumahId());
            userId = rumah.getCreadby();
            System.out.println(userId);
            roomId = rumah.getRuanganId();
            System.out.println(roomId);

            if (lampuId != null) {
                // Load existing room data if editing
                databaseRooms.child(lampuId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Lampu room = dataSnapshot.getValue(Lampu.class);
                        if (room != null) {
                            editTextLampuName.setText(room.getNama());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to load Lampu.", Toast.LENGTH_SHORT).show();
                        Log.e("AddEditLampuFragment", "Failed to load Lampu: " + databaseError.getMessage());
                    }
                });
            }
        }

        // Initialize Save Button
        //buttonSave.setOnClickListener(v -> saveRoom());

        return view;
    }

//    private void saveRoom() {
//        String roomName = editTextRoomName.getText().toString().trim();
//
//        if (!TextUtils.isEmpty(roomName)) {
//            if (roomId == null) {
//                databaseRooms.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        long roomCount = dataSnapshot.getChildrenCount();
//                        String newRoomId = "ruangan_id_" + (roomCount + 1);
//
//                        Ruangan room = new Ruangan(newRoomId, houseId, roomName, "Aktif", userId);
//                        databaseRooms.child(newRoomId).setValue(room, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                if (databaseError == null) {
//                                    Toast.makeText(requireContext(), "Room saved", Toast.LENGTH_SHORT).show();
//                                    requireActivity().getSupportFragmentManager().popBackStack();
//                                } else {
//                                    Toast.makeText(requireContext(), "Failed to save room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(requireContext(), "Failed to get room count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Ruangan room = new Ruangan(roomId, houseId, roomName, "1", userId);
//                databaseRooms.child(roomId).setValue(room, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                        if (databaseError == null) {
//                            Toast.makeText(requireContext(), "Room updated", Toast.LENGTH_SHORT).show();
//                            requireActivity().getSupportFragmentManager().popBackStack();
//                        } else {
//                            Toast.makeText(requireContext(), "Failed to update room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        } else {
//            Toast.makeText(requireContext(), "Please enter a room name", Toast.LENGTH_SHORT).show();
//        }
//    }
}
