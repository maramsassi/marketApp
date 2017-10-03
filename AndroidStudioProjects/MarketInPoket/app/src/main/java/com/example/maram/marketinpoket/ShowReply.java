package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class ShowReply extends AppCompatActivity {


    ListView list;
    static final String KEY_REQ = "request";
    static final String KEY_Time = "reqTime";
    static final String KEY_DATE = "reqDate";
    static final String KEY_DNO = "DevNO";
    static final String KEY_REQID = "reqid";
    SharedPreferences sharedpreferences;
    JSONArray IDS  ;
    int user_id;

    TextView tv1;
    JSONArray jsonMainNode;
    Button newB , allB, seenb;
    ProgressDialog dialog;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notafication);


        tv1 = (TextView)findViewById(R.id.tv1);

//        sharedpreferences = getBaseContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


//        user_id = sharedpreferences.getInt("user_id",0);
//        if(user_id != 0) {
            dialog = ProgressDialog.show(ShowReply.this, "",
                    "Loading. Please wait...", true);

            // getting the categories from the server
            //create the queue to put on it the requests
            final RequestQueue queue = Volley.newRequestQueue(this);


            // url address to connect to the server
            String url = getString(R.string.server_connect) + "/GetRequest";

            // create a list view object
            list = (ListView) findViewById(R.id.list);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView reqid = (TextView) view.findViewById(R.id.notf_id);
                showDialog_(reqid.getText().toString());

                return true;
            }
        });


            //send a get request to the server
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        //actions in the response object handled
                        @Override
                        public void onResponse(String response) {


                            ShowReply.this.dialog.dismiss();

                            ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();


                            try {

                                // put the response object in the jsonobject to be processed
                                JSONObject json = new JSONObject(response);

                                try {

                                    // get the categories array from the jsonObject
                                    jsonMainNode = json.optJSONArray("req");
                                    IDS = new JSONArray();

                                    int j = 0;
                                    // Process each JSON Node
                                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {

                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                        String request = jsonChildNode.optString("request").toString();
                                        String reqtime = jsonChildNode.optString("reqtime").toString();
                                        String reqdate = jsonChildNode.optString("reqdate").toString();
                                        String reqid = jsonChildNode.optString("reqid").toString();
                                        String marketid = jsonChildNode.optString("marketid").toString();
                                        String visibility = jsonChildNode.optString("visiblity").toString();

//                                         getting ids for all the receiving requests
                                        JSONObject id = new JSONObject();
                                        id.put("req_id",reqid);
                                        id.put("market_id",marketid);
//

                                        IDS.put(id);
                                        if(visibility.matches("f")){
                                            j++;
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put(KEY_REQ, request);
                                        map.put(KEY_Time, reqtime);
                                        map.put(KEY_DATE, reqdate);
                                        map.put(KEY_REQID, reqid);
                                        map.put("img", "3");
                                        codeList.add(map);}

                                        if(j==0)
                                            tv1.setText("There is no new replies");
                                        else
                                            tv1.setText("");


                                    }
//===================================================================================================================================
//=================================================================================================================================

//                                    //send a get request to the server
                                    StringRequest getRequest = new StringRequest(Request.Method.POST,
                                            getString(R.string.server_connect) + "/UpdateRes",
                                            new Response.Listener<String>() {

                                                //actions in the response object handled
                                                @Override
                                                public void onResponse(String response) {




                                                    try {

                                                        // put the response object in the jsonobject to be processed
                                                        JSONObject json = new JSONObject(response);

                                                        if(json.has("check")){
//                                                            Toast.makeText(getBaseContext(),"every thing goes well",Toast.LENGTH_LONG).show();
                                                        }
                                                        else
                                                        if (json.has("error")){
//                                                            Toast.makeText(getBaseContext(),"error detected",Toast.LENGTH_LONG).show();
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

                                                    ShowReply.this.dialog.dismiss();
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
                                            params.put("ids",IDS.toString());
                                            return params;
                                        }
//
                                    };
                                    queue.add(getRequest);
//=================================================================================================================================


                                    LazyAdapter adapter = new LazyAdapter(ShowReply.this, codeList);

                                    list.setAdapter(adapter);
                                } catch (Exception e) {

                                    // some thing gose wrong will loading categories

                                    Context context = getApplicationContext();
                                    CharSequence text = "Please check your internet connection and try again";
                                    int duration = Toast.LENGTH_LONG;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                }

                            } catch (Exception e) {
                                if (e == null) {

                                    Context context = getApplicationContext();
                                    CharSequence text = "Please check your internet connection and try again";
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

                            ShowReply.this.dialog.dismiss();
                            // error
                            Context context = getApplicationContext();
                            CharSequence text = "A problem detected will connecting to server";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                        params.put("dvno", FirebaseInstanceId.getInstance().getToken().toString());
                    params.put("reqtype","3");

                    return params;
                }

            };
            queue.add(getRequest);





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


//                Toast.makeText(getBaseContext(),jsonMainNode.toString(),Toast.LENGTH_LONG).show();
                ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();

                try{
                    int j =0;
                    // Process each JSON Node
                    for (int i = jsonMainNode.length() -1 ; i >= 0; i--) {

                        j++;
                        /****** Get Object for each JSON node.***********/
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        String request = jsonChildNode.optString("request").toString();
                        String reqtime = jsonChildNode.optString("reqtime").toString();
                        String reqdate = jsonChildNode.optString("reqdate").toString();
                        String deviceno = jsonChildNode.optString("deviceno").toString();
                        String reqid = jsonChildNode.optString("reqid").toString();
                        String visibility = jsonChildNode.optString("visiblity").toString();




                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_REQ, request);
                        map.put(KEY_Time, reqtime);
                        map.put(KEY_DATE, reqdate);
                        map.put(KEY_DNO, deviceno);
                        map.put(KEY_REQID, reqid);
                        map.put("img", "3");
                        codeList.add(map);}
                    if(j==0)
                        tv1.setText("There is no replies");
                    else
                        tv1.setText("");



                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(ShowReply.this, codeList);

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
                    int j=0;
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
                            j++;

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_REQ, request);
                            map.put(KEY_Time, reqtime);
                            map.put(KEY_DATE, reqdate);
                            map.put(KEY_DNO, deviceno);
                            map.put(KEY_REQID, reqid);
                            map.put("img", "3");
                            codeList.add(map);}
                        if(j==0)
                            tv1.setText("There is no new replies");
                        else
                            tv1.setText("");
                    }


                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(ShowReply.this, codeList);

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

                            j++;
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put(KEY_REQ, request);
                            map.put(KEY_Time, reqtime);
                            map.put(KEY_DATE, reqdate);
                            map.put(KEY_DNO, deviceno);
                            map.put(KEY_REQID, reqid);
                            map.put("img", "3");
                            codeList.add(map);}
                        if(j==0)
                            tv1.setText("There is no replies");
                        else
                            tv1.setText("");
                    }


                }
                catch(Exception ex){}



                LazyAdapter adapter = new LazyAdapter(ShowReply.this, codeList);

                list.setAdapter(adapter);

            }
        });

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                

                // get the categ id and run the next activity
                TextView reqid = (TextView) view.findViewById(R.id.notf_id);
                TextView req = (TextView) view.findViewById(R.id.req);
                TextView timeDate = (TextView) view.findViewById(R.id.timeDate);
//
//
//
                Intent intent = new Intent(getBaseContext() , ReplyDetails.class);
                intent.putExtra("reqid" , reqid.getText().toString());
                intent.putExtra("req" , req.getText().toString());
                intent.putExtra("reqDate" , timeDate.getText().toString());
//
//
//
                startActivityForResult(intent,1);


            }
        });

    }

    public  void showDialog_(final String reqid){

        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowReply.this);
        builder.setMessage("Are You Sure,You want to stop receiving replies on this request");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialog = ProgressDialog.show(ShowReply.this, "",
                        "Loading. Please wait...", true);
                RequestQueue queue = Volley.newRequestQueue(ShowReply.this);

                String url = getString(R.string.server_connect) + "/CancelRequest";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                ShowReply.this.dialog.dismiss();

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
                                Toast.makeText(getBaseContext(),error.toString(),Toast.LENGTH_LONG).show();
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


    public  void getReqs(){}

    }

