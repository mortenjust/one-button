package com.onebutton.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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



    public CustomArrayAdapter(Context context, List<Channel> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
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

        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);




        // getting movie data for the row
        //Movie m = movieItems.get(position);

        Channel channel = getItem(position);
        Log.v("Adapter", "" + position);

        Show currentShow = channel.getCurrentShow();
        long now = System.currentTimeMillis()/1000;
        long fullTime = currentShow.getEndtime() - currentShow.getStarttime();
        progressBar.setProgress((int)(((now - currentShow.getStarttime()) * 100)/fullTime));

        // removing the nopicture image for now. TODO: thumbNail.setImageResource(resource_id);
        // thumbNail.setImageUrl("http://www.classicposters.com/images/nopicture.gif", imageLoader);
        // thumbnail image
        String posterUrl = channel.getCurrentShow().getPosterUrl();
        if (null != posterUrl && !"N/A".equals(posterUrl)) {
            Log.v("Adapter" , channel.getCurrentShow().getTitle() + " - Setting image to " + channel.getCurrentShow().getPosterUrl());
            thumbNail.setImageUrl(channel.getCurrentShow().getPosterUrl(), imageLoader);
        }
        // title
        title.setText(channel.getCurrentShow().getTitle()+" on "+channel.getNumber());

        // genre
        rating.setText(channel.getCurrentShow().getGenre()+", "+String.valueOf(channel.getCurrentShow().getRating()));

        // genre
        // genre.setText();

        // release year -- keeping it clean for now, not sure if Year provides enough context to make me change my mind about a show. I could be wrong.
       // year.setText(String.valueOf(channel.getCurrentShow().getYear()));

        return convertView;

    }
}