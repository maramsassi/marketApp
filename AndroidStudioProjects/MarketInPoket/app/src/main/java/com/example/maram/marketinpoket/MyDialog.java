package com.example.maram.marketinpoket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

/**
 * Created by root on 25/07/17.
 */

public class MyDialog {

    public void showDialog(Context context, String dialogMessage , String option) throws Exception
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(dialogMessage );

        builder.setPositiveButton(option, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });


        builder.show();
    }
}
