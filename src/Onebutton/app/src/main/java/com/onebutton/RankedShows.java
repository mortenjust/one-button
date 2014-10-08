package com.onebutton;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.onebutton.domain.Channel;
import com.onebutton.domain.Show;
import com.onebutton.util.Logger;
import com.onebutton.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * This Fragment shows the ranked shows.
 */
public class RankedShows extends Fragment {

    // TAGs are used to show the origin of a log message.
    public static final String TAG = RankedShows.class.getSimpleName();

    // Array adapter that holds the list items to display.
    private ArrayAdapter<String> arrayAdapter;
    // Concurrent map that holds all the information to display.
    private ConcurrentMap<Channel, Show> concurrentMap = new ConcurrentHashMap<Channel, Show>();

    // Public constructor
    public RankedShows() {
    }

    @Override
    public void onStart() {
        super.onStart();
        rankShows();
    }

    /**
     * Initiate the ranking.
     */
    private void rankShows() {
        FetchChannelsAndShowsTask fetchChannelsAndShowsTask = new FetchChannelsAndShowsTask();
        fetchChannelsAndShowsTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> runningShows = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_tvshows, R.id.list_item_tvshows_textview, runningShows);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_tvshows);
        listView.setAdapter(arrayAdapter);

        return rootView;
    }

    /**
     * Updates the list item.
     */
    private void updateListItem() {
        synchronized (arrayAdapter) {
            arrayAdapter.clear();

            SortedMap<Channel, Show> sortedData = new TreeMap<Channel, Show>(new ValueComparer(concurrentMap));

            sortedData.putAll(concurrentMap);
            Logger.v(TAG, "----");
            ArrayList<String> al = new ArrayList<String>();
            for (Iterator iter = sortedData.keySet().iterator(); iter.hasNext(); ) {
                Channel channel = (Channel) iter.next();
                Show show = sortedData.get(channel);
                Logger.v(TAG, "Value/key:" + sortedData.get(channel) + "/" + channel);
                al.add(channel.getNumber() + " " + channel.getName() + " " + show.getTitle() + "(" + show.getRating() +")");
            }
            Logger.v(TAG, "----");
            arrayAdapter.addAll(al);
        }
    }

    /**
     * inner class to do sorting of the map *
     */
    private static class ValueComparer implements Comparator {
        private Map _data = null;

        public ValueComparer(Map data) {
            super();
            _data = data;
        }

        public int compare(Object o1, Object o2) {
            Float e1 = ((Show) _data.get(o1)).getRating();
            Float e2 = ((Show) _data.get(o2)).getRating();

            int compare = e2.compareTo(e1);
            if (compare == 0) {
                compare = (((Channel) o1)).getName().compareTo((((Channel) o2).getName()));
            }
            return compare;
        }
    }

    /**
     * Fetch Scores.
     */
    public class FetchScoresTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objs) {

            // Cast params.
            Show show = (Show) objs[0];
            Channel channel = (Channel) objs[1];

            String imdbScoreJsonStr;

            // Example Uri:
            // http://www.omdbapi.com/?t=Daniel%20Tiger%27s%20Neighborhood
            final String FORECAST_BASE_URL = "http://www.omdbapi.com/";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter("t", show.getTitle())
                    .build();

            // Perform http request to retrieve json.
            imdbScoreJsonStr = Network.executeHttpGet(builtUri);

            try {
                JSONObject imdbJson = new JSONObject(imdbScoreJsonStr);
                if (imdbJson != null && imdbJson.has("imdbRating")) {
                    String rating = imdbJson.getString("imdbRating");
                    if (rating == null || "".equals(rating) || "N/A".equals(rating)) {
                        show.setRating(0.0f);
                    } else {
                        show.setRating(Float.parseFloat(rating));
                    }

                    concurrentMap.put(channel, show);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            updateListItem();
        }
    }

    /**
     * Fetch channels and running shows.
     */
    public class FetchChannelsAndShowsTask extends AsyncTask<Void, Void, Void> {

        // Map that holds channels (key) and the show (value).
        private Map<Channel, Show> channelShows = new HashMap<Channel, Show>();

        @Override
        protected Void doInBackground(Void... voids) {

            String tvshowsJsonStr;

            // Example Uri:
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

            tvshowsJsonStr = Network.executeHttpGet(builtUri);

            try {
                // Convert the string into json object.
                JSONArray tvshowsJson = new JSONArray(tvshowsJsonStr);
                if (tvshowsJson != null) {
                    for (int i = 0; i < tvshowsJson.length(); i++) {

                        Channel channel = new Channel();
                        Show show = new Show();

                        JSONObject currentObject = tvshowsJson.getJSONObject(i);
                        if (currentObject != null) {

                            // Get the JSON object representing the channel
                            JSONObject channelJson = currentObject.getJSONObject("Channel");

                            if (channelJson != null) {
                                if (channelJson.has("Name")) {
                                    channel.setName(channelJson.getString("Name"));

                                }
                                if (channelJson.has("Number")) {
                                    channel.setNumber(channelJson.getString("Number"));
                                }
                            }

                            // Get the JSON object representing the show
                            JSONArray programSchedules = currentObject.getJSONArray("ProgramSchedules");
                            if (programSchedules != null) {
                                JSONObject currentShow = programSchedules.getJSONObject(0);
                                if (currentShow.has("Title")) {
                                    show.setTitle(currentShow.getString("Title"));
                                }

                                if (currentShow.has("CatId")) {
                                    show.setCategory(currentShow.getInt("CatId"));
                                }
                            }

                            Logger.v(TAG, channel.getName() + " " + show.getTitle());
                            // doing this so I can copy paste the debug info into the pitch deck


                            // this is a hacky way of getting rid of HD, expensive package channels, and reduce load time
                            if (channel.getNumber() != "" && Integer.parseInt(channel.getNumber()) < 100) {
                                channelShows.put(channel, show);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void a) {
            for (Map.Entry<Channel, Show> entry : channelShows.entrySet()) {
                FetchScoresTask fetchScoresTask = new FetchScoresTask();
                fetchScoresTask.execute(entry.getValue(), entry.getKey());
            }
        }
    }
}