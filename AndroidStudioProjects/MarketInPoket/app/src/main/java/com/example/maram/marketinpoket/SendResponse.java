package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SendResponse extends AppCompatActivity {

    TextView reqText , DateTime, tv2;
    RadioGroup g1;
    Button Reply;

    RadioButton nob;
    ProgressDialog dialog;
    String res , note;
    int reqid;
    EditText Note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_response);

//        res = "true";
        reqText=(TextView)findViewById(R.id.reqText);
        reqText.setText(getIntent().getStringExtra("request"));

        Note = (EditText)findViewById(R.id.Note);
        Note.setFilters(new InputFilter[] { new InputFilter.LengthFilter(250) });

        DateTime=(TextView)findViewById(R.id.reqDate);
        DateTime.setText(getIntent().getStringExtra("reqdate"));


        tv2 = (TextView)findViewById(R.id.tv2);

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
        Note.addTextChangedListener(mTextEditorWatcher);



        Reply = (Button)findViewById(R.id.reply);
        Reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Note.getText().toString().toString().isEmpty())||(Note.getText().toString().trim().isEmpty())) {
                    Toast.makeText(getBaseContext(),
                            "Please type in your reply",
                            Toast.LENGTH_LONG).show();
                    Note.requestFocus();
                } else {
                    dialog = ProgressDialog.show(SendResponse.this, "",
                            "Loading. Please wait...", true);
                    // send a request to a server


                    note = Note.getText().toString().trim();

                    reqid = Integer.parseInt(getIntent().getStringExtra("notf_id"));


                    RequestQueue queue = Volley.newRequestQueue(SendResponse.this);
                    String url = getString(R.string.server_connect) + "/UserResponse";

                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
//                                    Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();

                                    SendResponse.this.dialog.dismiss();


                                    try {

                                        JSONObject json = new JSONObject(response);
                                        if (json.has("error")) {

                                            // check the error messages
                                            if (Integer.parseInt(json.get("error").toString()) == 1) {


                                                Toast.makeText(getApplicationContext(),
                                                        "Some thing gose wrong please try again",
                                                        Toast.LENGTH_LONG).show();

                                            } else if (Integer.parseInt(json.get("error").toString()) == 3) {

                                                Toast.makeText(getApplicationContext(),
                                                        "Sorry there was a problem while connecting to server",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            if((Integer.parseInt(json.get("error").toString()) == 4)){
                                                Intent intent = new Intent(getBaseContext(),Login.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                        } else {
                                            if (json.has("check")) {
                                                try {
                                                    showDialog();
                                                } catch (Exception ex) {
                                                }

                                            }
                                        }

                                    } catch (Exception e) {


                                        Context context = getApplicationContext();
                                        CharSequence text = "Please check your internet connection and try again";
                                        int duration = Toast.LENGTH_LONG;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();

                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
//                                Log.d("Error.Response", response);
                                    dialog.dismiss();
                                    Toast.makeText(getBaseContext(), "Error detected while connecting to a server",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {

                            SharedPreferences sharedpreferences = SendResponse.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("note", SendResponse.this.note);

                            params.put("reqid", reqid + "");
                            params.put("marketid", sharedpreferences.getInt("market_id", 0) + "");
//                        params.put("res",res);


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
                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            String sessionId = sharedpreferences.getString("session","");



                            headers.put("Cookie", "JSESSIONID="+ sessionId);


                            return headers;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SendResponse.this);

        builder.setMessage("Your reply has been sent" );
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // go to main page
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();

                dialog.dismiss();
            }
        });


        builder.show();
    }
}
