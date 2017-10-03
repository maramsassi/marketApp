package com.example.maram.marketinpoket;

/**
 * Created by root on 06/05/17.
 */


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;


public class MyAdapter extends BaseAdapter  {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    private JSONArray ids =null;



    // the constructor specify the activity and the data that will be shown in the adapter
    public MyAdapter(Activity a,  ArrayList<HashMap<String, String>> d , JSONArray ids
    ) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ids = ids;

    }



    // returns the size or the number of elements on the list
    public int getCount() {
        return data.size();


    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // returns on element in the specified position
    public Object getItem(int position) {


        return position;
    }




    public long getItemId(int position) {
        return position;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
//        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView categname = (TextView)vi.findViewById(R.id.title);
        TextView idctg = (TextView) vi.findViewById(R.id.idctg);

        HashMap<String, String> category = new HashMap<String, String>();

        Drawable Sellected = activity.getDrawable(R.drawable.gradient_bg_hover);
        Drawable notSellected = activity.getDrawable(R.drawable.gradient_bg);
//
            category = data.get(position);

            // Setting all values in listview
            categname.setText(category.get(ShowCateg.KEY_Categ));
            idctg.setText(category.get(ShowCateg.KEY_ID));












        return vi;
    }
}
