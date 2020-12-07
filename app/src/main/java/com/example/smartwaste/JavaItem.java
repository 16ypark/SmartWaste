package com.example.smartwaste;

import org.jetbrains.annotations.NotNull;
import com.naver.maps.geometry.LatLng;

import ted.gun0912.clustering.clustering.TedClusterItem;
import ted.gun0912.clustering.geometry.TedLatLng;

public class JavaItem implements TedClusterItem {

    private LatLng latLng;
    private String key;

    public JavaItem(LatLng latLng, String key) {
        this.latLng = latLng;
        this.key = key;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getKey() { return key; }

    @NotNull
    @Override
    public TedLatLng getTedLatLng() {
        return new TedLatLng(latLng.latitude, latLng.longitude);
    }
}