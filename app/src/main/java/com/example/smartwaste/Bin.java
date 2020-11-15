package com.example.smartwaste;

import com.naver.maps.geometry.LatLng;

import java.util.HashSet;

public class Bin {

    public Double lat;
    public Double lng;
    public HashSet<BinType> binType;
    public String binTypeString;

    public Bin() {
        // Default constructor required for calls to DataSnapshot.getValue(Bin.class)
    }

    public Bin(LatLng binLocation, HashSet<BinType> binType) {
        this.lat = binLocation.latitude;
        this.lng = binLocation.longitude;
        this.binType = binType;
        this.binTypeString = toBinTypeString();
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
        return binTypeString;
    }

    public void setBinType(HashSet<BinType> binType) {
        this.binType = binType;
    }

    @Override
    public String toString() {
        return "Bin{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", binType='" + binTypeString + '\'' +
                '}';
    }

    public String toBinTypeString() {
        String binTypeString = "";
        for(BinType b : binType) {
            binTypeString += b.name();
            binTypeString += " ";
        }
        return binTypeString;
    }
}

enum BinType {

    NORMAL, RECYCLE, LARGE
}