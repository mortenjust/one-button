package com.onebutton.requests;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by flow on 10/11/14.
 */
public class ErrorResponseHandler implements Response.ErrorListener {

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("Volley error (was ABC)", "Failed (dude, are you online?)", error);
    }
}
