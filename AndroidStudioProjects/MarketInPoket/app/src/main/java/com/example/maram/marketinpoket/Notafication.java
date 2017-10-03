package com.example.maram.marketinpoket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Notafication extends AppCompatActivity {


    ListView list;
    static final String KEY_REQ = "request";
    static final String KEY_Time = "reqTime";
    static final String KEY_DATE = "reqDate";
    static final String KEY_DNO = "DevNO";
    static final String KEY_REQID = "reqid";
    SharedPreferences sharedpreferences;
    JSONArray jsonMainNode;
    int user_id;
    JSONArray IDS  ;

    Button newB , allB, seenb;
    ProgressDialog dialog;
    String id;
    TextView tv1 ;



    LazyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notafication);
//



        //create the queue to put on it the requests
       final RequestQueue queue = Volley.newRequestQueue(this);

        IDS = new JSONArray() ;

        sharedpreferences = getBaseContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        tv1 = (TextView)findViewById(R.id.tv1);

        user_id = sharedpreferences.getInt("user_id",0);
        if(user_id != 0) {
            dialog = ProgressDialog.show(Notafication.this, "",
                    "Loading. Please wait...", true);

            // getting the categories from the server



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

                            Notafication.this.dialog.dismiss();



                            ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();


                            try {

                                // put the response object in the jsonobject to be processed
                                JSONObject json = new JSONObject(response);


                                if(json.has("error")){

                                    if((Integer.parseInt(json.get("error").toString()) == 4)){
                                        Intent intent = new Intent(getBaseContext(),Login.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    else if (Integer.parseInt(json.get("error").toString()) == 3) {
                                        Context context = getApplicationContext();
                                        CharSequence text = "Sorry there was a problem while connecting to server";
                                        int duration = Toast.LENGTH_LONG;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }
                                }

                            else
                                try {

                                    // get the categories array from the jsonObject
                                    jsonMainNode = json.optJSONArray("req");

                                    int j = 0;
                                    // Process each JSON Node
                                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {


                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                        String request = jsonChildNode.optString("request").toString();
                                        String reqtime = jsonChildNode.optString("reqtime").toString();
                                        String reqdate = jsonChildNode.optString("reqdate").toString();
                                        String deviceno = jsonChildNode.optString("deviceno").toString();
                                        String reqid = jsonChildNode.optString("reqid").toString();
                                        String visibility = jsonChildNode.optString("visiblity").toString();


                                        // getting ids for all the receiving requests
//                                        IDS.put(new JSONObject().put("id",Integer.parseInt(jsonChildNode.optString("reqid"))));

                                        if (visibility.matches("f"))
                                        {
                                            j++;
                                        HashMap<String, String> map = new HashMap<String, String>();
                                            if(request.length()>30)
                                                map.put(KEY_REQ, request.substring(0,25)+".....");
                                            else
                                                map.put(KEY_REQ, request);
                                        map.put(KEY_Time, reqtime);
                                        map.put(KEY_DATE, reqdate);
                                        map.put(KEY_DNO, deviceno);
                                        map.put(KEY_REQID, reqid);
                                        map.put("img", "2");
                                        codeList.add(map);}

                                    }


                                    if (j==0){
                                        tv1.setText("There is no new requests");

                                    }
                                    else
                                        tv1.setText("");


                                    adapter = new LazyAdapter(Notafication.this, codeList);

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
                                }
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Notafication.this.dialog.dismiss();
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
//                params.put("categ",Notafication.this.id);
                    SharedPreferences sharedpreferences = getBaseContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                    params.put("reqtype",1+"");

//                        params.put("categ", sharedpreferences.getString("ctgids","[]") + "");
                        params.put("categ", sharedpreferences.getString("ctgids","[]"));
                        params.put("marketid", sharedpreferences.getInt("market_id",0)+"");

//                    params.put("market_id",sharedpreferences.getInt("market_id", 0) + "");



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
            queue.add(getRequest);
        }
        else{
            Toast.makeText(this,"some thing goes wrong",Toast.LENGTH_LONG).show();
        }




        allB = (Button)findViewById(R.id.allB);
        allB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seenb.setTextColor(Color.parseColor("#a10505"));
                seenb.setBackgroundColor(Color.parseColor("#ffffff"));
                newB.setTextColor(Color.parseColor("#a10505"));
                newB.setBackgroundColor(Color.parseColor("#ffffff"));
                allB.setTextColor(Color.parseColor("#ffffff"));
                allB.setBackgroundColor(Color.parseColor("#a10505"));


                ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();

                try{
                    int j = 0;
                    // Process each JSON Node
                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {


                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String request = jsonChildNode.optString("request").toString();
                        String reqtime = jsonChildNode.optString("reqtime").toString();
                        String reqdate = jsonChildNode.optString("reqdate").toString();
                        String deviceno = jsonChildNode.optString("deviceno").toString();
                        String reqid = jsonChildNode.optString("reqid").toString();
                        String visibility = jsonChildNode.optString("visiblity").toString();


                            j++;

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_REQ, request);
                            map.put(KEY_Time, reqtime);
                            map.put(KEY_DATE, reqdate);
                            map.put(KEY_DNO, deviceno);
                            map.put(KEY_REQID, reqid);
                        map.put("img", "2");
                            codeList.add(map);}

                    if(j==0)
                        tv1.setText("There is no requests");
                    else
                        tv1.setText("");




                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(Notafication.this, codeList);

                list.setAdapter(adapter);




            }
        });

        newB = (Button)findViewById(R.id.newB);
        newB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seenb.setTextColor(Color.parseColor("#a10505"));
                seenb.setBackgroundColor(Color.parseColor("#ffffff"));
                newB.setTextColor(Color.parseColor("#ffffff"));
                newB.setBackgroundColor(Color.parseColor("#a10505"));
                allB.setTextColor(Color.parseColor("#a10505"));
                allB.setBackgroundColor(Color.parseColor("#ffffff"));

                ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();

                try{
                    int j = 0;
                    // Process each JSON Node
                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {



                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String request = jsonChildNode.optString("request").toString();
                        String reqtime = jsonChildNode.optString("reqtime").toString();
                        String reqdate = jsonChildNode.optString("reqdate").toString();
                        String deviceno = jsonChildNode.optString("deviceno").toString();
                        String reqid = jsonChildNode.optString("reqid").toString();
                        String visibility = jsonChildNode.optString("visiblity").toString();

                        if (visibility.matches("f"))
                        {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_REQ, request);
                            map.put(KEY_Time, reqtime);
                            map.put(KEY_DATE, reqdate);
                            map.put(KEY_DNO, deviceno);
                            map.put(KEY_REQID, reqid);
                            map.put("img", "2");
                            j++;
                            codeList.add(map);}
                    }
                    if(j==0)
                        tv1.setText("There is no new requests");
                    else
                        tv1.setText("");


                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(Notafication.this, codeList);

                list.setAdapter(adapter);



            }


        });

        seenb = (Button)findViewById(R.id.seenB);
        seenb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seenb.setTextColor(Color.parseColor("#ffffff"));
                seenb.setBackgroundColor(Color.parseColor("#a10505"));
                newB.setTextColor(Color.parseColor("#a10505"));
                newB.setBackgroundColor(Color.parseColor("#ffffff"));
                allB.setTextColor(Color.parseColor("#a10505"));
                allB.setBackgroundColor(Color.parseColor("#ffffff"));


                ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();

                try{
                    int j =0;
                    // Process each JSON Node
                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {


                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String request = jsonChildNode.optString("request").toString();
                        String reqtime = jsonChildNode.optString("reqtime").toString();
                        String reqdate = jsonChildNode.optString("reqdate").toString();
                        String deviceno = jsonChildNode.optString("deviceno").toString();
                        String reqid = jsonChildNode.optString("reqid").toString();
                        String visibility = jsonChildNode.optString("visiblity").toString();

                        if (visibility.matches("t"))
                        {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_REQ, request);
                            map.put(KEY_Time, reqtime);
                            map.put(KEY_DATE, reqdate);
                            map.put(KEY_DNO, deviceno);
                            map.put(KEY_REQID, reqid);
                            map.put("img", "2");
                            j++;
                            codeList.add(map);}
                    }
                    if(j==0)
                        tv1.setText("There is no requests");
                    else
                        tv1.setText("");


                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(Notafication.this, codeList);

                list.setAdapter(adapter);

            }
        });





        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // get the categ id and run the next activity
                TextView marketid = (TextView) view.findViewById(R.id.notf_id);
                TextView reqtext = (TextView) view.findViewById(R.id.req);
                TextView reqDate = (TextView) view.findViewById(R.id.timeDate);
                int i = Integer.parseInt( adapter.getItem(position)+"");
                Intent intent = new Intent(getBaseContext() , SendResponse.class);
                intent.putExtra("notf_id" , marketid.getText().toString());
                intent.putExtra("request" , reqtext.getText().toString());
                intent.putExtra("reqdate" , reqDate.getText().toString());


                startActivityForResult(intent,1);

            }
        });

    }



    }

