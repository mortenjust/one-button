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


public class CustomArrayAdapter extends ArrayAdapter<Channel> {
    private final Context context;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_POSTER = 0;
    private static final int VIEW_TYPE_ROW = 1;


    public CustomArrayAdapter(Context context, List<Channel> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
    }

    private int getLogoResource(String channelName) {
        String logoString = channelName.toLowerCase().replace("&", "_and_").replace("+", "plus");

        int resId = getContext().getResources().getIdentifier(logoString, "drawable", "com.onebutton");
        if (resId != 0) {
            return resId;
        }
        // return R.drawable.flag_default;
        // todo: need a default drawable for channels with no logo, using NBC for now:

        Log.v("RES", "Can't find " + channelName);
        return R.drawable.nologo;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO: Recycle convertview

        if (inflater == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (position == 0) {
            convertView = inflater.inflate(R.layout.list_first_row, null);
        } else {
            convertView = inflater.inflate(R.layout.list_row, null);
        }


        if (imageLoader == null) {
            imageLoader = RequestQueueSingleton.getInstance(context).getImageLoader();
        }

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView channelNumber = (TextView) convertView.findViewById(R.id.channelNumber);
        ImageView channelLogo = (ImageView) convertView.findViewById(R.id.channelLogo);


        // getting movie data for the row
        //Movie m = movieItems.get(position);

        Channel channel = getItem(position);
        Log.v("Adapter", "" + position);

        Show currentShow = channel.getCurrentShow();
        // long now = System.currentTimeMillis()/1000;
        // long fullTime = currentShow.getEndtime() - currentShow.getStarttime();


        // thumbnail image
        // title and thumbnail
        if (position == 0) {

            // big show at the top:
            String backdropUrl = channel.getCurrentShow().getBackdropUrl();
            if (null != backdropUrl && !"N/A".equals(backdropUrl)) {
                Log.v("Adapter", channel.getCurrentShow().getTitle() + " - Setting backdrop to " + backdropUrl);
                thumbNail.setImageUrl(channel.getCurrentShow().getBackdropUrl(), imageLoader);
            }
            // small shows in the list:
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