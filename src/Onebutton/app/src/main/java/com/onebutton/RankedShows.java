package com.onebutton;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.melnykov.fab.FloatingActionButton;
import com.onebutton.domain.Channel;
import com.onebutton.listview.CustomArrayAdapter;
import com.onebutton.requests.ChannelResponseHandler;
import com.onebutton.requests.ErrorResponseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This Fragment shows the ranked shows.
 */
public class RankedShows extends Fragment {

    public static final String PREFS_NAME = "MyPrefsFile";


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
        if (arrayAdapter == null || arrayAdapter.getCount() == 0) {
            fetchChannels();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchChannels();
    }

    private void fetchChannels() {
        // Example Uri:
        // http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/80004/start/1412681400/duration/120
        // ?ChannelFields=Name%2CFullName%2CNumber%2CSourceId
        // &ScheduleFields=ProgramId%2CEndTime%2CStartTime%2CTitle%2CAiringAttrib%2CCatId
        // &formattype=json
        // &disableChannels=music%2Cppv%2C24hr

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        String serviceProviderAndDevice = settings.getString("serviceProviderAndDevice", "");
        String start = Long.toString(System.currentTimeMillis() / 1000);
        String duration = Integer.toString(120);
        String channelFields = "Name,FullName,Number,SourceId";
        String scheduleFields = "ProgramId,EndTime,StartTime,Title,AiringAttrib,CatId";
        String formattype = "json";
        String disableChannels = "music,ppv,24hr";


        final String FORECAST_BASE_URL =
                "http://mobilelistings.tvguide.com/Listingsweb/ws/rest/schedules/" +
                        serviceProviderAndDevice + "/start/" + start + "/duration/" + duration + "?";
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, builtUri.toString(), new ChannelResponseHandler(runningChannels, this), new ErrorResponseHandler());
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rankedshows, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.v(TAG, "Refresh.");
            arrayAdapter.clear();
            fetchChannels();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment.
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_tvshows);
        arrayAdapter = new CustomArrayAdapter(getActivity(), runningChannels);
        mListView.setAdapter(arrayAdapter);



        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.button_floating_action);
        floatingActionButton.attachToListView(mListView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        return rootView;
    }


    public CustomArrayAdapter getArrayAdapter() {
        return arrayAdapter;
    }

}