package com.polytechnic.astra.ac.id.smartglowapp.Model;

public class Rumah {
    private String rumahId;
    private String nama;
    private String alamat_rumah;
    private String status;
    private String creadby;

    public Rumah(String rumahId, String nama, String alamat_rumah, String status, String creadby) {
        this.rumahId = rumahId;
        this.nama = nama;
        this.alamat_rumah = alamat_rumah;
        this.status = status;
        this.creadby = creadby;
    }

    public Rumah() {
    }

    public String getRumahId() {
        return rumahId;
    }

    public void setRumahId(String rumahId) {
        this.rumahId = rumahId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat_rumah() {
        return alamat_rumah;
    }

    public void setAlamat_rumah(String alamat_rumah) {
        this.alamat_rumah = alamat_rumah;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreadby() {
        return creadby;
    }

    public void setCreadby(String creadby) {
        this.creadby = creadby;
    }
}
