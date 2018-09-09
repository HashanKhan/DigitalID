package com.example.asus.nfc_qr_app.entities;

public class User {
    private String name;
    private String regno;
    private String nic;
    private String photo;
    private int sync_status;

    public User(String name, String regno, String nic, String photo, int sync_status){
        this.name = name;
        this.regno = regno;
        this.nic = nic;
        this.photo = photo;
        this.sync_status = sync_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
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
