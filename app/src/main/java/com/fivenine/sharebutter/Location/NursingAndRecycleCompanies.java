package com.fivenine.sharebutter.Location;

public class NursingAndRecycleCompanies {

    String name;
    String address;
    String phone;
    String type;
    Double latitude;
    Double longitude;

    public NursingAndRecycleCompanies() {
    }

    public String getName() {
        return name;
    }

    public NursingAndRecycleCompanies setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public NursingAndRecycleCompanies setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public NursingAndRecycleCompanies setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getType() {
        return type;
    }

    public NursingAndRecycleCompanies setType(String type) {
        this.type = type;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public NursingAndRecycleCompanies setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public NursingAndRecycleCompanies setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public String toString() {
        return "NursingAndRecycleCompanies{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", type='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
