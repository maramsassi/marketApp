package com.example.maram.marketinpoket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaketData extends AppCompatActivity {

    Button next;
    EditText marketname, location,tel ;
    double lat = 0.0;
    double lng = 0.0;
    String locationS = null;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                lat = data.getDoubleExtra("lat",0.0);
                lng = data.getDoubleExtra("lng",0.0);
                locationS = data.getStringExtra("location");
                location.setText(locationS);
            }

        }

//        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maket_data);






        marketname = (EditText) findViewById(R.id.marketname);
        marketname.requestFocus();
        location = (EditText) findViewById(R.id.location);
        tel = (EditText) findViewById(R.id.tel);

        tel.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
        marketname.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
//        location.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent location = new Intent(MaketData.this,MapsActivity.class);

                location.putExtra("lat",lat);
                location.putExtra("lng",lng);
                location.putExtra("location",locationS);

//                setResult(RESULT_OK, location);
                startActivityForResult(location,1);
            }
        });
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!Validation.validateMarketName(marketname.getText().toString())){
                    Toast.makeText(getBaseContext(),"Market name in wrong format, try another one",Toast.LENGTH_LONG).show();
                    marketname.requestFocus();
                }
//                if(marketname.getText().toString().length() <=5 && marketname.getText().toString().length()>=15){
//                    Toast.makeText(getBaseContext(),"Market name in wrong format, try another one",Toast.LENGTH_LONG).show();
//                    marketname.requestFocus();
//                }
                else
                    if(marketname.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(),"Please, fill in the market name",Toast.LENGTH_LONG).show();
                        marketname.requestFocus();
                    }

                else
                    if (!Validation.validatePhoneNumber(tel.getText().toString())){
                        Toast.makeText(getBaseContext(),"Phone number in wrong format, try another one",Toast.LENGTH_LONG).show();
                        tel.requestFocus();
                    }
                else
                    if(tel.getText().toString().isEmpty()){
                        Toast.makeText(getBaseContext(),"Please, fill in the phone number",Toast.LENGTH_LONG).show();
                        tel.requestFocus();
                    }
                else{

                    Intent intent = new Intent(getBaseContext() , SpecifyCateg.class);
                    intent.putExtra("marketname",marketname.getText().toString());
                    intent.putExtra("location",locationS);
                    intent.putExtra("tel",tel.getText().toString());
                    intent.putExtra("lat",lat);
                    intent.putExtra("lng",lng);

                    intent.putExtra("userid",getIntent().getIntExtra("userid",1));
                    startActivityForResult(intent,1);

                }

            }


        });
    }
}
