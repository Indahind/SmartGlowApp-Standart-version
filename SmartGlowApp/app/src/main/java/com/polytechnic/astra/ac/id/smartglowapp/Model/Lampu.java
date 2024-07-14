package com.polytechnic.astra.ac.id.smartglowapp.Model;

public class Lampu {
    private String lampuId;
    private String ruanganId;
    private String nama;
    private String status;
    private String creadby;
    private Integer red;
    private Integer green;
    private Integer blue;

    public Lampu(String lampuId, String ruanganId, String nama, String status, String creadby, Integer red, Integer green, Integer blue, String serial_number, Integer pin) {
        this.lampuId = lampuId;
        this.ruanganId = ruanganId;
        this.nama = nama;
        this.status = status;
        this.creadby = creadby;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.serial_number = serial_number;
        this.pin = pin;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    private String serial_number;


    public Lampu(String lampuId, String ruanganId, String nama, String status, String creadby, Integer red, Integer green, Integer blue, Integer pin) {
        this.lampuId = lampuId;
        this.ruanganId = ruanganId;
        this.nama = nama;
        this.status = status;
        this.creadby = creadby;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.pin = pin;
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

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    private Integer pin;


    public Lampu(String lampuId, String ruanganId, String nama, String status, String creadby) {
        this.lampuId = lampuId;
        this.ruanganId = ruanganId;
        this.nama = nama;
        this.status = status;
        this.creadby = creadby;
    }

    public Lampu() {
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

    public String getCreadby() {
        return creadby;
    }

    public void setCreadby(String creadby) {
        this.creadby = creadby;
    }
}
