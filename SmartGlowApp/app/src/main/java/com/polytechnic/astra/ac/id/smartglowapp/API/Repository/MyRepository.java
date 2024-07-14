package com.polytechnic.astra.ac.id.smartglowapp.API.Repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polytechnic.astra.ac.id.smartglowapp.API.ApiUtils;
import com.polytechnic.astra.ac.id.smartglowapp.API.Service.MyService;
import com.polytechnic.astra.ac.id.smartglowapp.API.VO.MyVO;
import com.polytechnic.astra.ac.id.smartglowapp.Model.MyModel;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRepository {
    private static final String TAG = "MyRepository";
    private static MyRepository INSTANCE;
    private MyService mMyService;
    private DatabaseReference mDatabaseRef;

    private MyRepository(Context context){
        FirebaseApp.initializeApp(context);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://projectled-46877-default-rtdb.asia-southeast1.firebasedatabase.app")
                .setProjectId("projectled-46877")
                .setApiKey("AIzaSyB34XHs_jTseAWmhwL2WvwBdEzmBf16yT4")
                .build();
        FirebaseApp.initializeApp(context, options, "Secondary");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mMyService = ApiUtils.getMyService();
    }

    public static void initialize(Context context){
        if (INSTANCE == null){
            INSTANCE = new MyRepository(context);
        }
    }

    public static MyRepository get(){
        return INSTANCE;
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        DatabaseReference usersRef = mDatabaseRef.child("users");
        usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    if (user.getPassword().equals(password)) {
                                        callback.onUserLoggedIn(user);
                                        return;
                                    } else {
                                        callback.onLoginError("Incorrect password");
                                        return;
                                    }
                                }
                            }
                        }
                        callback.onLoginError("User not found");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onLoginError("Database error: " + error.getMessage());
                    }
                });
    }

    public interface LoginCallback {
        void onUserLoggedIn(User user);
        void onLoginError(String error);
    }

    public MutableLiveData<List<MyModel>> getMyListModel(String apiKey, String category){
        MutableLiveData<List<MyModel>> data = new MutableLiveData<>();
        Call<List<MyModel>> call = mMyService.getMyModel(apiKey, category);
        call.enqueue(new Callback<List<MyModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MyModel>> call, @NonNull Response<List<MyModel>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                    Log.d(TAG, "Data size: " + data.getValue().size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MyModel>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error API call: " + t.getMessage());
            }
        });
        return data;
    }

    public MutableLiveData<List<MyVO>> getMyListVO(){
        MutableLiveData<List<MyVO>> data = new MutableLiveData<>();
        Call<List<MyVO>> call = mMyService.getMyVO(); // Adjust API call as needed
        call.enqueue(new Callback<List<MyVO>>() {
            @Override
            public void onResponse(@NonNull Call<List<MyVO>> call, @NonNull Response<List<MyVO>> response) {
                if (response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MyVO>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error API call: " + t.getMessage());
            }
        });
        return data;
    }
}