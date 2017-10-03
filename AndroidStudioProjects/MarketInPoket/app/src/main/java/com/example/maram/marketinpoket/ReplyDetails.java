package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ReplyDetails extends AppCompatActivity {

    ListView list;
    static final String KEY_RES    = "response";
    static final String KEY_Market = "market";
    static final String KEY_DATE   = "resDate";
    static final String KEY_MARKETID = "marketid";
    static final String KEY_NOTE = "note";
    SharedPreferences sharedpreferences;
    ProgressDialog dialog;
    int user_id;
    TextView reqText , reqDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_details);

        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        reqText = (TextView)findViewById(R.id.reqText);
        reqText.setText(getIntent().getStringExtra("req"));

        reqDate = (TextView)findViewById(R.id.reqDate);
        reqDate.setText(getIntent().getStringExtra("reqDate"));


        list = (ListView)findViewById(R.id.list);

        // getting the categories from the server
        //create the queue to put on it the requests
        RequestQueue queue = Volley.newRequestQueue(this);


        // url address to connect to the server
        String url = getString(R.string.server_connect) + "/GetRequest";

        //send a get request to the server
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    //actions in the response object handled
                    @Override
                    public void onResponse(String response) {

                        ReplyDetails.this.dialog.dismiss();

                        ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();


                        try {

                            // put the response object in the jsonobject to be processed
                            JSONObject json = new JSONObject(response);

                            try {

                                // get the categories array from the jsonObject
                                JSONArray jsonMainNode = json.optJSONArray("req");
                                // Process each JSON Node
                                for (int i = jsonMainNode.length()-1; i >= 0 ; i--) {

                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    String marketName = jsonChildNode.optString("name").toString();
                                    String resp;
                                    if(jsonChildNode.optString("response").toString().matches("f"))
                                         resp ="NOT AVAILABLE";
                                    else
                                        resp = "AVAILABLE";
                                    String marketid = jsonChildNode.optString("marketid").toString();
                                    String resdate = jsonChildNode.optString("resdate").toString();
                                    String note = jsonChildNode.optString("note").toString();

                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(KEY_RES, resp);
                                    map.put(KEY_Market, marketName);
                                    map.put(KEY_DATE, resdate);
                                    map.put(KEY_MARKETID, marketid);
                                    map.put(KEY_NOTE, note);
                                    codeList.add(map);





                                }


                                RepliesAdapter adapter = new RepliesAdapter(ReplyDetails.this, codeList);

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

                        ReplyDetails.this.dialog.dismiss();
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
                params.put("reqtype","4");
                params.put("dvno", FirebaseInstanceId.getInstance().getToken().toString());
//                params.put("dvno", "12345");
                params.put("reqid",getIntent().getStringExtra("reqid"));



                return params;
            }

        };
        queue.add(getRequest);


        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                // get the categ id and run the next activity
                TextView marketid = (TextView) view.findViewById(R.id.marketid);




                Intent intent = new Intent(getBaseContext() , ShowMarketData.class);
                intent.putExtra("marketid" , marketid.getText().toString());




                startActivityForResult(intent,1);

            }
        });
    }
}
