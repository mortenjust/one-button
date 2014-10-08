package com.onebutton.util;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Network utility.
 */
public class Network {

    // TAGs are used to show the origin of a log message.
    public static final String TAG = Network.class.getSimpleName();

    /**
     * Get Json through Http.
     *
     * @param uri the uri.
     * @return the returned json or null.
     */
    public static String executeHttpGet(Uri uri) {
        String jsonReply;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(uri.toString());

            Logger.v(TAG, "Url to download: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                jsonReply = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                jsonReply = null;
            }
            jsonReply = buffer.toString();
        } catch (IOException e) {
            Logger.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            jsonReply = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Logger.e(TAG, "Error closing stream", e);
                }
            }
        }
        Logger.v(TAG, "JsonReply:  " + jsonReply);
        return  jsonReply;
    }


}
