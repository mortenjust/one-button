package com.onebutton.requests;

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

        final Show currentShow = mChannel.getCurrentShow();

        try {
            final JSONObject imdbJson = new JSONObject(imdbScoreJsonStr);
            if (imdbJson.has("imdbRating")) {
                String rating = imdbJson.getString("imdbRating");
                if (rating == null || "".equals(rating) || "N/A".equals(rating)) {
                    currentShow.setRating(0.0f);
                } else {
                    currentShow.setRating(Float.parseFloat(rating));
                }
            }
            if (imdbJson.has("Genre")) {
                currentShow.setGenre(imdbJson.getString("Genre"));
            }

            if (imdbJson.has("Year")) {
                currentShow.setYear(imdbJson.getString("Year"));
            }

            if (imdbJson.has("Plot")) {
                currentShow.setPlot(imdbJson.getString("Plot"));
            }

            if (imdbJson.has("imdbID")) {
                currentShow.setImdbId(imdbJson.getString("imdbID"));
            }

            if (imdbJson.has("Poster")) {
                currentShow.setPosterUrl(imdbJson.getString("Poster"));
            }

            final String FORECAST_BASE_URL = "http://www.omdbapi.com/";

            final String url = "http://api.themoviedb.org/3/find/" + currentShow.getImdbId() + "?api_key=bc14543062e3ef8391d32cb264987581&external_source=imdb_id";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    String backdropUrl = "";

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        JSONArray tvshowsJson = responseJson.getJSONArray("tv_results");

                        if (tvshowsJson.length() != 0) {
                            backdropUrl = tvshowsJson.getJSONObject(0).getString("backdrop_path");
                        }

                        JSONArray movie_results = responseJson.getJSONArray("movie_results");
                        if (movie_results.length() != 0) {
                            backdropUrl = movie_results.getJSONObject(0).getString("backdrop_path");
                        }

                        if(backdropUrl.isEmpty()){
                            backdropUrl = movie_results.getJSONObject(0).getString("poster_path");
                        }


                        backdropUrl = "http://image.tmdb.org/t/p/w780" + backdropUrl;

                        currentShow.setBackdropUrl(backdropUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.v("TAG", "For: " + url);




// <<<<<<< HEAD

/// =======

// >>>>>>> 0a7a9caa2a5b18774aece5dc56fadf0262c11112
                    }


                    mActivity.getArrayAdapter().sort(new Comparator<Channel>() {
                        @Override
                        public int compare(Channel lhs, Channel rhs) {
                            Float rating1 = lhs.getCurrentShow().getRating();
                            Float rating2 = rhs.getCurrentShow().getRating();

                            rating1 = rating1 - (lhs.getCurrentShow().getProgress()/10);
                            rating2= rating2 - (rhs.getCurrentShow().getProgress()/10);

                            if(rating1<0){rating1=0f;}
                            if(rating2<0){rating2=0f;}

                            int compare = rating2.compareTo(rating1);
                            if (compare == 0) {
                                compare = lhs.getName().compareTo(rhs.getName());
                            }
                            return compare;
                        }
                    });
                    mActivity.getArrayAdapter().notifyDataSetChanged();

                }
            }, new ErrorResponseHandler());

            RequestQueueSingleton.getInstance(mActivity.getActivity()).addToRequestQueue(stringRequest);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("ShowResponseHandler", "" + mChannel);
    }
}
