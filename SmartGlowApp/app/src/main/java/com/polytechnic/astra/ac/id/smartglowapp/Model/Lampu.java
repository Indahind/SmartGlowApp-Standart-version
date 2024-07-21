package com.polytechnic.astra.ac.id.smartglowapp.Model;

import java.io.Serializable;

public class Lampu implements Serializable {
    private String lampuId;
    private String ruanganId;
    private String nama;
    private String status;
    private String status_lampu;
    private String creadby;
    private Integer red;
    private Integer green;
    private Integer blue;
    private Integer pin_awal;
    private Integer pin_akhir;

    public Lampu() {
    }

    public Lampu(String lampuId, String ruanganId, String nama, String status, String status_lampu, String creadby, Integer red, Integer green, Integer blue, Integer pin_awal, Integer pin_akhir) {
        this.lampuId = lampuId;
        this.ruanganId = ruanganId;
        this.nama = nama;
        this.status = status;
        this.status_lampu = status_lampu;
        this.creadby = creadby;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.pin_awal = pin_awal;
        this.pin_akhir = pin_akhir;
    }

    public String getLampuId() {
        return lampuId;
    }

    public void setLampuId(String lampuId) {
        this.lampuId = lampuId;
    }

    public String getRuanganId() {
        return ruanganId;
    }

    public void setRuanganId(String ruanganId) {
        this.ruanganId = ruanganId;
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

    public String getStatus_lampu() {
        return status_lampu;
    }

    public void setStatus_lampu(String status_lampu) {
        this.status_lampu = status_lampu;
    }

    public String getCreadby() {
        return creadby;
    }

    public void setCreadby(String creadby) {
        this.creadby = creadby;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public Integer getGreen() {
        return green;
    }

    public void setGreen(Integer green) {
        this.green = green;
    }

    public Integer getBlue() {
        return blue;
    }

    public void setBlue(Integer blue) {
        this.blue = blue;
    }

    public Integer getPin_awal() {
        return pin_awal;
    }

    public void setPin_awal(Integer pin_awal) {
        this.pin_awal = pin_awal;
    }

    public Integer getPin_akhir() {
        return pin_akhir;
    }

    public void setPin_akhir(Integer pin_akhir) {
        this.pin_akhir = pin_akhir;
    }
}
