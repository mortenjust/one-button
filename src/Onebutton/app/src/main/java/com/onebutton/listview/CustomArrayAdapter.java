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

    private final Context mContext;
    private LayoutInflater mInflater;

    // Image loader for volley
    private ImageLoader mImageLoader;


    public CustomArrayAdapter(Context context, List<Channel> values) {
        super(context, R.layout.list_row, values);
        this.mContext = context;
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
        if (mInflater == null) {
            mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (mImageLoader == null) {
            mImageLoader = RequestQueueSingleton.getInstance(mContext).getImageLoader();
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (position == 0) {
                convertView = mInflater.inflate(R.layout.list_first_row, null);
                viewHolder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            } else {
                convertView = mInflater.inflate(R.layout.list_row, null);
                viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            }
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.channelNumber = (TextView) convertView.findViewById(R.id.channelNumber);
            viewHolder.channelLogo = (ImageView) convertView.findViewById(R.id.channelLogo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);
        Show currentShow = channel.getCurrentShow();

        // Position 0 is different.
        if (position == 0) {
            String imageUrl = currentShow.getBackdropUrl();
            if (null == imageUrl || "N/A".equals(imageUrl)) {
                imageUrl = currentShow.getPosterUrl();
            }
            viewHolder.thumbnail.setImageUrl(imageUrl, mImageLoader);
        } else {
            viewHolder.progressBar.setProgress(currentShow.getProgress());
        }

        viewHolder.channelLogo.setImageResource(getLogoResource(channel.getName()));
        viewHolder.channelNumber.setText((position + 1) + " (" + channel.getNumber() + ")");
        viewHolder.rating.setText(String.valueOf(currentShow.getRating()));
        viewHolder.title.setText(currentShow.getTitle());
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

    static class ViewHolder {
        TextView title;
        TextView rating;
        TextView channelNumber;
        ImageView channelLogo;
        ProgressBar progressBar;
        NetworkImageView thumbnail;
    }
}