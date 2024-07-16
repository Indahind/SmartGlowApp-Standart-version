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
import com.polytechnic.astra.ac.id.smartglowapp.Model.Rumah;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Rumah>> houses;
    private MutableLiveData<Rumah> roominHouses = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage;
    private DatabaseReference databaseUsers;

    public HomeViewModel() {
        houses = new MutableLiveData<>(new ArrayList<>());
        errorMessage = new MutableLiveData<>();
        databaseUsers = FirebaseDatabase.getInstance().getReference("smart_home/rumah");
    }

    public void setHouses(Rumah houses) {
        roominHouses.setValue(houses);
    }

    public LiveData<Rumah> getHouse(){
        return roominHouses;
    }

    public LiveData<List<Rumah>> getHouses() {
        return houses;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadHouses(String userId) {
        Query query = databaseUsers.orderByChild("creadby").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Rumah> houseList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rumah rumah = postSnapshot.getValue(Rumah.class);
                    if (rumah != null) {
                        houseList.add(rumah);
                    }
                }
                houses.setValue(houseList);
                if (houseList.isEmpty()) {
                    errorMessage.setValue("No houses found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                errorMessage.setValue("Database error: " + databaseError.getMessage());
            }
        });
    }

}