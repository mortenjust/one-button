package com.onebutton.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.onebutton.R;
import com.onebutton.RequestQueueSingleton;
import com.onebutton.domain.Channel;

import java.util.List;


public class CustomArrayAdapter extends ArrayAdapter<Channel> {
    private final Context context;
    private final List<Channel> values;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;



    public CustomArrayAdapter(Context context, List<Channel> values) {
        super(context, R.layout.list_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // TODO: convertview

        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //if (convertView == null)
        convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader =  RequestQueueSingleton.getInstance(context).getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView rating = (TextView) convertView.findViewById(R.id.rating);
        TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView year = (TextView) convertView.findViewById(R.id.releaseYear);

        // getting movie data for the row
        //Movie m = movieItems.get(position);

        Channel channel = getItem(position);
        Log.v("Adapter", "" + position);

        if (position == 0) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(700, 700);
            thumbNail.setLayoutParams(layoutParams);
        }

        // removing the nopicture image for now. TODO: How do you use a local image here? @drawable somethingsomethign?
        // thumbNail.setImageUrl("http://www.classicposters.com/images/nopicture.gif", imageLoader);
        // thumbnail image
        String posterUrl = channel.getCurrentShow().getPosterUrl();
        if (null != posterUrl && !"N/A".equals(posterUrl)) {
            Log.v("Adapter" , channel.getCurrentShow().getTitle() + " - Setting image to " + channel.getCurrentShow().getPosterUrl());
            thumbNail.setImageUrl(channel.getCurrentShow().getPosterUrl(), imageLoader);
        }
        // title
        title.setText(channel.getCurrentShow().getTitle());

        // rating
        rating.setText("Rating: " + String.valueOf(channel.getCurrentShow().getRating()));

        // genre
        genre.setText(channel.getCurrentShow().getGenre());

        // release year
        year.setText(String.valueOf(channel.getCurrentShow().getYear()));

        return convertView;

    }
}