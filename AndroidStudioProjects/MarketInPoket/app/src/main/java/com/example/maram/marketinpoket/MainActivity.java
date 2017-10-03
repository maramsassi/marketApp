package com.example.maram.marketinpoket;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.text.AndroidCharacter;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import android.app.NotificationManager;
import android.app.PendingIntent;


import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Random;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;


import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;


import org.json.JSONObject;


import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;

import android.provider.Settings.Secure;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private int user_id ;
    public static Context context ;
    private String session ;
    private int marketid ;
    SharedPreferences sharedpreferences;

    ProgressDialog dialog;
    LinearLayout add , res , waiting;

    FloatingActionButton sendReq;

    Intent intent ;
    public static NotificationManager mNotifyMgr;
    static final String KEY_TITLE = "title";
    static final String KEY_IMGE = "list_image";
    final MainActivity mainpage = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);




        context = this;
// Gets an instance of the NotificationManager service
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        add = (LinearLayout)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), ShowCateg.class);
                startActivity(intent);

            }
        });

        res = (LinearLayout)findViewById(R.id.res);
        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),ShowReply.class);
                intent.putExtra("reqtype",3);
                startActivityForResult(intent,1);

            }
        });

        waiting = (LinearLayout)findViewById(R.id.wait);
        waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(),ShowRequest.class);

                intent.putExtra("reqtype",2);
                startActivityForResult(intent,1);

            }
        });



        sendReq = (FloatingActionButton)findViewById(R.id.sendreq);
        sendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), ShowCateg.class);
                startActivity(intent);

            }
        });




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);










        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        try {
//         get the session id from login activity
            sharedpreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


//            session =  sharedpreferences.getString("market_id", null);
            marketid =  sharedpreferences.getInt("market_id", 0);

            if(marketid != 0){

                navigationView.getMenu().clear();

                navigationView.inflateMenu(R.menu.main_page);


                navigationView.setNavigationItemSelectedListener(MainActivity.this);

            }
            else{
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.activity_main_page_drawer);

            }



        }
        catch (Exception ex){


            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_page_drawer);

        }
        navigationView.setNavigationItemSelectedListener(this);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       if (id == R.id.nav_notafication) {
            Intent intent = new Intent(getBaseContext(), Notafication.class);
            intent.putExtra("id",getIntent().getStringExtra("id"));
            intent.putExtra("session",getIntent().getStringExtra("session"));
            intent.putExtra("reqtype",1);

            startActivityForResult(intent,1);





        } // if login menue item selected
        else if (id == R.id.nav_login) {
            Intent intent = new Intent(getBaseContext(), Login.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {

            Intent intent = new Intent(getBaseContext(), ShowProfile.class);
            startActivity(intent);

        }else if (id == R.id.about) {

        }else if (id == R.id.nav_logout) {

            showDialog();


        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public  void showDialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are You Sure, Want to logout");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Loading. Please wait...", true);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

                String url = getString(R.string.server_connect) + "/Logout";

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                MainActivity.this.dialog.dismiss();

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
                                            navigationView.getMenu().clear();
                                            navigationView.inflateMenu(R.menu.activity_main_page_drawer);
                                            session = "";
                                            user_id = 0;
                                            sharedpreferences.edit().clear().commit();




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

                        params.put("tokenID", FirebaseInstanceId.getInstance().getToken().toString());

                        return params;
                    }
                };
                queue.add(postRequest);


                dialogInterface.dismiss();

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