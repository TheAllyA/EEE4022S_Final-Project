package com.application.e_scoots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest ;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.GeofencingClient ;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.List;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment smf ;
    GeofencingClient gfClient ;
    FusedLocationProviderClient location ;
    Double lat = -33.9471524669, longi = 18.4696122428;
    GoogleMap gMap ;
    LocationRequest lRequest ;
    Location lastL ;
    Marker mCurrLocM ;
    Geofence buildGF ;
    PendingIntent geofencePI ;
    SQLiteDatabase SQLdb ;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ActivityCompat.requestPermissions(Map.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 99);

        gfClient = LocationServices.getGeofencingClient(this) ;
        location = LocationServices.getFusedLocationProviderClient(this) ;

        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map) ;
        smf.getMapAsync(this);
    }

    @Override
    public void onPause(){
        super.onPause() ;
        if (location != null) {
            location.removeLocationUpdates(mLCallback) ;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap ;
        int Priority = 100 ; //Priority high
        long intmilli = 20000 ; // 20 seconds


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //lRequest = new LocationRequest.Builder(intmilli).build() ;
            lRequest = new LocationRequest() ;
            lRequest.setInterval(intmilli) ;
            lRequest.setPriority(Priority) ;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location.requestLocationUpdates(lRequest, mLCallback, Looper.myLooper()) ;
                gMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermission() ;
            }
        } else {
            location.requestLocationUpdates(lRequest, mLCallback, Looper.myLooper()) ;
            gMap.setMyLocationEnabled(true);
        }
        Circle c = gMap.addCircle(new CircleOptions().center(new LatLng(lat,longi)).radius(65).strokeColor(Color.RED).strokeWidth(4f).fillColor(0xFFCCCB)) ;
        buildGF();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gfClient.addGeofences(GFrequest(), GFpi())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Geofences added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Geofences failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
    }

    LocationCallback mLCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            File path = Map.this.getDatabasePath("E_Scoots.db") ;
            SQLdb = SQLiteDatabase.openDatabase(path.getAbsolutePath(),null,SQLiteDatabase.OPEN_READWRITE) ;

            List<Location> loclist = locationResult.getLocations() ;

            if (loclist.size() > 0) {
                Location loc = loclist.get(loclist.size()-1) ;
                lastL = loc ;

                if (mCurrLocM != null) {
                    mCurrLocM.remove();
                }

                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude()) ;
                //lat = loc.getLatitude() ;
                //longi = loc.getLongitude() ;
//                String update = "UPDATE Scooter_Information SET Location = '" + Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude()) + " WHERE Student_Num= 'ADMALI004'" ;
//                SQLdb.execSQL(update);

                MarkerOptions MO = new MarkerOptions() ;
                MO.position(latLng) ;
                MO.title("Current Position");

                mCurrLocM = gMap.addMarker(MO) ;

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

                String update = "UPDATE Scooter_Information SET Location = '" + Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude()) + "' WHERE Student_Num= 'ADMALI004'" ;
                SQLdb.execSQL(update);
                SQLdb.close();

            }
        }
    } ;

    //public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99 ;
    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int ReqC, String permissions[], int[] grantResults) {
        if (ReqC == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location.requestLocationUpdates(lRequest,mLCallback, Looper.myLooper()) ;
                    gMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
            return ;
        }
    }

    @SuppressLint("MissingPermission")
    public void buildGF() {
        buildGF = new Geofence.Builder()
                .setRequestId("UCT ID")
                .setCircularRegion(lat, longi, 65)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(10000)
                .build() ;

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            gfClient.addGeofences(GFrequest(), GFpi())
//                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(getApplicationContext(), "Geofences added", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(this, new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext(), "Geofences failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else {
//            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
//        }

    }

    private GeofencingRequest GFrequest() {
        GeofencingRequest.Builder build = new GeofencingRequest.Builder() ;
        build.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) ;
        build.addGeofence(buildGF) ;

        return build.build() ;
    }
    @SuppressLint({"MissingPermission","UnspecifiedImmutableFlag" })
    private PendingIntent GFpi() {
        if (geofencePI != null) {
            return geofencePI ;
        }

        Intent i = new Intent(this, GFBroadcastReceiver.class) ;
        geofencePI = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_MUTABLE |PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePI ;
    }
}