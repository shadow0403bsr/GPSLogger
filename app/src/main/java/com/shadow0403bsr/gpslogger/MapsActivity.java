package com.shadow0403bsr.gpslogger;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng chandigarh = new LatLng(30.75511041d, 76.78201742d);
        this.mMap.addMarker(new MarkerOptions().position(chandigarh).title("Marker in Chandigarh"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chandigarh, 14.0f));
        Log.i("INFO", "onMapReady executed");
    }
}
