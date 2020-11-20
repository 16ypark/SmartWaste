package com.example.smartwaste;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
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
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MainActivity<NMapLocationManager> extends AppCompatActivity
        implements MainFragment.OnNewButtonTappedListener, MainFragment.OnNewButtonTappedListener2, AddFragment.OnApproveButtonTappedListener, AddFragment.OnBackButtonTappedListener,
        OnMapReadyCallback,LocationListener,Overlay.OnClickListener {
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
    private ArrayList<Marker> normalBinMarkerArray = new ArrayList<Marker>();
    private InfoWindow infoWindow;

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
        readBin();
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
        currentLocationMarker.setMap(naverMap);
        isCreatingNewBin = true;
        infoWindow.close();

        naverMap.moveCamera(CameraUpdate.scrollTo(locationOverlay.getPosition())
            .animate(CameraAnimation.Easing, 200));
    }
    @Override
    public void onNewButtonTapped2() {
        infoWindow.close();
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(false);
        Button button =findViewById(R.id.button_delete);
        if(button.getVisibility()==View.VISIBLE){
            button.setVisibility(View.INVISIBLE);      }
///we have to delete marker

        naverMap.moveCamera(CameraUpdate.scrollTo(locationOverlay.getPosition())
                .animate(CameraAnimation.Easing, 200));
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
                    currentLocationMarker.setPosition(new LatLng(naverMap.getCameraPosition().target.latitude, naverMap.getCameraPosition().target.longitude));
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
            .animate(CameraAnimation.Easing, 1000));

        writeNewBin(new Bin(currentLocationMarker.getPosition(), BinType.NORMAL));
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
                        Toast.makeText(MainActivity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(MainActivity.this, "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readBin() {
        mDatabase.child("bins").child("normal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    double lat = postSnapshot.child("lat").getValue(Double.class);
                    double lng = postSnapshot.child("lng").getValue(Double.class);
                    String binType = postSnapshot.child("binType").getValue(String.class);
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(lat, lng));
                    Log.w("FireBaseData", "lat" + lat);
                    Log.w("FireBaseData", "lng" + lng);
                    marker.setMap(naverMap);
                    normalBinMarkerArray.add(marker);
                    infoWindow = new InfoWindow();
                    infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(getApplicationContext()) {
                        @NonNull
                        @Override
                        protected View getContentView(@NonNull InfoWindow infoWindow) {
                            Marker marker = infoWindow.getMarker();
                            View view = View.inflate(MainActivity.this, R.layout.view_info_window, null);
                            return view;
                        }
                    });
                     Overlay.OnClickListener listener = overlay -> {
                         if (overlay instanceof Marker) {
                              Button button =findViewById(R.id.button_delete);
                              if(button.getVisibility()==View.VISIBLE){
                                  button.setVisibility(View.INVISIBLE);
                                  infoWindow.close();
                              }else{
                                button.setVisibility(View.VISIBLE);
                                infoWindow.open(marker); }
                              return true;
                        }
                        return false;
                    };
                    marker.setOnClickListener(listener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });

        mDatabase.child("bins").child("public").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FireBaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
            return false;
        }
}



