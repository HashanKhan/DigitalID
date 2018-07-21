package com.example.asus.nfc_qr_app.entities;

import java.sql.Blob;

public class User {
    private String regno;
    private String name;
    private String nic;
    private String photo;
    private int sync_status;

    public User(String regno, String name, String nic, String photo, int sync_status){
        this.setRegno(regno);
        this.setName(name);
        this.setNic(nic);
        this.setPhoto(photo);
        this.setSync_status(sync_status);
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
