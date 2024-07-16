package com.polytechnic.astra.ac.id.smartglowapp.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String nama;
    private String email;
    private String noTelpon;
    private String password; // tambahkan atribut password
    private String status;   // tambahkan atribut status
    private String username; // tambahkan atribut username

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String nama, String email, String noTelpon) {
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.noTelpon = noTelpon;
    }

    public User(String userId, String nama, String email, String noTelpon, String password, String status, String username) {
        this.userId = userId;
        this.nama = nama;
        this.email = email;
        this.noTelpon = noTelpon;
        this.password = password;
        this.status = status;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoTelpon() {
        return noTelpon;
    }

    public void setNoTelpon(String noTelpon) {
        this.noTelpon = noTelpon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
