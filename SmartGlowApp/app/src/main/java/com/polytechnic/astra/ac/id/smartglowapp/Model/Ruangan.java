package com.polytechnic.astra.ac.id.smartglowapp.Model;

import java.io.Serializable;

public class Ruangan implements Serializable {
    private String ruanganId;
    private String rumahId;
    private String nama;
    private String status;
    private String creadby;

    public Ruangan(String ruanganId, String rumahId, String nama, String status, String creadby) {
        this.ruanganId = ruanganId;
        this.rumahId = rumahId;
        this.nama = nama;
        this.status = status;
        this.creadby = creadby;
    }

    public Ruangan() {
    }

    public String getRuanganId() {
        return ruanganId;
    }

    public void setRuanganId(String ruanganId) {
        this.ruanganId = ruanganId;
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
