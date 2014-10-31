package com.onebutton.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.onebutton.R;
import com.onebutton.RequestQueueSingleton;
import com.onebutton.domain.Channel;
import com.onebutton.domain.Show;

import java.util.List;

/**
 * A custom array adapter that shows current shows.
 */
public class CustomArrayAdapter extends ArrayAdapter<Channel> {

    // Constants for view type.
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_POSTER = 0;
    private static final int VIEW_TYPE_ROW = 1;

    private static final String TAG = CustomArrayAdapter.class.getSimpleName();

    private final Context context;
    private LayoutInflater inflater;

    // Image loader for volley
    private ImageLoader imageLoader;


    public CustomArrayAdapter(Context context, List<Channel> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
    }

    private int getLogoResource(String channelName) {
        String logoString = channelName.toLowerCase().replace("&", "_and_").replace("+", "plus");

        int defaultLogo = R.drawable.nologo;
        int resId = getContext().getResources().getIdentifier(logoString, "drawable",
                "com.onebutton");

        if (resId == 0) {
            //TODO: need a default drawable for channels with no logo, using NBC for now:
            Log.v(TAG, "Can't find " + channelName);
            resId = defaultLogo;
        }

        return resId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO: Recycle convertview
        if (inflater == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (imageLoader == null) {
            imageLoader = RequestQueueSingleton.getInstance(context).getImageLoader();
        }

        if (position == 0) {
            convertView = inflater.inflate(R.layout.list_first_row, null);
        } else {
            convertView = inflater.inflate(R.layout.list_row, null);
        }


        // TODO: viewholder pattern
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView channelNumber = (TextView) convertView.findViewById(R.id.channelNumber);
        ImageView channelLogo = (ImageView) convertView.findViewById(R.id.channelLogo);

        Channel channel = getItem(position);
        Show currentShow = channel.getCurrentShow();

        // Position 0 is different.
        if (position == 0) {
            NetworkImageView thumbnail = (NetworkImageView) convertView.
                    findViewById(R.id.thumbnail);

            // Big show at the top.
            String backdropUrl = channel.getCurrentShow().getBackdropUrl();
            if (null != backdropUrl && !"N/A".equals(backdropUrl)) {
                Log.v(TAG, currentShow.getTitle() + " - Setting backdrop to " +
                        backdropUrl);
                thumbnail.setImageUrl(channel.getCurrentShow().getBackdropUrl(), imageLoader);
            } else {
                thumbnail.setImageUrl(channel.getCurrentShow().getPosterUrl(), imageLoader);
            }
        } else {
            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            progressBar.setProgress(currentShow.getProgress());
        }

        channelLogo.setImageResource(getLogoResource(channel.getName()));
        channelNumber.setText(channel.getNumber());
        rating.setText(String.valueOf(channel.getCurrentShow().getRating()));
        title.setText(channel.getCurrentShow().getTitle());
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_POSTER : VIEW_TYPE_ROW;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}