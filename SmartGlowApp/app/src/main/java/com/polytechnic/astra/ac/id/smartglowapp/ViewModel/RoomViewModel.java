package com.polytechnic.astra.ac.id.smartglowapp.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.Model.Ruangan;

import java.util.ArrayList;
import java.util.List;

public class RoomViewModel extends ViewModel {
    private MutableLiveData<List<Ruangan>> rooms;
    private MutableLiveData<String> errorMessage;
    private DatabaseReference databaseRooms;

    public RoomViewModel() {
        rooms = new MutableLiveData<>(new ArrayList<>());
        errorMessage = new MutableLiveData<>();
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/ruangan");
    }

    public LiveData<List<Ruangan>> getRooms() {
        return rooms;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadRooms(String houseId) {
        Query query = databaseRooms.orderByChild("rumahId").equalTo(houseId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ruangan> roomList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Ruangan room = postSnapshot.getValue(Ruangan.class);
                    if (room != null) {
                        roomList.add(room);
                        System.out.println(room.getNama()+room.getCreadby()+room.getRuanganId()+room.getRumahId());
                    }
                }
                rooms.setValue(roomList);
                if (roomList.isEmpty()) {
                    errorMessage.setValue("No rooms found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorMessage.setValue("Database error: " + databaseError.getMessage());
            }
        });
    }
}
