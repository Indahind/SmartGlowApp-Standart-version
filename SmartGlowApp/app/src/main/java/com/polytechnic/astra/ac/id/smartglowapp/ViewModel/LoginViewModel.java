package com.polytechnic.astra.ac.id.smartglowapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.polytechnic.astra.ac.id.smartglowapp.API.Repository.MyRepository;
import com.polytechnic.astra.ac.id.smartglowapp.Model.User;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<User> loggedInUser = new MutableLiveData<>();
    private MutableLiveData<String> loginError = new MutableLiveData<>();
    private MyRepository repository;

    public LoginViewModel() {
        repository = MyRepository.get();
    }

    public void setLoggedInUser(User user) {
        loggedInUser.setValue(user);
    }

    public LiveData<User> getLoggedInUser() {
        return loggedInUser;
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public void loginUser(String username, String password) {
        repository.loginUser(username, password, new MyRepository.LoginCallback() {
            @Override
            public void onUserLoggedIn(User user) {
                loggedInUser.setValue(user);
            }

            @Override
            public void onLoginError(String error) {
                loginError.setValue(error);
            }
        });
    }
}