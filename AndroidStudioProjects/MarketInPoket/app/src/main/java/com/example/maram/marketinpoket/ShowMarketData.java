package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShowMarketData extends AppCompatActivity {
    TextView marketname, tel, location;
    ProgressDialog dialog;
    ImageView img;
    LinearLayout locationl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_market_data);
        marketname = (TextView)findViewById(R.id.marketname);
        tel = (TextView) findViewById(R.id.tel);
        location = (TextView)findViewById(R.id.location);
        locationl = (LinearLayout)findViewById(R.id.locationL);
        img = (ImageView)findViewById(R.id.imageView4);
        dialog = ProgressDialog.show(ShowMarketData.this, "",
                "Loading. Please wait...", true);


        RequestQueue queue = Volley.newRequestQueue(ShowMarketData.this);

        String url = getString(R.string.server_connect) + "/GetMarketData";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        ShowMarketData.this.dialog.dismiss();

                        try {


                            JSONObject json = new JSONObject(response);

                            if (json.has("error")) {

                                Toast.makeText(getBaseContext(),"sorry problem detected while connecting to the server",Toast.LENGTH_LONG).show();
                            } else {
                                marketname.setText(json.getString("marketname"));
                                tel.setText(json.getString("tel"));
                                location.setText(json.getString("location"));


                            }

                        } catch (Exception e) {
                            if (e == null) {



                                dialog.dismiss();
                                Toast.makeText(getBaseContext(),"please check your internet connection and try again",Toast.LENGTH_LONG).show();

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
                params.put("marketid", getIntent().getStringExtra("marketid"));
//
                return params;
            }
        };
        queue.add(postRequest);


        locationl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),MarketLocation.class);
                intent.putExtra("marketid",getIntent().getStringExtra("marketid"));
                intent.putExtra("marketname",marketname.getText().toString());
                startActivityForResult(intent,1);
            }
        });

    }
}
