package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
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

public class ShowCateg extends AppCompatActivity {


    // All static variables

    // XML node keys

    static final String KEY_ID = "id";
    static final String KEY_Categ = "categname";

    ProgressDialog dialog;
    ListView list;
    NewAddapter adapter;
    JSONObject json;
    JSONArray jsonMainNode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_categ);

        dialog = ProgressDialog.show(ShowCateg.this, "",
                "Loading. Please wait...", true);

        // getting the categories from the server
        //create the queue to put on it the requests
        RequestQueue queue = Volley.newRequestQueue(this);


        // url address to connect to the server
        String url = getString(R.string.server_connect) +"/ShowCategoreis";

        // create a list view object
        list  = (ListView)findViewById(R.id.list);


        //send a get request to the server
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    //actions in the response object handled
                    @Override
                    public void onResponse(String response) {

                        ShowCateg.this.dialog.dismiss();

                        ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();


//                        Toast.makeText(getBaseContext(),response.toString(),Toast.LENGTH_LONG).show();

                        try {

                            // put the response object in the jsonobject to be processed
                            json = new JSONObject(response);

                            try{

                                // get the categories array from the jsonObject
                                jsonMainNode = json.optJSONArray("categ");
                                // Process each JSON Node
                                for(int i=0; i < jsonMainNode.length(); i++){

                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    String id = jsonChildNode.optString("id").toString();
                                    String name   = jsonChildNode.optString("name").toString();

                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(KEY_Categ, name);
                                    map.put(KEY_ID,id);
                                    codeList.add(map);

                                }


                                adapter = new NewAddapter(ShowCateg.this , codeList);

                                list.setAdapter(adapter);
                            }
                            catch (Exception e){

                                // some thing gose wrong will loading categories

                                Context context = getApplicationContext();
                                CharSequence text = "please check your internet connection and try again";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                            }

                        }
                        catch (Exception e) {
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

                        ShowCateg.this.dialog.dismiss();
                        // error
                        Context context = getApplicationContext();
                        CharSequence text = "a problem detected will connecting to server";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
        ) {

        };
        queue.add(getRequest);



        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get the categ id and run the next activity
                TextView idctg = (TextView) view.findViewById(R.id.idctg);
                Intent intent = new Intent(getBaseContext() , SendReq.class);
                intent.putExtra("idctg" , idctg.getText().toString());
               int i = Integer.parseInt(adapter.getItem(position)+"");
                try{
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);


                    intent.putExtra("ctgText",jsonChildNode.optString("name").toString());
                }
                catch (Exception e){
                    intent.putExtra("ctgText","");


                }


                startActivityForResult(intent,1);
                finish();

            }
        });

    }
}
