package com.example.maram.marketinpoket;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 16/09/17.
 */
public class DStringRequest extends StringRequest {


    String sessionId;

    public DStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener ,  String s) {
        super(method, url, listener, errorListener);
        sessionId = s;
    }

    public DStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener ) {
        super(url, listener, errorListener);




    }

    /* (non-Javadoc)
         * @see com.android.volley.Request#getHeaders()
         */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<>();
        }
        // add the session cookie
        // try to get the cookie from the shared prefs


        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("connect.sid");
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey("Set-Cookie")) {
                builder.append("; ");
                builder.append(headers.get("Set-Cookie"));
            }
            headers.put("Set-Cookie", builder.toString());
        }

        return headers;
    }
}
