package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class Signup extends AppCompatActivity {


    ProgressDialog dialog;
    Button nextB;
    EditText password , conf ,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        password = (EditText)findViewById(R.id.password);
        conf = (EditText)findViewById(R.id.conf);
        username = (EditText)findViewById(R.id.username);

        password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        conf.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        username.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });


        nextB = (Button)findViewById(R.id.nextB);
        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(Signup.this, "",
                        "Loading. Please wait...", true);

                if(!Validation.validateUserName(username.getText().toString())){
                    dialog.dismiss();
                    Toast.makeText(getBaseContext(),

                            "Sorry, you entred a non valid username",
                            Toast.LENGTH_LONG).show();
                    username.requestFocus();
                }
                else if(password.getText().length() <6){
                    Toast.makeText(getBaseContext(),

                            "Sorry, password should contain at least 6 characters",
                            Toast.LENGTH_LONG).show();
                    password.requestFocus();
                }
                else
                if(!(password.getText().toString().isEmpty() || username.getText().toString().isEmpty()
                        || conf.getText().toString().isEmpty())){

                if(password.getText().toString().matches(conf.getText().toString())){

                    RequestQueue queue = Volley.newRequestQueue(Signup.this);

                    String url = getString(R.string.server_connect) +"/Signup";

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>(){

                                @Override
                                public void onResponse(String response) {

//                                    Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();

                                    Signup.this.dialog.dismiss();

                                    try{

//                                    username.setText(response.toString());
                                        JSONObject json = new JSONObject(response);
                                        if(json.has("error")){

                                            // check the error messages
                                            if(Integer.parseInt(json.get("error").toString())==1){
                                                Context context = getApplicationContext();
                                                CharSequence text = "Please try another username";
                                                int duration = Toast.LENGTH_LONG;

                                                Toast toast = Toast.makeText(context, text, duration);
                                                toast.show();
                                            }
                                            else if(Integer.parseInt(json.get("error").toString())==2){
                                                Context context = getApplicationContext();
                                                CharSequence text = "Please fill in the required data";
                                                int duration = Toast.LENGTH_LONG;

                                                Toast toast = Toast.makeText(context, text, duration);
                                                toast.show();
                                            }
                                            else if(Integer.parseInt(json.get("error").toString())==3){
                                                Context context = getApplicationContext();
                                                CharSequence text = "Sorry there was a problem while connecting to server";
                                                int duration = Toast.LENGTH_LONG;

                                                Toast toast = Toast.makeText(context, text, duration);
                                                toast.show();
                                            }
                                        }
                                        else{
                                            if(json.has("session")){

                                                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                                editor.putString("username", username.getText().toString());
                                                editor.putString("session", json.get("session").toString());

                                                editor.commit();

                                                Intent intent = new Intent(getBaseContext(), MaketData.class);
                                                intent.putExtra("userid",json.getInt("id"));
                                                startActivityForResult(intent,1);
//                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                    }
                                    catch(Exception e){
                                        if(e == null){

                                            Context context = getApplicationContext();
                                            CharSequence text = "Please check your internet connection and try again";
                                            int duration = Toast.LENGTH_LONG;

                                            Signup.this.dialog.dismiss();
                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();

                                        }
                                    }

                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
//                                Log.d("Error.Response", response);
                                    Signup.this.dialog.dismiss();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("username", username.getText().toString());
                            params.put("password", password.getText().toString());


                            return params;
                        }
                    };
                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(postRequest);


                }
                else {
                    Signup.this.dialog.dismiss();

                    Context context = getApplicationContext();
                    CharSequence text = "the password dose not match";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }}
                else {

                    Signup.this.dialog.dismiss();
                    Context context = getApplicationContext();
                    CharSequence text = "please fill in the required data";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                
            }
        });
    }
}
