package com.example.maram.marketinpoket;

/**
 * Created by root on 04/05/17.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapter extends BaseAdapter  {

    private Activity activity;



    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d
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
            vi = inflater.inflate(R.layout.notafication_row, null);

        TextView request = (TextView)vi.findViewById(R.id.req);
        TextView dateTime = (TextView) vi.findViewById(R.id.timeDate);
        TextView notfID = (TextView) vi.findViewById(R.id.notf_id);
        ImageView img =(ImageView) vi.findViewById(R.id.img);


        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        // Setting all values in listview
        if(song.get("img").matches("1")){

//            img.setImageResource(R.drawable.bin);
            img.setVisibility(View.INVISIBLE);

        }
        request.setText(song.get(Notafication.KEY_REQ));
        dateTime.setText(song.get(Notafication.KEY_DATE) );//+" at  "+song.get(Notafication.KEY_Time));
        notfID.setText(song.get(Notafication.KEY_REQID));

        return vi;
    }
}
