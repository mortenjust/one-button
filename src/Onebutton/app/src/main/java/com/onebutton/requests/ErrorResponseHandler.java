package com.onebutton.requests;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Error response handler.
 */
public class ErrorResponseHandler implements Response.ErrorListener {

    private static final String TAG = ErrorResponseHandler.class.getSimpleName();
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Dude, where is your internet?", error);
    }
}
