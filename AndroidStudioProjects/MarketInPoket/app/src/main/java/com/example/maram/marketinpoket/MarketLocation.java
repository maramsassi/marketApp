package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import android.location.Geocoder;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by root on 08/09/17.
 */

public class MarketLocation  extends FragmentActivity implements OnMapReadyCallback {
    ProgressDialog dialog;
    private GoogleMap mMap;
    JSONObject json;
    double loc[];
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        done = (Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });










    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        getMarketLocation();




    }

    public  void getMarketLocation(){
        dialog = ProgressDialog.show(MarketLocation.this, "",
                "Loading. Please wait...", true);
        RequestQueue queue = Volley.newRequestQueue(MarketLocation.this);

        String url = getString(R.string.server_connect) + "/GetMarketLocation";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        MarketLocation.this.dialog.dismiss();

                        try {

                            json = new JSONObject(response);
                            if (json.has("error")) {

                                // check the error messages
                                if (Integer.parseInt(json.get("error").toString()) == 1) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "The location of the market not available";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
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
