package com.onebutton.requests;

import android.util.Log;

import com.android.volley.Response;
import com.onebutton.RankedShows;
import com.onebutton.domain.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;



/**
 * Created by flow on 10/11/14.
 */
public class ShowResponseHandler implements Response.Listener<String> {

    private Channel mChannel;
    private RankedShows mActivity;

    public ShowResponseHandler(Channel channel, RankedShows activity) {
        mChannel = channel;
        mActivity = activity;
    }

    @Override
    public void onResponse(String imdbScoreJsonStr) {

        try {
            JSONObject imdbJson = new JSONObject(imdbScoreJsonStr);
            if (imdbJson.has("imdbRating")) {
                String rating = imdbJson.getString("imdbRating");
                if (rating == null || "".equals(rating) || "N/A".equals(rating)) {
                    mChannel.getCurrentShow().setRating(0.0f);
                } else {
                    mChannel.getCurrentShow().setRating(Float.parseFloat(rating));
                }
            }
            if (imdbJson.has("Genre")) {
                mChannel.getCurrentShow().setGenre(imdbJson.getString("Genre"));
            }

            if (imdbJson.has("Year")) {
                mChannel.getCurrentShow().setYear(imdbJson.getString("Year"));
            }

            if (imdbJson.has("Plot")) {
                mChannel.getCurrentShow().setPlot(imdbJson.getString("Plot"));
            }



            if (imdbJson.has("imdbID")) {
                mChannel.getCurrentShow().setImdbId(imdbJson.getString("imdbID"));

                // TODO: a real request to the tmdb api instead of this hack
                mChannel.getCurrentShow().setBackdropUrl("http://mortenjust.com/one-button/image.php?id="+imdbJson.getString("imdbID"));
            }



            if (imdbJson.has("Poster")) {
                mChannel.getCurrentShow().setPosterUrl(imdbJson.getString("Poster")+"@@._V4_SX2000.jpg"); //morten added this stuff to the end because he thinks we can get higher resolution posters that way. Call him crazy. Call him an optimist. Call him on Skype.
            }


            mActivity.getArrayAdapter().sort(new Comparator<Channel>() {
                @Override
                public int compare(Channel lhs, Channel rhs) {
                    Float rating1 = lhs.getCurrentShow().getRating();
                    Float rating2 = rhs.getCurrentShow().getRating();

                    int compare = rating2.compareTo(rating1);
                    if (compare == 0) {
                        compare = lhs.getName().compareTo(rhs.getName());
                    }
                    return compare;
                }
            });
            mActivity.getArrayAdapter().notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("ShowResponseHandler", "" + mChannel);
    }
}
