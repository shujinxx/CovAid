package com.ganshujin.androidrealtimelocation;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.ganshujin.androidrealtimelocation.Model.MyLocation;
import com.ganshujin.androidrealtimelocation.Utils.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap mMap;
    DatabaseReference trackingUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        registerEventRealtime();
    }

    private void registerEventRealtime() {
        trackingUserLocation = FirebaseDatabase.getInstance()
                .getReference(Common.PUBLIC_LOCATION)
                .child(Common.trackingUser.getUid());

        trackingUserLocation.addValueEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackingUserLocation.addValueEventListener(this);
    }

    @Override
    protected void onStop() {
        trackingUserLocation.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //For zoom ui
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Map skin


    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue() !=null)
        {
            MyLocation location = dataSnapshot.getValue(MyLocation.class);

            LatLng userMarker = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userMarker)
            .title(Common.trackingUser.getEmail())
            .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.getTime()))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker,16f));
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
