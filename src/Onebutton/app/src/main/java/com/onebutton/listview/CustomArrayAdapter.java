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
        String logoString = channelName.toLowerCase();
        int resId = getContext().getResources().getIdentifier(logoString, "drawable", "com.onebutton");
        if(resId != 0){
            return resId;
        }
        // return R.drawable.flag_default;
        // todo: need a default drawable for channels with no logo, using NBC for now:
        return R.drawable.nbc;
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
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);
        TextView channelName = (TextView) convertView.findViewById(R.id.channelName);
        ImageView channelLogo = (ImageView) convertView.findViewById(R.id.channelLogo);

        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);




        // getting movie data for the row
        //Movie m = movieItems.get(position);

        Channel channel = getItem(position);
        Log.v("Adapter", "" + position);

        Show currentShow = channel.getCurrentShow();
        long now = System.currentTimeMillis()/1000;
        long fullTime = currentShow.getEndtime() - currentShow.getStarttime();
        progressBar.setProgress((int)(((now - currentShow.getStarttime()) * 100)/fullTime));

        // thumbnail image
        // title and thumbnail
        if(position==0) {

            // big show at the top
            String posterUrl = channel.getCurrentShow().getPosterUrl();
            if (null != posterUrl && !"N/A".equals(posterUrl)) {
                Log.v("Adapter" , channel.getCurrentShow().getTitle() + " - Setting backdrop to " + channel.getCurrentShow().getBackdropUrl());
                thumbNail.setImageUrl(channel.getCurrentShow().getBackdropUrl(), imageLoader);
                // thumbNail.setImageUrl("http://mortenjust.com/one-button/image.php?id=tt0460681", imageLoader);
            }

            title.setText(channel.getCurrentShow().getTitle() + " on " + channel.getNumber());
            rating.setText(channel.getName()+", "+String.valueOf(channel.getCurrentShow().getRating()));

            // small shows in the list
        } else {
            String posterUrl = channel.getCurrentShow().getPosterUrl();
            if (null != posterUrl && !"N/A".equals(posterUrl)) {
                Log.v("Adapter" , channel.getCurrentShow().getTitle() + " - Setting thumbnail image to " + channel.getCurrentShow().getPosterUrl());
                thumbNail.setImageUrl(channel.getCurrentShow().getPosterUrl(), imageLoader);
            }
            title.setText(channel.getCurrentShow().getTitle() + " on " + channel.getNumber());
            rating.setText(String.valueOf(channel.getCurrentShow().getRating()));
            channelName.setText(channel.getName());
            channelLogo.setImageResource(getLogoResource(channel.getName()));
        }
        // secondary text
        //rating.setText(channel.getCurrentShow().getGenre()+", "+String.valueOf(channel.getCurrentShow().getRating()));

        // genre
        // genre.setText();

        // release year -- keeping it clean for now, not sure if Year provides enough context to make me change my mind about a show. I could be wrong.
       // year.setText(String.valueOf(channel.getCurrentShow().getYear()));

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