package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowRequest extends AppCompatActivity {


    ListView list;
    static final String KEY_REQ = "request";
    static final String KEY_Time = "reqTime";
    static final String KEY_DATE = "reqDate";
    static final String KEY_DNO = "DevNO";
    static final String KEY_REQID = "reqid";
     public String devno;
    SharedPreferences sharedpreferences;
    int user_id;

    ProgressDialog dialog;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_reqs);




            dialog = ProgressDialog.show(ShowRequest.this, "",
                    "Loading. Please wait...", true);


        getReqs();

        // Click event for single list row
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                TextView reqid = (TextView) view.findViewById(R.id.notf_id);
//                showDialog_(reqid.getText().toString());
//
//
//
//            }
//        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView reqid = (TextView) view.findViewById(R.id.notf_id);
                showDialog_(reqid.getText().toString());

                return true;
            }
        });

    }


    private  void getReqs(){
        RequestQueue queue = Volley.newRequestQueue(this);


        // url address to connect to the server
        String url = getString(R.string.server_connect) + "/GetRequest";

        // create a list view object
        list = (ListView) findViewById(R.id.list);


        //send a get request to the server
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    //actions in the response object handled
                    @Override
                    public void onResponse(String response) {

                        ShowRequest.this.dialog.dismiss();

                        ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();


                        try {

                            // put the response object in the jsonobject to be processed
                            JSONObject json = new JSONObject(response);
                            Log.d("ddddddddddd",response.toString());
                            try {

                                // get the categories array from the jsonObject
                                JSONArray jsonMainNode = json.optJSONArray("req");
                                // Process each JSON Node
                                for (int i = 0; i <jsonMainNode.length() ; i++) {

                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    String request = jsonChildNode.optString("request").toString();
                                    String reqtime = jsonChildNode.optString("reqtime").toString();
                                    String reqdate = jsonChildNode.optString("reqdate").toString();
                                    String deviceno = jsonChildNode.optString("deviceno").toString();
                                    String reqid = jsonChildNode.optString("reqid").toString();

                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(KEY_REQ, request);
                                    map.put(KEY_Time, reqtime);
                                    map.put(KEY_DATE, reqdate);
                                    map.put(KEY_DNO, deviceno);
                                    map.put(KEY_REQID, reqid);
                                    map.put("img", "1");
                                    codeList.add(map);

                                }


                                LazyAdapter adapter = new LazyAdapter(ShowRequest.this, codeList);

                                list.setAdapter(adapter);
                            } catch (Exception e) {

                                // some thing gose wrong will loading categories

                                Context context = getApplicationContext();
                                CharSequence text = "please check your internet connection and try again";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();


                            }

                        } catch (Exception e) {
                            if (e == null) {

                                Context context = getApplicationContext();
                                CharSequence text = "please check your internet connection and try again";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
//
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        ShowRequest.this.dialog.dismiss();
                        // error
                        Context context = getApplicationContext();
                        CharSequence text = "a problem detected will connecting to server";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

//                    params.put("reqtype",getIntent().getIntExtra("reqtype",0)+"");
//
//                        params.put("dvno", Settings.Secure.getString(getBaseContext().getContentResolver(),
//                                Settings.Secure.ANDROID_ID));
                params.put("reqtype","2");

                params.put("dvno", FirebaseInstanceId.getInstance().getToken().toString());
//                    params.put("dvno","12345");


                return params;
            }

        };
        queue.add(getRequest);


    }



    public  void showDialog_(final String reqid){

        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowRequest.this);
        builder.setMessage("Are You Sure, Want to delete this request");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialog = ProgressDialog.show(ShowRequest.this, "",
                        "Loading. Please wait...", true);
                RequestQueue queue = Volley.newRequestQueue(ShowRequest.this);

                String url = getString(R.string.server_connect) + "/CancelRequest";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                ShowRequest.this.dialog.dismiss();

                                try {

                                    JSONObject json = new JSONObject(response);
                                    if (json.has("error")) {

                                        // check the error messages
                                        if (Integer.parseInt(json.get("error").toString()) == 1) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "A Problem deteccted, please try again";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }  else if (Integer.parseInt(json.get("error").toString()) == 3) {

                                            Toast.makeText(getApplicationContext(),
                                                    "Sorry there was a problem while connecting to server"
                                                    , Toast.LENGTH_LONG).show();

                                        }
                                    } else {
                                        if (json.has("check")) {
                                            getReqs();

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

                        params.put("reqId",reqid);

                        return params;
                    }
                };
                queue.add(postRequest);

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }


}

