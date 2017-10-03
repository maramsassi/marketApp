package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShowProfile extends AppCompatActivity {

    String session;
    LinearLayout EditLocation;
    int id ;
    ImageView home , notafication;
    TextView username , tel , location , marketName;
    ProgressDialog dialog;

    SharedPreferences sharedpreferences ;
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        username = (TextView)findViewById(R.id.username);
        tel = (TextView)findViewById(R.id.tele);
        location = (TextView)findViewById(R.id.location);
        marketName = (TextView)findViewById(R.id.marketName);
        EditLocation = (LinearLayout)findViewById(R.id.location_edit);


        sharedpreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username.setText( sharedpreferences.getString("username", null));
        tel.setText(getIntent().getStringExtra("tel"));
        marketName.setText(getIntent().getStringExtra("marketname"));
        location.setText(getIntent().getStringExtra("location"));

//        int id = Integer.parseInt(sharedpreferences.getString("id", null));

        dialog = ProgressDialog.show(ShowProfile.this, "",
                "Loading. Please wait...", true);

        RequestQueue queue = Volley.newRequestQueue(ShowProfile.this);

        String url = getString(R.string.server_connect) + "/GetAccountData";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        ShowProfile.this.dialog.dismiss();

                        try {

                            JSONObject json = new JSONObject(response);
                            if (json.has("error")) {

                                // check the error messages
                                if (Integer.parseInt(json.get("error").toString()) == 1) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "No such user please try again";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
//                                    toast.show();
                                } else if (Integer.parseInt(json.get("error").toString()) == 2) {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Please fill in the required data";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
//                                    toast.show();
                                } else if (Integer.parseInt(json.get("error").toString()) == 3) {

                                    Toast.makeText(getApplicationContext(),
                                            "Sorry there was a problem while connecting to server"
                                            , Toast.LENGTH_LONG).show();

                                }  else if (Integer.parseInt(json.get("error").toString()) == 4) {


                                    Intent intent = new Intent(getBaseContext(),Login.class);
                                    startActivity(intent);
                                    finish();

                                }
                            } else {
                                if (json.has("marketname")) {


                                    username.setText( sharedpreferences.getString("username", null));
                                    tel.setText(json.getString("tel"));
                                    marketName.setText(json.getString("marketname"));
                                    location.setText(json.getString("location"));

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
//                params.put("username", username.getText().toString());
////                params.put("password", password.getText().toString());
//                params.put("tokenID", FirebaseInstanceId.getInstance().getToken().toString());

                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<>();
                }
                // add the session cookie
                // try to get the cookie from the shared prefs
                sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                String sessionId = sharedpreferences.getString("session","");



                headers.put("Cookie", "JSESSIONID="+ sessionId);


                return headers;
            }


        };
        queue.add(postRequest);


//
//        EditLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(),MapsActivity.class);
//                startActivity(intent);
//            }
//        });







    }
}
