package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button login , signup;
    EditText username , password;
    private static final int PROGRESS = 0x1;

    ProgressDialog dialog;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String id = "id";
    public static final String session = "session";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        signup =(Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Signup.class);
                startActivity(intent);

            }
        });

        username = (EditText)findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        username.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });



        login =(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                 dialog = ProgressDialog.show(Login.this, "",
                        "Loading. Please wait...", true);

                if((username.getText().toString().isEmpty()) || (password.getText().toString().isEmpty()))
                {
                    dialog.dismiss();
                    Context context = getApplicationContext();
                    CharSequence text = "please fill in the password and username";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else if(!Validation.validateUserName(username.getText().toString())){
                    dialog.dismiss();
                    Toast.makeText(getBaseContext(),"Sorry, you entered a non valid username",Toast.LENGTH_LONG).show();
                    username.requestFocus();
                }
                else {
                    RequestQueue queue = Volley.newRequestQueue(Login.this);

                    String url = getString(R.string.server_connect) + "/LogIn";

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {

                                    Login.this.dialog.dismiss();


                                    try {

                                        JSONObject json = new JSONObject(response);
                                        if (json.has("error")) {

                                            // check the error messages
                                            if (Integer.parseInt(json.get("error").toString()) == 1) {
                                                Context context = getApplicationContext();
                                                CharSequence text = "No such user please try again";
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


                                                if(json.has("marketid")) {

                                                    if (json.getInt("marketid") == 0) {

                                                        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedpreferences.edit();

                                                        editor.putString("username", username.getText().toString());
                                                        editor.putString("session", json.get("session").toString());

                                                        editor.commit();

                                                        Intent intent = new Intent(getBaseContext(), MaketData.class);
                                                        intent.putExtra("userid", json.getInt("userid"));
                                                        startActivityForResult(intent, 1);
//                                                startActivity(intent);
                                                        finish();
                                                    }
                                                    else{


                                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                    sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                                    editor.putInt("user_id", Integer.parseInt(json.get("userid").toString()));
                                                    editor.putString("ctgids", json.getJSONArray("ctgids").toString());
                                                    editor.putInt("market_id", Integer.parseInt(json.get("marketid").toString()));
                                                    editor.putString("session", json.get("session").toString());
                                                    editor.putString("username", username.getText().toString());

                                                    editor.commit();


//                                                Toast.makeText(getBaseContext(),json.get("session").toString(),Toast.LENGTH_LONG).show();
//
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                }

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
                            params.put("username", username.getText().toString());
                            params.put("password", password.getText().toString());
                            params.put("tokenID", FirebaseInstanceId.getInstance().getToken().toString());

                            return params;
                        }
                    };
                    queue.add(postRequest);







            }}
        });

    }
}
