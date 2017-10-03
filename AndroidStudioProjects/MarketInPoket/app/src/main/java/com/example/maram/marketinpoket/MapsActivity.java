package com.example.maram.marketinpoket;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.SharedPreferences;


import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;

import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.util.Locale;
import android.location.Address;
import java.util.List;

import android.location.Geocoder;

import android.app.ProgressDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Button;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    double loc[];
    private GoogleMap mMap , map;
    ProgressDialog dialog;
    Button setLocation;

    MarkerOptions markerOptions;
    JSONObject json;

    LocationListener mLocationListener;
    EditText locationET;
    double lat = 0.0;
    double lng = 0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setLocation =(Button)findViewById(R.id.setLocation);
        locationET = (EditText)findViewById(R.id.LocationET);
        locationET.setText(getIntent().getStringExtra("location"));

        lat= getIntent().getDoubleExtra("lat",0);
        lng= getIntent().getDoubleExtra("lng",0);


        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                                            Intent location = new Intent();
                                            String text = "Result to be returned....";

                                            location.setData(Uri.parse(text));
                                            location.putExtra("lat",lat);
                                            location.putExtra("lng",lng);
                                            location.putExtra("location",locationET.getText().toString());

                                            setResult(RESULT_OK, location);

                                            finish();



            }
        });
    }










    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        if (lat==0.0 && lng==0.0)
            getCurrentLocation();
        LatLng currentLoc = new LatLng(lat, lng);
        mMap.addMarker(new
                MarkerOptions().position(currentLoc).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));
        markerOptions = new MarkerOptions();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                markerOptions.position(point);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                mMap.addMarker(markerOptions);
                String all_vals = String.valueOf(point);
                String[] separated = all_vals.split(":");
                String latlng[] = separated[1].split(",");
                MapsActivity.this.lat = Double.parseDouble(latlng[0].trim().substring(1));
                MapsActivity.this.lng= Double.parseDouble(latlng[1].substring(0,latlng[1].length()-1));
                markerOptions.title("Outlet Location");
                getLocation(MapsActivity.this.lat ,MapsActivity.this.lng);
            }
        });
    }



    public void getCurrentLocation(){
        loc = new double[2];
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        LocationManager locationManager =
                (LocationManager) this.getSystemService(LOCATION_SERVICE);



        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            // ...
            lat = location.getLatitude();
            lng = location.getLongitude();

        }
//                tv1.setText("null");
        this.mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                lat  = location.getLatitude();
                lng  = location.getLongitude();
                //new MainManager(MainActivity.this).getNearstCars(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,mLocationListener);



    }


    public void setUpMap() {
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // map.setMyLocationEnabled(true);
        //  map.setTrafficEnabled(true);
        //  map.setIndoorEnabled(true);
        // map.getCameraPosition();
        // map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        markerOptions = new MarkerOptions();
        markerOptions.title("Outlet Location");
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                markerOptions.position(point);
                map.animateCamera(CameraUpdateFactory.newLatLng(point));
                map.addMarker(markerOptions);
                String all_vals = String.valueOf(point);
                String[] separated = all_vals.split(":");
                String latlng[] = separated[1].split(",");
                double MyLat = Double.parseDouble(latlng[0].trim().substring(1));
                double MyLong = Double.parseDouble(latlng[1].substring(0,latlng[1].length()-1));
                markerOptions.title("Outlet Location");
                getLocation(MyLat,MyLong);
            }
        });
    }

    public void getLocation(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }





    public  void getMarketLocation(){
        dialog = ProgressDialog.show(MapsActivity.this, "",
                "Loading. Please wait...", true);
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

        String url = getString(R.string.server_connect) + "/GetMarketLocation";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        MapsActivity.this.dialog.dismiss();

                        try {

                            json = new JSONObject(response);
                            if (json.has("error")) {

                                // check the error messages
                                if (Integer.parseInt(json.get("error").toString()) == 1) {
                                    getCurrentLocation();
                                } else if (Integer.parseInt(json.get("error").toString()) == 2) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Please fill in the required data";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                } else if (Integer.parseInt(json.get("error").toString()) == 3) {

                                    Toast.makeText(getApplicationContext(),
                                            "Sorry there was a problem while connecting to server"
                                            , Toast.LENGTH_LONG).show();

                                }  else if (Integer.parseInt(json.get("error").toString()) == 4) {


                                    Toast.makeText(getApplicationContext(),
                                            "Sorry, username has wrong format",
                                            Toast.LENGTH_LONG).show();

                                }
                            } else {
                                if (json.has("check")) {
                                    LatLng point = new LatLng(Double.parseDouble(json.get("lat").toString()),
                                            Double.parseDouble(json.get("lng").toString()));
                                    LatLng TutorialsPoint = new LatLng(Double.parseDouble(json.get("lat").toString()),
                                            Double.parseDouble(json.get("lng").toString()));
                                    mMap.addMarker(new
                                            MarkerOptions().position(TutorialsPoint).title(getIntent().getStringExtra("marketname").toString()));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));
//
//                                    loc[0] = Double.parseDouble(json.get("lat").toString());
//                                    loc[1] = Double.parseDouble(json.get("lng").toString());

//                                   Toast.makeText(getBaseContext(),loc[0]+"",Toast.LENGTH_LONG).show();
//                                   Toast.makeText(getBaseContext(),json.get("lat").toString()+"",Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (Exception e) {
                            if (e == null) {

                                Context context = getApplicationContext();
                                CharSequence text = "please check your internet connection and try again";
                                int duration = Toast.LENGTH_LONG;
                                dialog.dismiss();

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // error
                        Context context = getApplicationContext();
                        CharSequence text = "a problem detected will connecting to server";
                        int duration = Toast.LENGTH_LONG;

                        dialog.dismiss();
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

//                params.put("marketId", "270");
                params.put("marketId", getIntent().getStringExtra("marketid"));

                return params;
            }
        };
        queue.add(postRequest);

    }


}

