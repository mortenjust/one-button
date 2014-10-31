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

    private ViewHolder mViewHolder;


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

        if (convertView == null) {
            mViewHolder = new ViewHolder();
            if (position == 0) {
                convertView = mInflater.inflate(R.layout.list_first_row, null);
                mViewHolder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            } else {
                convertView = mInflater.inflate(R.layout.list_row, null);
                mViewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            }
            mViewHolder.title = (TextView) convertView.findViewById(R.id.title);
            mViewHolder.rating = (TextView) convertView.findViewById(R.id.rating);
            mViewHolder.channelNumber = (TextView) convertView.findViewById(R.id.channelNumber);
            mViewHolder.channelLogo = (ImageView) convertView.findViewById(R.id.channelLogo);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Channel channel = getItem(position);
        Show currentShow = channel.getCurrentShow();

        // Position 0 is different.
        if (position == 0) {
            String imageUrl = currentShow.getBackdropUrl();
            if (null == imageUrl || "N/A".equals(imageUrl)) {
                imageUrl = currentShow.getPosterUrl();
            }
            mViewHolder.thumbnail.setImageUrl(imageUrl, mImageLoader);
        } else {
            mViewHolder.progressBar.setProgress(currentShow.getProgress());
        }

        mViewHolder.channelLogo.setImageResource(getLogoResource(channel.getName()));
        mViewHolder.channelNumber.setText((position + 1) + " (" + channel.getNumber() + ")");
        mViewHolder.rating.setText(String.valueOf(currentShow.getRating()));
        mViewHolder.title.setText(currentShow.getTitle());
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