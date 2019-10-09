package com.shadow0403bsr.gpslogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private boolean ifLogging;
    public TextView lat;
    double latitude;
    public TextView lng;
    private LocationListener locationListener;
    private LocationManager locationManager;
    double longitude;
    public TextView spd;
    double speed;
    public FileWriter writer;

    public void functionStart(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        }
        Context context = getApplicationContext();
        if (this.ifLogging) {
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "GPS Logging already in progress!", duration);
            toast.setGravity(16, 0, 0);
            toast.show();
            return;
        }
        this.ifLogging = true;
        int duration = Toast.LENGTH_SHORT;
        Toast toast2 = Toast.makeText(context, "GPS Logging started!", duration);
        toast2.setGravity(16, 0, 0);
        toast2.show();
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append("/GPS Logger");
        String baseDir = sb.toString();
        String fileName = String.format("logger%s.csv", new Object[]{new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())});
        StringBuilder sb2 = new StringBuilder();
        sb2.append(baseDir);
        sb2.append(File.separator);
        sb2.append(fileName);
        try {
            this.writer = new FileWriter(sb2.toString());
            this.writer.write("latitude,longitude,speed,timestamp\n");
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
        final DecimalFormat df = new DecimalFormat("#.########");
        final DecimalFormat sf = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        sf.setRoundingMode(RoundingMode.CEILING);
        this.lat = (TextView) findViewById(R.id.currentLatitude);
        this.lng = (TextView) findViewById(R.id.currentLongitude);
        this.spd = (TextView) findViewById(R.id.currentSpeed);
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                MainActivity.this.latitude = location.getLatitude();
                MainActivity.this.longitude = location.getLongitude();
                MainActivity.this.speed = (double) location.getSpeed();
                MainActivity.this.lat.setText(df.format(MainActivity.this.latitude));
                MainActivity.this.lng.setText(df.format(MainActivity.this.longitude));
                MainActivity.this.spd.setText(String.format("%s m/s", new Object[]{sf.format(MainActivity.this.speed)}));
                try {
                    MainActivity.this.writer.write(String.format("%1$s,%2$s,%3$s,%4$s\n", new Object[]{Double.valueOf(MainActivity.this.latitude), Double.valueOf(MainActivity.this.longitude), Double.valueOf(MainActivity.this.speed), "time"}));
                } catch (IOException e) {
                    Log.e("ERROR", e.toString());
                }
            }

            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            public void onProviderEnabled(String s) {
            }

            public void onProviderDisabled(String s) {
            }
        };
        locationManager.requestLocationUpdates("gps", 2000, 5.0f, this.locationListener);
    }

    public void functionStop(View view) {
        CharSequence text;
        Context context = getApplicationContext();
        if (!this.ifLogging) {
            text = "GPS Logging is not active!";
        } else {
            text = "GPS Logging stopped!";
            this.ifLogging = false;
        }
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(16, 0, 0);
        toast.show();
        locationManager.removeUpdates(this.locationListener);
        try {
            this.writer.close();
        } catch (IOException e) {
            Log.e("ERROR", e.toString());
        }
    }

    public void functionMap(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(intent, 99);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 99 || resultCode != RESULT_OK) {
            Log.i("INFO", "User didn't select any file");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        Uri uri = data.getData();
        String path = uri.getPath();
        String localPath = path.replace("/document/primary:", "");
        sb.append("/" + localPath);
        File file = new File(sb.toString());
        String row;
        StringBuilder csv_data = new StringBuilder();
        try{
            BufferedReader csvReader = new BufferedReader(new FileReader(file.toString()));
            while ((row = csvReader.readLine()) != null) {
                row += ";";
                csv_data.append(row);
            }
            csvReader.close();
        }
        catch(FileNotFoundException e){
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), "File not found", duration);
            toast.setGravity(16, 0, 0);
            toast.show();
        }
        catch(IOException e){
            Log.i("INFO", "IOException occured while reading the CSV file.");
        }
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("csv_content", csv_data.toString());
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
            return;
        }
    }
}
