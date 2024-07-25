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
import com.polytechnic.astra.ac.id.smartglowapp.Model.Lampu;

import java.util.ArrayList;
import java.util.List;

public class LampuViewModel extends ViewModel {
    private MutableLiveData<List<Lampu>> lamps;
    private MutableLiveData<String> errorMessage;
    private DatabaseReference databaseRooms;

    public LampuViewModel() {
        lamps = new MutableLiveData<>(new ArrayList<>());
        errorMessage = new MutableLiveData<>();
        databaseRooms = FirebaseDatabase.getInstance().getReference("smart_home/perangkat");
    }

    public LiveData<List<Lampu>> getLamps() {
        return lamps;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadLams(String ruanganId) {
        Query query = databaseRooms.orderByChild("ruanganId").equalTo(ruanganId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Lampu> roomList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Lampu room = postSnapshot.getValue(Lampu.class);
                    if (room != null) {
                        roomList.add(room);
                    }
                }
                lamps.setValue(roomList);
                if (roomList.isEmpty()) {
                    errorMessage.setValue("No lamps found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorMessage.setValue("Database error: " + databaseError.getMessage());
            }
        });
    }
}
