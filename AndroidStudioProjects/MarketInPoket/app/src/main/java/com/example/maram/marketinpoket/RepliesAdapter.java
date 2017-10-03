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
 * Created by root on 23/07/17.
 */

public class RepliesAdapter extends BaseAdapter {

    private Activity activity;



    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public RepliesAdapter(Activity a, ArrayList<HashMap<String, String>> d
    ) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();


    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.reply_detail_row, null);

        TextView marketName = (TextView)vi.findViewById(R.id.marketName);
        TextView resDate = (TextView) vi.findViewById(R.id.resDate);
        TextView marketid = (TextView) vi.findViewById(R.id.marketid);
        TextView note = (TextView) vi.findViewById(R.id.note);

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        // Setting all values in listview
        marketName.setText(song.get(ReplyDetails.KEY_Market));
        resDate.setText(song.get(ReplyDetails.KEY_DATE));
        marketid.setText(song.get(ReplyDetails.KEY_MARKETID));
        note.setText(song.get(ReplyDetails.KEY_NOTE));

        return vi;
    }
}
