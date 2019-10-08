package com.shadow0403bsr.gpslogger;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        String csv_data = getIntent().getStringExtra("csv_content");
        String[] csv_rows = csv_data.split(";");
        int length = csv_rows.length;
        LatLng chandigarh = new LatLng(30.75511041d, 76.78201742d);
        for(int row_num = 1; row_num < length; row_num++){
            String[] data = csv_rows[row_num].split(",");
            LatLng pointer = new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
            if(row_num == 1){
                chandigarh = new LatLng(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
                this.mMap.addMarker(new MarkerOptions().position(pointer).title("Marker " + row_num).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
            else if(row_num == length-1){
                this.mMap.addMarker(new MarkerOptions().position(pointer).title("Marker " + row_num).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            else{
                this.mMap.addMarker(new MarkerOptions().position(pointer).title("Marker " + row_num).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chandigarh, 14.0f));
        Log.i("INFO", "onMapReady executed");
    }
}
