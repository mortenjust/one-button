package com.onebutton.requests;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.onebutton.RankedShows;
import com.onebutton.RequestQueueSingleton;
import com.onebutton.domain.Channel;
import com.onebutton.domain.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;



public class ChannelResponseHandler implements Response.Listener<String> {

    private List<Channel> mRunningChannels;
    private RankedShows mContext;

    public List<Channel> getmRunningChannels() {
        return mRunningChannels;
    }

    public ChannelResponseHandler(List<Channel> runningChannels, RankedShows context) {
        mRunningChannels = runningChannels;
        mContext = context;
    }

    private static final String TAG = ChannelResponseHandler.class.getSimpleName();

    @Override
    public void onResponse(String response) {

        try {
            if (response != null) {

                // Convert the string into json object.
                JSONArray tvshowsJson = new JSONArray(response);
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

                            if (currentShow.has("StartTime")) {
                                show.setStarttime(currentShow.getLong("StartTime"));
                            }

                            if (currentShow.has("EndTime")) {
                                show.setEndtime(currentShow.getLong("EndTime"));
                            }
                        }

                        Log.v(TAG, channel.getName() + " " + show.getTitle());
                        // doing this so I can copy paste the debug info into the pitch deck

                        channel.setCurrentShow(show);

                        // this is a hacky way of getting rid of HD, expensive package channels, and reduce load time
                        if (channel.getNumber() != null && !"".equals(channel.getNumber()) && Integer.parseInt(channel.getNumber()) < 100) {

                            mRunningChannels.add(channel);


                            // Get show info
                            // Example Uri:
                            // http://www.omdbapi.com/?t=Daniel%20Tiger%27s%20Neighborhood
                            final String FORECAST_BASE_URL = "http://www.omdbapi.com/";

                            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                    .appendQueryParameter("t", show.getTitle())
                                    .build();

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, builtUri.toString(), new ShowResponseHandler(channel, mContext), new ErrorResponseHandler());
                            RequestQueueSingleton.getInstance(mContext.getActivity()).addToRequestQueue(stringRequest);

                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
