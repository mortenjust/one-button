package com.onebutton;

import android.app.Activity;
import android.os.Bundle;

/**
 * Main activity.
 * <p/>
 * Loads the fragment.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new RankedShows()).commit();
        }
    }
}
