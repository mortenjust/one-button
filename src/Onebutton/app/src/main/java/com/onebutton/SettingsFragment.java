package com.onebutton;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.onebutton.requests.ErrorResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Fragment for settings.
 */
public class SettingsFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefsFile";

    private Button mSearchButton;
    private EditText mZipCodeEditText;
    private ListView mServiceProviders;
    private ArrayList<String> mServiceId;

    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);


        mSearchButton = (Button) rootView.findViewById(R.id.search_button);
        mZipCodeEditText = (EditText) rootView.findViewById(R.id.zip_code_text);
        mServiceProviders = (ListView) rootView.findViewById(R.id.service_provider_list_view);

        mServiceId = new ArrayList<String>();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        String zip = settings.getString(getString(R.string.zip_setting), "");
        mZipCodeEditText.setText(zip);

        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Hide keyboard.
                InputMethodManager inputMgr = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.hideSoftInputFromWindow(mZipCodeEditText.getWindowToken(), 0);

                String zipCode = mZipCodeEditText.getText().toString();

                String url = "http://mobilelistings.tvguide.com/Listingsweb/ws/rest/" +
                        "serviceproviders/zipcode/" + zipCode + "?formattype=json";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("Settings", "Response" + response);

                                ArrayList<String> items = new ArrayList<String>();
                                // Convert the string into json object.
                                try {
                                    JSONArray serviceproviders = new JSONArray(response);
                                    for (int i = 0; i < serviceproviders.length(); i++) {
                                        JSONObject currentObject = serviceproviders.getJSONObject(i);
                                        JSONArray devices = currentObject.getJSONArray("Devices");
                                        for (int j = 0; j < devices.length(); j++) {
                                            JSONObject device = devices.getJSONObject(j);
                                            String entry = currentObject.getString("Type") + " - " +
                                                    currentObject.getString("Name");
                                            entry += device.getString("DeviceName");
                                            items.add(entry);
                                            mServiceId.add(currentObject.getString("Id") + "." +
                                                    device.getString("DeviceFlag"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ArrayAdapter<String> itemsAdapter =
                                        new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, items);
                                mServiceProviders.setAdapter(itemsAdapter);

                            }
                        }, new ErrorResponseHandler());
                RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
            }
        });

        mServiceProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getString(R.string.service_provider_setting), mServiceId.get(position));
                editor.putString(getString(R.string.zip_setting), mZipCodeEditText.getText().toString());
                editor.apply();
                getActivity().finish();
            }
        });

        return rootView;
    }
}
