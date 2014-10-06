package com.onebutton;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RunningShows extends Fragment {


    public static final String TAG = RunningShows.class.getSimpleName();

    private ArrayAdapter<String> arrayAdapter;

    public RunningShows() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTvshows();
    }

    private void updateTvshows() {
        FetchTvshowsTask fetchTvshowsTask = new FetchTvshowsTask();
        fetchTvshowsTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> runningShows = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_tvshows, R.id.list_item_tvshows_textview, runningShows);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_tvshows);
        listView.setAdapter(arrayAdapter);

        return rootView;
    }

    public class FetchTvshowsTask extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String tvshowsJsonStr = null;

            String time = Long.toString(System.currentTimeMillis() / 1000);
            String lineupid = "USA-DFLTP";
            String zip = Integer.toString(94114);
            String timezone = "US/Pacific";
            String searchId = "";

            try {
                // http://www.zap2it.com/tvgrid/_xhr/schedule?time=1412623694&lineupid=USA-DFLTP&zip=94114&tz=US%2FPacific&searchId=

                final String FORECAST_BASE_URL =
                        "http://www.zap2it.com/tvgrid/_xhr/schedule?";
                final String TIME_PARAM = "time";
                final String LINEUPID_PARAM = "lineupid";
                final String ZIP_PARAM = "zip";
                final String TIMEZONE_PARAM = "tz";
                final String SEARCHID_PARAM = "searchId";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(TIME_PARAM, time)
                        .appendQueryParameter(LINEUPID_PARAM, lineupid)
                        .appendQueryParameter(ZIP_PARAM, zip)
                        .appendQueryParameter(TIMEZONE_PARAM, timezone)
                        .appendQueryParameter(SEARCHID_PARAM, searchId)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(TAG, "Url: " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    tvshowsJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    tvshowsJsonStr = null;
                }
                tvshowsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                tvshowsJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            Log.v(TAG, tvshowsJsonStr);
            try {
                return getTvshowDataFromJson(tvshowsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            arrayAdapter.clear();
            arrayAdapter.addAll(strings);
        }

        private String[] getTvshowDataFromJson(String tvshowsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONObject tvshowsJson = new JSONObject(tvshowsJsonStr);

            JSONArray tvshowsArray = tvshowsJson.getJSONObject("data").getJSONObject("results").getJSONArray("stations");


            String[] resultStrs = new String[tvshowsArray.length()];
            for (int i = 0; i < tvshowsArray.length(); i++) {


                String callSign = "";

                // Get the JSON object representing the tvshow
                JSONObject tvshow = tvshowsArray.getJSONObject(i);

                if (tvshow.has("callSign")) {
                    callSign = tvshow.getString("callSign");
                }

                resultStrs[i] = callSign;
            }

            return resultStrs;
        }
    }
}
