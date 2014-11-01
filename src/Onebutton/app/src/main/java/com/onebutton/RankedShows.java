package com.onebutton;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.melnykov.fab.FloatingActionButton;
import com.onebutton.domain.Channel;
import com.onebutton.domain.ChannelComparator;
import com.onebutton.listview.CustomArrayAdapter;
import com.onebutton.requests.ChannelResponseHandler;
import com.onebutton.requests.ErrorResponseHandler;
import com.onebutton.requests.ShowResponseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This Fragment shows the ranked shows.
 */
public class RankedShows extends Fragment implements ShowResponseHandler.Callback {

    private static final String PREFS_NAME = "MyPrefsFile";

    private static final String TAG = RankedShows.class.getSimpleName();
    private ListView mListView;
    private CustomArrayAdapter arrayAdapter;
    private List<Channel> runningChannels = new ArrayList<Channel>();
    private View rootView;

    // Public constructor
    public RankedShows() {
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        String serviceProviderAndDevice =
                settings.getString(getString(R.string.service_provider_setting), "");

        if ("".equals(serviceProviderAndDevice)) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        } else {
            runningChannels.clear();
            fetchChannels();
        }
    }

    private void fetchChannels() {

        // Example Uri:
        // http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/80004/start/1412681400
        // /duration/120?ChannelFields=Name%2CFullName%2CNumber%2CSourceId
        // &ScheduleFields=ProgramId%2CEndTime%2CStartTime%2CTitle%2CAiringAttrib%2CCatId
        // &formattype=json
        // &disableChannels=music%2Cppv%2C24hr

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        String serviceProviderAndDevice =
                settings.getString(getString(R.string.service_provider_setting), "");
        String start = Long.toString(System.currentTimeMillis() / 1000);
        String duration = Integer.toString(120);
        String channelFields = "Name,FullName,Number,SourceId";
        String scheduleFields = "ProgramId,EndTime,StartTime,Title,AiringAttrib,CatId";
        String formattype = "json";
        String disableChannels = "music,ppv,24hr";


        final String FORECAST_BASE_URL =
                "http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/" +
                        serviceProviderAndDevice + "/start/" + start + "/duration/" + duration +
                        "?";
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
        Log.v(TAG, "URL: " + builtUri);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, builtUri.toString(),
                new ChannelResponseHandler(runningChannels, this), new ErrorResponseHandler());
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment.
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_tvshows);
        arrayAdapter = new CustomArrayAdapter(getActivity(), runningChannels);
        mListView.setAdapter(arrayAdapter);


        FloatingActionButton floatingActionButton = (FloatingActionButton)
                rootView.findViewById(R.id.button_floating_action);
        floatingActionButton.attachToListView(mListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        final android.os.Handler h = new Handler();
        h.postDelayed(
                new Runnable() {
                    public void run() {
                        arrayAdapter.sort(new ChannelComparator());
                        arrayAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Refreshed.", Toast.LENGTH_LONG).show();
                        h.postDelayed(this, 60000);
                    }
                },
                60000);

        return rootView;
    }

    public CustomArrayAdapter getArrayAdapter() {
        return arrayAdapter;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

}