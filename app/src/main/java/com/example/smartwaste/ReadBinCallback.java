package com.example.smartwaste;

import java.util.ArrayList;

public interface ReadBinCallback {
    void onReadNormalBin(ArrayList<JavaItem> normalBinArray);
    void onReadPublicBin(ArrayList<JavaItem> publicBinArray);
    void onReadLargeBin(ArrayList<JavaItem> largeBinArray);
}
