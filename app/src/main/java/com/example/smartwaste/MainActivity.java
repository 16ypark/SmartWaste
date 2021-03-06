package com.example.smartwaste;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ted.gun0912.clustering.clustering.TedClusterItem;
import ted.gun0912.clustering.naver.TedNaverClustering;

public class MainActivity<NMapLocationManager> extends AppCompatActivity
        implements MainFragment.OnNewButtonTappedListener, AddFragment.OnApproveButtonTappedListener, AddFragment.OnBackButtonTappedListener,
        OnMapReadyCallback,LocationListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String TAG = "MainActivity";
    private MapView mapView;
    private FragmentManager fragmentManager;
    private MainFragment fragmentMain;
    private AddFragment fragmentAdd;
    private FragmentTransaction transaction;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private NMapLocationManager mMapLocationManager;
    private LocationManager locationManager;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private DatabaseReference mDatabase;
    private Marker currentLocationMarker;
    private boolean isCreatingNewBin = false;
    private HashMap<LatLng, JavaItem> normalBinsAggregated = new HashMap<>();
          
    private TedNaverClustering<TedClusterItem> normalCluster;
    private TedNaverClustering<TedClusterItem> publicCluster;
    private TedNaverClustering<TedClusterItem> largeCluster;

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;
    private Call<GeocodeResultVO> mCallGeocodeResult;

    private InfoWindow infoWindow;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fragmentManager = getSupportFragmentManager();
        fragmentMain = new MainFragment();
        fragmentAdd = new AddFragment();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentMain).commitAllowingStateLoss();
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setRetrofitInit();
        makeInfoWindow();
        prepareSearchView();
    }

    private void prepareSearchView() {
        mSearchView = findViewById(R.id.searchView);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callGeocodeResult(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void makeInfoWindow() {
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(getApplicationContext()) {
            @NonNull
            @Override
            protected View getContentView(@NonNull InfoWindow infoWindow) {
                View view = View.inflate(MainActivity.this, R.layout.view_info_window, null);
                return view;
            }
        });
        infoWindow.setOnClickListener(new Overlay.OnClickListener()
        {
            @Override
            public boolean onClick(@NonNull Overlay overlay)
            {
                onDeleteButtonTapped();
                return false;
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        mapView.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hasPermission()) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 10, (android.location.LocationListener) this);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this, PERMISSIONS,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null) {
            locationManager.removeUpdates((android.location.LocationListener) this);
        }
    }

    public void onLocationChanged(Location location) {
        if (naverMap == null || location == null) {
            return;
        }

        // add_fragment 도중에는 카메라 업데이트가 일어나지 않도록 수정
        LatLng coord = new LatLng(location);
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(coord);
        locationOverlay.setBearing(location.getBearing());

        if (isCreatingNewBin) {
            return;
        }

        naverMap.moveCamera(CameraUpdate.scrollTo(coord));

    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    private boolean hasPermission() {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0])
                == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, PERMISSIONS[1])
                == PermissionChecker.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onNewButtonTapped() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentAdd).commitAllowingStateLoss();
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(false);
        currentLocationMarker = new Marker();
        currentLocationMarker.setPosition(locationOverlay.getPosition());
        currentLocationMarker.setIcon(OverlayImage.fromResource(R.drawable.bin));
        currentLocationMarker.setMap(naverMap);

        isCreatingNewBin = true;
        infoWindow.close();

        naverMap.moveCamera(CameraUpdate.scrollTo(locationOverlay.getPosition())
            .animate(CameraAnimation.Easing, 3000));
    }

    public void onDeleteButtonTapped() {
        LatLng latLng = infoWindow.getPosition();
        JavaItem javaItem = normalBinsAggregated.get(latLng);
        mDatabase.child("bins").child("normal").child(javaItem.getKey()).removeValue();
        normalCluster.removeItem(javaItem);
        normalBinsAggregated.remove(latLng);
        infoWindow.close();
        Toast.makeText(MainActivity.this, R.string.report_received, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        Log.d( TAG, "onMapReady");
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true); // 기본값 : false
        uiSettings.setLogoGravity(Gravity.RIGHT|Gravity.BOTTOM);
        ActivityCompat.requestPermissions(this, PERMISSIONS, (Integer) LOCATION_PERMISSION_REQUEST_CODE);
        naverMap.addOnCameraChangeListener((reason, animated) -> {
            Log.i("NaverMap", "카메라 변경 - reason: " + reason + ", animated: " + animated);
            if (isCreatingNewBin) {
                currentLocationMarker.setIcon(OverlayImage.fromResource(R.drawable.bin));
                currentLocationMarker.setPosition(new LatLng(naverMap.getCameraPosition().target.latitude, naverMap.getCameraPosition().target.longitude));

            }
        });

        readBin(new ReadBinCallback() {
            /*@Override
            public void onReadNormalBin(HashMap<LatLng, JavaItem> normalBins) {
                normalCluster = TedNaverClustering.with(MainActivity.this, naverMap)
                        .customMarker(javaItem -> {
                            LatLng latLng = new LatLng(javaItem.getTedLatLng().getLatitude(),
                                    javaItem.getTedLatLng().getLongitude());
                            Marker marker = new Marker(latLng);
                            marker.setIcon((OverlayImage.fromResource(R.drawable.bin)));
                            return marker;
                        })
                        .items(new ArrayList<JavaItem>(normalBins.values()))
                        .markerClickListener(javaItem -> {
                            infoWindow.setPosition(new LatLng(javaItem.getTedLatLng().getLatitude(),
                                    javaItem.getTedLatLng().getLongitude()));
                            infoWindow.open(naverMap);

                            return null;
                        })
                        .make();
                addChildListener();
            }*/

            @Override
            public void onReadPublicBin(ArrayList<JavaItem> publicBinArray) {
                publicCluster = TedNaverClustering.with(MainActivity.this, naverMap)
                        .customMarker(javaItem -> {
                            Marker marker = new Marker(new LatLng(javaItem.getTedLatLng().getLatitude(),
                                    javaItem.getTedLatLng().getLongitude()));
                            marker.setIcon((OverlayImage.fromResource(R.drawable.public_bin)));
                            return marker;
                        })
                        .items(publicBinArray)
                        .make();
            }

            @Override
            public void onReadLargeBin(ArrayList<JavaItem> largeBinArray) {
                largeCluster = TedNaverClustering.with(MainActivity.this, naverMap)
                        .customMarker(javaItem -> {
                            Marker marker = new Marker(new LatLng(javaItem.getTedLatLng().getLatitude(),
                                    javaItem.getTedLatLng().getLongitude()));
                            marker.setIcon((OverlayImage.fromResource(R.drawable.big_bin)));
                            return marker;
                        })
                        .items(largeBinArray)
                        .make();
            }
        });
    }

    @Override
    public void onApproveButtonTapped() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentMain).commitAllowingStateLoss();
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        isCreatingNewBin = false;
        currentLocationMarker.setMap(null);
        naverMap.moveCamera(CameraUpdate.scrollTo(locationOverlay.getPosition())
            .animate(CameraAnimation.Easing, 3000));
        writeNewBin(new Bin(currentLocationMarker.getPosition(),
                new HashSet<BinType>(Arrays.asList(BinType.NORMAL, BinType.RECYCLE))));
    }

    @Override
    public void onBackButtonTapped() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentMain).commitAllowingStateLoss();

        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        isCreatingNewBin = false;
        currentLocationMarker.setMap(null);

    }

    private void writeNewBin(Bin bin) {
        mDatabase.child("bins").child("normal").push().setValue(bin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        Toast.makeText(MainActivity.this, R.string.save_complete, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readBin(ReadBinCallback callback) {
        addChildListener();
        /*mDatabase.child("bins").child("normal").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Get Post object and use the values to update the UI
               HashMap<LatLng, JavaItem> normalBinMap = new HashMap<>();
               for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                   if (!(postSnapshot.hasChild("lat") && postSnapshot.hasChild("lng"))) {
                       continue;
                   }
                   double lat = postSnapshot.child("lat").getValue(Double.class);
                   double lng = postSnapshot.child("lng").getValue(Double.class);

                   LatLng latLngNormal = new LatLng(lat, lng);
                   normalBinMap.put(latLngNormal, new JavaItem(latLngNormal, postSnapshot.getKey()));
                   normalBinsAggregated.put(latLngNormal, new JavaItem(latLngNormal, postSnapshot.getKey()));
               }
               HashMap<LatLng, JavaItem> temp = new HashMap<>(normalBinMap);
               callback.onReadNormalBin(normalBinMap);
           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
       });*/

                mDatabase.child("bins").child("public").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        ArrayList<JavaItem> publicBinArray = new ArrayList<JavaItem>();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (!(postSnapshot.hasChild("lat") && postSnapshot.hasChild("lng"))) {
                                continue;
                            }
                            double lat = postSnapshot.child("lat").getValue(Double.class);
                            double lng = postSnapshot.child("lng").getValue(Double.class);

                            LatLng latLngPublic = new LatLng(lat, lng);
                            publicBinArray.add(new JavaItem(latLngPublic, postSnapshot.getKey()));
                        }
                        callback.onReadPublicBin(publicBinArray);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
                    }
                });

        mDatabase.child("bins").child("large").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                ArrayList<JavaItem> largeBinArray = new ArrayList<JavaItem>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (!(postSnapshot.hasChild("lat") && postSnapshot.hasChild("lng"))) {
                        continue;
                    }
                    double lat = postSnapshot.child("lat").getValue(Double.class);
                    double lng = postSnapshot.child("lng").getValue(Double.class);

                    LatLng latLngLarge = new LatLng(lat, lng);
                    largeBinArray.add(new JavaItem(latLngLarge, postSnapshot.getKey()));
                }
                callback.onReadLargeBin(largeBinArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void addChildListener() {
        mDatabase.child("bins").child("normal").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Toast.makeText(MainActivity.this, "더합니다.", Toast.LENGTH_SHORT);
                if (snapshot.hasChild("lat") && snapshot.hasChild("lng")) {
                    double lat = snapshot.child("lat").getValue(Double.class);
                    double lng = snapshot.child("lng").getValue(Double.class);

                    LatLng latLngNormal = new LatLng(lat, lng);
                    JavaItem bin = new JavaItem(latLngNormal, snapshot.getKey());
                    if (normalCluster != null) {
                        normalCluster.addItem(bin);
                    } else {
                        normalCluster = TedNaverClustering.with(MainActivity.this, naverMap)
                                .customMarker(javaItem -> {
                                    LatLng latLng = new LatLng(javaItem.getTedLatLng().getLatitude(),
                                            javaItem.getTedLatLng().getLongitude());
                                    Marker marker = new Marker(latLng);
                                    marker.setIcon((OverlayImage.fromResource(R.drawable.bin)));
                                    return marker;
                                })
                                .markerClickListener(javaItem -> {
                                    infoWindow.setPosition(new LatLng(javaItem.getTedLatLng().getLatitude(),
                                            javaItem.getTedLatLng().getLongitude()));
                                    infoWindow.open(naverMap);

                                    return null;
                                })
                                .item(bin)
                                .make();
                    }
                    normalBinsAggregated.put(latLngNormal, bin);
                }
                return;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                return;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                /*
                Toast.makeText(MainActivity.this, "지웁니다.", Toast.LENGTH_SHORT);
                if (snapshot.hasChild("lat") && snapshot.hasChild("lng")) {
                    double lat = snapshot.child("lat").getValue(Double.class);
                    double lng = snapshot.child("lng").getValue(Double.class);

                    LatLng latLngNormal = new LatLng(lat, lng);
                    JavaItem javaItem = new JavaItem(latLngNormal, snapshot.getKey());
                    normalCluster.removeItem(javaItem);
                    normalBinsAggregated.remove(latLngNormal);
                }

                 */
                return;
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
    }

    private void setRetrofitInit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com/")
                 .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);
    }

    private void callGeocodeResult(String query) {
        mCallGeocodeResult = mRetrofitAPI.getGeocodeResult(query);
        mCallGeocodeResult.enqueue(new Callback<GeocodeResultVO>() {
            @Override
            public void onResponse(Call<GeocodeResultVO> call, Response<GeocodeResultVO> response) {
                if (response.isSuccessful()) {

                    GeocodeResultVO result = response.body();
                    GeocodeResultVO.AddressVO[] addresses = result.getAddress();
                    if (addresses != null && addresses.length > 0) {
                        GeocodeResultVO.AddressVO address = addresses[0];
                        LatLng moveLatLng = new LatLng(Double.parseDouble(address.getY()), Double.parseDouble(address.getX()));
                        naverMap.moveCamera(CameraUpdate.toCameraPosition(new CameraPosition(moveLatLng, 16)));
                    } else {
                        Toast.makeText(MainActivity.this, "주소가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("onResponse error", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<GeocodeResultVO> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



}



