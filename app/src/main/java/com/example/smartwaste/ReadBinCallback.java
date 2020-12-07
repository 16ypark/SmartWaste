package com.example.smartwaste;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public interface ReadBinCallback {
    void onReadNormalBin(HashMap<LatLng, JavaItem> normalBinArray);
    void onReadPublicBin(ArrayList<JavaItem> publicBinArray);
    void onReadLargeBin(ArrayList<JavaItem> largeBinArray);
}
