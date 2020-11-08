package com.example.smartwaste;

import com.naver.maps.geometry.LatLng;

public class Bin {

    public Double lat;
    public Double lng;
    public BinType binType;

    public Bin() {
        // Default constructor required for calls to DataSnapshot.getValue(Bin.class)
    }

    public Bin(LatLng binLocation, BinType binType) {
        this.lat = binLocation.latitude;
        this.lng = binLocation.longitude;
        this.binType = binType;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lat) {
        this.lng = lng;
    }

    public String getBinType() {
        return binType.name();
    }

    public void setBinType(BinType binType) {
        this.binType = binType;
    }

    @Override
    public String toString() {
        return "Bin{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", binType='" + binType.name() + '\'' +
                '}';
    }
}

enum BinType {

    NORMAL, GLASS, CAN, PLASTIC
}