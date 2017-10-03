package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class SpecifyCateg extends AppCompatActivity {
    JSONObject j ;
    // All static variables
    static final String URL = "http://api.androidhive.info/music/music.xml";
    ArrayList<HashMap<String, String>> codeList = new ArrayList<HashMap<String, String>>();

    // XML node keys
    static final String KEY_ID = "id";
    static final String KEY_Categ = "categname";

    JSONArray catids;
    ProgressDialog dialog1;
    SharedPreferences sharedpreferences;
    ProgressDialog dialog, dialog2;
    ListView list;

    MyAdapter adapter;
    int selected_categ =0;
    Button nextB , addctg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_categ);
        catids = new JSONArray();
        j = new JSONObject();
        // getting the categories from the server

        RequestQueue queue = Volley.newRequestQueue(this);

        nextB = (Button)findViewById(R.id.next);

        sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String sessionId = sharedpreferences.getString("session","");

        String url = getString(R.string.server_connect) +"/ShowCategoreis";
        list  = (ListView)findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {






                        try {

//
                            JSONObject json = new JSONObject(response);

                            try{

                                JSONArray jsonMainNode = json.optJSONArray("categ");
                                // Process each JSON Node
                                for(int i=0; i < jsonMainNode.length(); i++){

                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    String id = jsonChildNode.optString("id").toString();
                                    String name   = jsonChildNode.optString("name").toString();

//                                   OutputData += "Node : \n\n     "+ id +" | "
//                                            + name+" | \n\n ";
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put(KEY_Categ, name);
                                    map.put(KEY_ID,id);
                                    codeList.add(map);

                                }
//                                outputDataList.add(OutputData);
//                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowCateg.this,
//                                        android.R.layout.simple_spinner_item ,codeList);

                                adapter = new MyAdapter(SpecifyCateg.this , codeList , catids);

                                list.setAdapter(adapter);
                            }
                            catch (Exception e){

                                // some thing gose wrong will loading categories

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



//        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drawable d = getDrawable(R.drawable.gradient_bg_hover);

                if(catids.length()!=0){
                    TextView idctg = (TextView) view.findViewById(R.id.idctg);
                    try{
                    catids.put(new JSONObject().put("ctgid",Integer.parseInt(idctg.getText().toString())));
                    parent.getChildAt(position).setBackground(d);

                    }


                    catch (Exception e){

                    }



                }

          }
        }

        );
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Drawable d = getDrawable(R.drawable.gradient_bg_hover);
                Drawable d2 = getDrawable(R.drawable.gradient_bg);
                TextView idctg = (TextView) view.findViewById(R.id.idctg);
                int j = 0;
                try {
                    for (int i = 0; i < catids.length(); i++) {
                        if (catids.getJSONObject(i).optString("ctgid").equalsIgnoreCase(idctg.getText().toString())){
//                            view.setBackground(d2);
                            parent.getChildAt(position).setBackground(d2);
                            catids.remove(i);
                            j=1;
                        }
                    }
                    if(j==0){
                        catids.put(new JSONObject().put("ctgid",Integer.parseInt(idctg.getText().toString())));
//                        view.setBackground(d);
                        parent.getChildAt(position).setBackground(d);
                    }



                }
                catch (Exception e){

                }
              

                return true;
            }
        });

        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(catids.length()==0){
                    Toast.makeText(getBaseContext(),"Please, select your category first",Toast.LENGTH_LONG).show();

                }
                else
                {
                dialog = ProgressDialog.show(SpecifyCateg.this, "",
                        "Loading. Please wait...", true);




                RequestQueue queue = Volley.newRequestQueue(SpecifyCateg.this);

                String url = getString(R.string.server_connect) + "/Marketdatd";


                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {


                                dialog.dismiss();
                                try {

                                    JSONObject json = new JSONObject(response);

                                    if (json.has("error")) {

                                        // check the error messages
                                        if (Integer.parseInt(json.get("error").toString()) == 1) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "A problem detected, please try again";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        } else
                                            if((Integer.parseInt(json.get("error").toString()) == 4)){
                                                Intent intent = new Intent(getBaseContext(),Login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        else
                                            if (Integer.parseInt(json.get("error").toString()) == 2) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "A problem detected, please try again";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }
                                        else if (Integer.parseInt(json.get("error").toString()) == 3) {
                                            Context context = getApplicationContext();
                                            CharSequence text = "Sorry there was a problem while connecting to server";
                                            int duration = Toast.LENGTH_LONG;

                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        }
                                    } else {
                                        if (json.has("session")) {


                                            Intent intent = new Intent(getBaseContext(), MainActivity.class);

//                                            intent.putExtra("marketname",getIntent().getStringExtra("marketname"));
//                                            intent.putExtra("marketname",getIntent().getStringExtra("marketname"));
//                                            intent.putExtra("tel",getIntent().getStringExtra("tel"));
//                                            intent.putExtra("location", getIntent().getStringExtra("location"));

                                            sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("user_id",getIntent().getIntExtra("userid",1));
                                            editor.putString("ctgids",catids.toString());
                                            editor.putInt("market_id",json.getInt("marketid"));
                                            editor.putString("session", json.getString("session").toString());
                                            Log.d("marketid",json.getInt("marketid")+"");
                                            Log.d("categid",catids.toString()+"");
                                            Log.d("marketid",json.getInt("marketid")+"");
                                            Log.d("marketid",json.getInt("marketid")+"");
                                            Log.d("marketid",json.getInt("marketid")+"");

                                            editor.commit();

//                                            startActivityForResult(intent,1);
                                            startActivity(intent);
                                            finishAffinity();
                                        }
                                    }

                                } catch (Exception e) {
                                    if (e == null) {

                                        dialog.dismiss();

                                        Toast.makeText(getBaseContext(),
                                                "Please check your internet connection and try again",
                                                Toast.LENGTH_LONG).show();

                                    }
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                // error


                                dialog.dismiss();
                                Toast.makeText(getBaseContext(),
                                        "a problem detected will connecting to server",
                                        Toast.LENGTH_LONG).show();
                                try {
                                    showDialog(error.toString());
                                }
                                catch (Exception e){

                                }


                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
//
                        params.put("name", getIntent().getStringExtra("marketname"));
                        params.put("tel", getIntent().getStringExtra("tel"));
                        params.put("categid", catids.toString());
                        params.put("location", getIntent().getStringExtra("location"));
                        params.put("lat", getIntent().getDoubleExtra("lat",0)+"");
                        params.put("lng", getIntent().getDoubleExtra("lng",0)+"");
                        params.put("userid", getIntent().getIntExtra("userid",1)+"");
                        params.put("tokenID", FirebaseInstanceId.getInstance().getToken().toString());

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
                    postRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(postRequest);

                }


            }
        });
    }


    public void showDialog(String s) throws Exception
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SpecifyCateg.this);

        builder.setMessage(s);

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
