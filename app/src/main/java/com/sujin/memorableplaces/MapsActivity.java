package com.sujin.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        int pos = intent.getIntExtra("position", 0);

        if (pos==0) {
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {



                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    List<Address> listAddresses = null;
                    try {
                        listAddresses = geocoder.getFromLocation(point.latitude,point.longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String address = "";
                    if(listAddresses != null && listAddresses.size() > 0 )
                    {
                        if (listAddresses.get(0).getThoroughfare() != null)
                        {
                            address+=listAddresses.get(0).getThoroughfare();
                        }
                    }
                    if (address == "")
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String date = dateFormat.format(new Date());
                        address = date;
                    }
                    mMap.addMarker(new MarkerOptions().position(point).title(address));

                    Toast.makeText(MapsActivity.this, "Saved!!", Toast.LENGTH_SHORT).show();
                    MainActivity.array.add(address);
                    MainActivity.adapter.notifyDataSetChanged();
                    MainActivity.location.add(point);

                    ArrayList<String> lat = new ArrayList<String>();
                    ArrayList<String> lon = new ArrayList<String>();

                    for(LatLng loc : MainActivity.location)
                    {
                        lat.add(Double.toString(loc.latitude));
                        lon.add(Double.toString(loc.longitude));
                    }

                    SharedPreferences share = getApplicationContext().getSharedPreferences("com.sujin.memorableplaces",Context.MODE_PRIVATE);

                    try {
                        share.edit().putString( "places",ObjectSerializer.serialize(MainActivity.array)).apply();
                        share.edit().putString( "lat",ObjectSerializer.serialize(lat)).apply();
                        share.edit().putString( "lon",ObjectSerializer.serialize(lon)).apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            /*locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                /*Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> listAddresses = null;
                try {
                    listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(listAddresses != null && listAddresses.size() > 0 )
                {
                    Log.i("Info",listAddresses.get(0).toString());
                }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };*/


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

           else {
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                mMap.clear();
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,9));
            }
        }

        else
        {

            mMap.clear();
            LatLng userLocation = new LatLng(MainActivity.location.get(pos).latitude, MainActivity.location.get(pos).longitude);
            mMap.addMarker(new MarkerOptions().position(userLocation).title(MainActivity.array.get(pos)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,9));

        }




    }
}
