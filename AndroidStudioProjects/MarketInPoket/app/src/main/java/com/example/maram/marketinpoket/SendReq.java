package com.example.maram.marketinpoket;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class SendReq extends AppCompatActivity {
    String ctgid;
    String request;
    Button sendReqBtn;
    ProgressDialog dialog;
    EditText req;
    TextView tv1 , tv2;
    double loc[];

    LocationListener mLocationListener;



    private double currentLatitude ;
    private double currentLongitude ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_req);

        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv1.setText(getIntent().getStringExtra("ctgText"));





//        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());
        req = (EditText)findViewById(R.id.order);


         final TextWatcher mTextEditorWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                tv2.setText(String.valueOf(s.length())+"/250");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        req.addTextChangedListener(mTextEditorWatcher);
        req.setFilters(new InputFilter[] { new InputFilter.LengthFilter(250) });
//        req.setText(FirebaseInstanceId.getInstance().getToken().toString());
//        Toast.makeText(getBaseContext(),FirebaseInstanceId.getInstance().getToken().toString(),Toast.LENGTH_LONG).show();

        sendReqBtn = (Button)findViewById(R.id.send_req_btn);
        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc = getReqLocation();

                if((req.getText().toString().isEmpty())
                        ||(req.getText().toString().trim().isEmpty())){
                    Toast.makeText(getBaseContext(),
                            "Please type in your request",
                            Toast.LENGTH_LONG).show();
                    req.requestFocus();}
                else{
                dialog = ProgressDialog.show(SendReq.this, "",
                        "Loading. Please wait...", true);
                // send a request to a server



                request = req.getText().toString().trim();
                ctgid =getIntent().getStringExtra("idctg");

                RequestQueue queue = Volley.newRequestQueue(SendReq.this);
                String url = getString(R.string.server_connect) +"/AddRequest";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>(){

                            @Override
                            public void onResponse(String response) {

                                SendReq.this.dialog.dismiss();



                                try{

//                                    username.setText(response.toString());
                                    JSONObject json = new JSONObject(response);
                                    if(json.has("error")){

                                        // check the error messages
                                        if(Integer.parseInt(json.get("error").toString())==1){
                                            Context context = getApplicationContext();
                                            CharSequence text = "some thing gose wrong please try again";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }else if (Integer.parseInt(json.get("error").toString()) == 3) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "sorry there was a problem while connecting to server";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }}
                                    else{
                                        if(json.has("check")){

                                           try{


                                               showDialog();
//                                               new MyDialog().showDialog(getBaseContext(),
//                                                       "Your request has sent successfully ",
//                                                       "OK");


                                           }
                                           catch (Exception ex){

                                               }
                                        }
                                    }

                                }
                                catch(Exception e){
//                                    if(e == null){

                                        Context context = getApplicationContext();
                                        CharSequence text = "please check your internet connection and try again";
                                        int duration = Toast.LENGTH_LONG;

                                    dialog.dismiss();
                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
//                                    }
                                }

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                // error
//                                Log.d("Error.Response", response);
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {

                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("req", SendReq.this.request);
                        params.put("dno", FirebaseInstanceId.getInstance().getToken().toString());
//                        params.put("dno", Settings.Secure.getString(getBaseContext().getContentResolver(),
//                                Settings.Secure.ANDROID_ID));
//                        params.put("dno", "12345");
                        params.put("ctgid",SendReq.this.ctgid);
                        params.put("latitude",SendReq.this.loc[0]+"");
                        params.put("longitude",SendReq.this.loc[1]+"");


                        return params;
                    }

                };
                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(postRequest);

            }
        }
        });

    }


    public void showDialog() throws Exception
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SendReq.this);

        builder.setMessage("Your request has sent successfully " );
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(getBaseContext(), ShowRequest.class);
                startActivity(intent);
                finish();

                dialog.dismiss();
            }
        });


        builder.show();
    }



    public double[] getReqLocation(){
        loc = new double[2];
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        LocationManager locationManager =
                (LocationManager) this.getSystemService(LOCATION_SERVICE);



        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            // ...
            loc[0] = location.getLatitude();
            loc[1] = location.getLongitude();

        }
//                tv1.setText("null");
        this.mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                loc[0]  = location.getLatitude();
                loc[1]  = location.getLongitude();
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

        return loc;

    }






}

