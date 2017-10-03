package com.example.maram.marketinpoket;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 20/06/17.
 */

public class NotaficationAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;




    // the constructor specify the activity and the data that will be shown in the adapter
    public NotaficationAdapter(Activity a,  ArrayList<HashMap<String, String>> d
    ) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }



    // returns the size or the number of elements on the list
    public int getCount() {
        return data.size();


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
        if(convertView==null)
            vi = inflater.inflate(R.layout.notafication_row, null);

        TextView request = (TextView)vi.findViewById(R.id.req);
        TextView dateTime = (TextView) vi.findViewById(R.id.timeDate);
        TextView notfID = (TextView) vi.findViewById(R.id.notf_id);

        HashMap<String, String> notafications = new HashMap<String, String>();
        notafications = data.get(position);

        // Setting all values in listview
        request.setText(notafications.get(Notafication.KEY_REQ));
        dateTime.setText(notafications.get(Notafication.KEY_DATE) +" at  "+notafications.get(Notafication.KEY_Time));
        notfID.setText(notafications.get(Notafication.KEY_REQID));


        return vi;
    }

}
