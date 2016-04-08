package com.forever.kimoo.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by KiMoo on 24/01/2016.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    public TrailerAdapter(Activity context, List<Trailer> trailerObjects) {
        super(context, 0, trailerObjects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer=getItem(position);
        View rootView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        ImageView iconView= (ImageView) rootView.findViewById(R.id.trailer_image);
        Picasso.with(getContext()).load("http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg").into(iconView);

        TextView trailerName= (TextView) rootView.findViewById(R.id.trailer_name);
        trailerName.setText(trailer.getName());

        return rootView;
    }
}

