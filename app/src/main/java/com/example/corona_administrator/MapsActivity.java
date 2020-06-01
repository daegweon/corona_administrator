package com.example.corona_administrator;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geocoder;
    private String quarantine_address;
    private String current_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();

        this.quarantine_address = intent.getExtras().getString("quarantine_address");
        this.current_address = "대전광역시 유성구 구성동 146-4"; // 임시로 하드코딩. 실제로는 데이터 받아와야


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geocoder = new Geocoder(this);


        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(this.quarantine_address,10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Address> current_addressList = null;
        try {
            current_addressList = geocoder.getFromLocationName(this.current_address,10);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 좌표(위도, 경도) 생성
        LatLng quarantine_point = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

        // 좌표(위도, 경도) 생성
        LatLng current_point = new LatLng(current_addressList.get(0).getLatitude(), current_addressList.get(0).getLongitude());

//        MarkerOptions mOptions1 = new MarkerOptions().position(quarantine_point).title("격리주소").icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        //MarkerOptions mOptions1 = new MarkerOptions().position(quarantine_point).title("격리주소");
//        mOptions1.snippet(this.quarantine_address);
//        mMap.addMarker(mOptions1);
//
//        MarkerOptions mOptions2 = new MarkerOptions().position(current_point).title("현재위치");
//        mOptions2.snippet(this.current_address);
//        mMap.addMarker(mOptions2).showInfoWindow();


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_point, 15));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//        //the include method will calculate the min and max bound.
//        builder.include(mOptions1.getPosition());
//        builder.include(mOptions2.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }
}
