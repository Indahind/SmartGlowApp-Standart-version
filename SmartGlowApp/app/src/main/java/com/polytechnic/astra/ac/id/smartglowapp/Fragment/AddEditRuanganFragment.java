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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;
import com.polytechnic.astra.ac.id.smartglowapp.R;

public class AddEditRuanganFragment extends Fragment {

    private EditText editTextRoomName;
    private Button buttonSave;
    private DatabaseReference databaseRooms;
    private String roomId;
    private String houseId;
    private String userId; // Assuming you have houseId passed from previous fragment

    public AddEditRuanganFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_edit_ruangan, container, false);

        // Initialize Firebase Database reference
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");

        // Initialize EditText
        editTextRoomName = view.findViewById(R.id.editTextName);
        buttonSave = view.findViewById(R.id.buttonSave);

        Bundle arguments = getArguments();
        if (arguments != null) {
            roomId = arguments.getString("room_id");
            houseId = arguments.getString("house_id");
            System.out.println(houseId);
            userId = arguments.getString("userId");
            System.out.println(userId);

            if (roomId != null) {
                // Load existing room data if editing
                databaseRooms.child(roomId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Ruangan room = dataSnapshot.getValue(Ruangan.class);
                        if (room != null) {
                            editTextRoomName.setText(room.getNama());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to load room.", Toast.LENGTH_SHORT).show();
                        Log.e("AddEditRuanganFragment", "Failed to load room: " + databaseError.getMessage());
                    }
                });
            }
        }

        // Initialize Save Button
        buttonSave.setOnClickListener(v -> saveRoom());

        return view;
    }

    private void saveRoom() {
        String roomName = editTextRoomName.getText().toString().trim();

        if (!TextUtils.isEmpty(roomName)) {
            if (roomId == null) {
                databaseRooms.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        long roomCount = dataSnapshot.getChildrenCount();
                        String newRoomId = String.valueOf(roomCount + 1);
                        String roomId = "room_id_" + newRoomId;

                        Ruangan room = new Ruangan(roomId,houseId, roomName, "1",userId );
                        databaseRooms.child(roomId).setValue(room, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    System.out.println("indah disini");
                                    Toast.makeText(requireContext(), "Room saved", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                } else {
                                    Toast.makeText(requireContext(), "Failed to save room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to get room count: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Ruangan room = new Ruangan(roomId,houseId, roomName, "1",userId);
                databaseRooms.child(roomId).setValue(room, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(requireContext(), "Room updated", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Toast.makeText(requireContext(), "Please enter a room name", Toast.LENGTH_SHORT).show();
        }
    }
}
