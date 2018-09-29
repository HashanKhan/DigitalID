package com.example.asus.student_app.entities;

public class User {
    private String name;
    private String regno;
    private String nic;
    private String photo;
    private String username;
    private String password;
    private int sync_status;

    public User(String name, String regno, String nic, String photo, String username, String password, int sync_status){
        this.name = name;
        this.regno = regno;
        this.nic = nic;
        this.photo = photo;
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSync_status() {
        return sync_status;
    }

    public void setSync_status(int sync_status) {
        this.sync_status = sync_status;
    }
}
