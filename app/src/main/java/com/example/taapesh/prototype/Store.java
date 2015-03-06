package com.example.taapesh.prototype;

public class Store {
    private String name;
    private String address;
    private String phoneNumber;

    // Store constructor
    public Store(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name= name;
    }
    public String getName() {
        return name;
    }
    public void setAddress(String address) {
        this.address= address;
    }
    public String getAddress() {
        return address;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
