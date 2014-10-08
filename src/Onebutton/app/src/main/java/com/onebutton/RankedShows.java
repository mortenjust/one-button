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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RankedShows extends Fragment {

    public static final String TAG = RankedShows.class.getSimpleName();

    private ArrayAdapter<String> arrayAdapter;

    public RankedShows() {
    }

    @Override
    public void onStart() {
        super.onStart();
        rankShows();
    }

    private void rankShows() {

        FetchAndRankTvshowsTask fetchAndRankTvshowsTask = new FetchAndRankTvshowsTask();
        fetchAndRankTvshowsTask.execute();
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

    private ConcurrentMap<String, Float> concurrentMap = new ConcurrentHashMap<String, Float>();

    private void updateIt() {
        synchronized(arrayAdapter) {
            arrayAdapter.clear();

            SortedMap sortedData = new TreeMap(new ValueComparer(concurrentMap));

            sortedData.putAll(concurrentMap);
            System.out.println("---");
            ArrayList<String> al = printMap(sortedData);
            System.out.println("---");
            arrayAdapter.addAll(al);
        }
    }

    private ArrayList<String> printMap(Map data) {
        ArrayList<String> al = new ArrayList<String>();
        for (Iterator iter = data.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            System.out.println("Value/key:" + data.get(key) + "/" + key);
            al.add(key + " (" + data.get(key) + ")");
        }
        return al;
    }

    public class FetchScoresTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String title = strings[0];
            String channel = strings[1];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String imdbScoreJsonStr = null;

            // http://www.omdbapi.com/?t=Daniel%20Tiger%27s%20Neighborhood


            try {


                final String FORECAST_BASE_URL = "http://www.omdbapi.com/";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter("t", title)
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
                    imdbScoreJsonStr = null;
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
                    imdbScoreJsonStr = null;
                }
                imdbScoreJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                imdbScoreJsonStr = null;
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

           // Log.v(TAG, imdbScoreJsonStr);
            try {
                JSONObject imdbJson = new JSONObject(imdbScoreJsonStr);
                if (imdbJson.has("imdbRating")) {
                    String rating = imdbJson.getString("imdbRating");
                    if (!"N/A".equals(rating)) {
                        concurrentMap.put(channel + " " + title + " ", Float.parseFloat(rating));
                    } else {
                        concurrentMap.put(channel + "  " + title + " ", 0.0f);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            updateIt();
        }
    }

    public class FetchAndRankTvshowsTask extends AsyncTask<Void, Void, Void> {

        private Map<String, String> titleChannels = new HashMap<String, String>();

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String tvshowsJsonStr = null;

            // http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/80004/start/1412681400/duration/120
            // ?ChannelFields=Name%2CFullName%2CNumber%2CSourceId
            // &ScheduleFields=ProgramId%2CEndTime%2CStartTime%2CTitle%2CAiringAttrib%2CCatId
            // &formattype=json
            // &disableChannels=music%2Cppv%2C24hr

            String timezone = "70178.16777216";
            String start = Long.toString(System.currentTimeMillis() / 1000);
            String duration = Integer.toString(120);
            String channelFields = "Name,FullName,Number,SourceId";
            String scheduleFields = "ProgramId,EndTime,StartTime,Title,AiringAttrib,CatId";
            String formattype = "json";
            String disableChannels = "music,ppv,24hr";

            try {

                final String FORECAST_BASE_URL =
                        "http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/" +
                                timezone + "/start/" + start + "/duration/" + duration + "?";
                final String CHANNELFIELDS_PARAM = "ChannelFields";
                final String SCHEDULEFIELDS_PARAM = "ScheduleFields";
                final String FORMATTYPE_PARAM = "formattype";
                final String DISABLECHANNELS_PARAM = "disableChannels";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(CHANNELFIELDS_PARAM, channelFields)
                        .appendQueryParameter(SCHEDULEFIELDS_PARAM, scheduleFields)
                        .appendQueryParameter(FORMATTYPE_PARAM, formattype)
                        .appendQueryParameter(DISABLECHANNELS_PARAM, disableChannels)
                        .build();

                URL url = new URL(builtUri.toString());
              //  Log.v(TAG, "Url: " + url);

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
                    buffer.append(line);
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

//            tvshowsJsonStr = "[\n" +
//                    "{\n" +
//                    "Channel: {\n" +
//                    "FullName: \"TV Guide Network (West)\",\n" +
//                    "Name: \"TVGN\",\n" +
//                    "Number: \"2\",\n" +
//                    "SourceId: 12013\n" +
//                    "},\n" +
//                    "ProgramSchedules: [\n" +
//                    "{\n" +
//                    "AiringAttrib: 10,\n" +
//                    "CatId: 5,\n" +
//                    "EndTime: 1412704800,\n" +
//                    "ProgramId: 20173498,\n" +
//                    "StartTime: 1412701200,\n" +
//                    "Title: \"The Love Boat\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "AiringAttrib: 8,\n" +
//                    "CatId: 5,\n" +
//                    "EndTime: 1412706600,\n" +
//                    "ProgramId: 21083142,\n" +
//                    "StartTime: 1412704800,\n" +
//                    "Title: \"Humana Medicare Advantage Plans\"\n" +
//                    "},\n" +
//                    "{\n" +
//                    "AiringAttrib: 266,\n" +
//                    "CatId: 5,\n" +
//                    "EndTime: 1412708400,\n" +
//                    "ProgramId: 112811,\n" +
//                    "StartTime: 1412706600,\n" +
//                    "Title: \"Family Ties\"\n" +
//                    "}\n" +
//                    "]\n" +
//                    "}]";
        //    Log.v(TAG, tvshowsJsonStr);
            try {
                getTvshowDataFromJson(tvshowsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }



        @Override
        protected void onPostExecute(Void a) {
            for (Map.Entry<String, String> entry : titleChannels.entrySet()) {
               // Log.v(TAG, "Get score for: " + entry.getValue());
                FetchScoresTask fetchScoresTask = new FetchScoresTask();
                fetchScoresTask.execute(entry.getValue(), entry.getKey());
            }
        }


        private void getTvshowDataFromJson(String tvshowsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.

            JSONArray tvshowsJson = new JSONArray(tvshowsJsonStr);

            for (int i = 0; i < tvshowsJson.length(); i++) {
                String callSign = "";
                String channelNumber = "";
                JSONObject currentObject = tvshowsJson.getJSONObject(i);

                // Get the JSON object representing the channel
                JSONObject channel = currentObject.getJSONObject("Channel");
                JSONObject currentShow = currentObject.getJSONArray("ProgramSchedules").getJSONObject(0);

                if (channel.has("Name")) {
                    callSign = channel.getString("Name");
                    if (channel.has("Number")) {
                        channelNumber = channel.getString("Number");
                    }
                }
                if (currentShow.has("Title") && currentShow.has("CatId")) {
                    if (Integer.parseInt(channelNumber) < 100) { // this is a hacky way of getting rid of HD, expensive package channels, and reduce load time
                       // Log.v(TAG, "It is a movie!");
                        String title = currentShow.getString("Title");
                        callSign = channelNumber + " "+callSign;
                        Log.v(TAG, callSign+" "+title); // doing this so I can copy paste the debug info into the pitch deck
                        titleChannels.put(callSign, title);

                    }
                }
            }

        }

    }

    /**
     * inner class to do soring of the map *
     */
    private static class ValueComparer implements Comparator {
        private Map _data = null;

        public ValueComparer(Map data) {
            super();
            _data = data;
        }

        public int compare(Object o1, Object o2) {
            Float e1 = (Float) _data.get(o1);
            Float e2 = (Float) _data.get(o2);

            int compare = e2.compareTo(e1);
            if (compare == 0) {
                compare = ((String) o1).compareTo(((String)o2));
            }
            return compare;
            //return e2.compareTo(e1);
        }
    }
}