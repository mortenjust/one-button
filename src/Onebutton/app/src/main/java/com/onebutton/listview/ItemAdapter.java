package com.onebutton.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onebutton.R;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<String> {

    // declaring our ArrayList of items
    private ArrayList<String> objects;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public ItemAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_tvshows, null);
        }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        String i = objects.get(position);

        if (i != null) {

            int progress = Integer.parseInt(i.substring(i.length() - 3).trim());

            ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.PROGRESS_BAR);
            progressBar.setProgress(progress);
            TextView textView = (TextView)v.findViewById(R.id.list_item_tvshows_textview);
            textView.bringToFront();
            textView.setText(i);

        }

        // the view must be returned to our activity
        return v;

    }

}