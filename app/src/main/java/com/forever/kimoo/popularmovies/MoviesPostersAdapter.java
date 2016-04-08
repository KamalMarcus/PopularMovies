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
 * Created by KiMoo on 04/12/2015.
 */
public class MoviesPostersAdapter extends ArrayAdapter<MovieObject> {

    public MoviesPostersAdapter(Activity context, List<MovieObject> movieObjects) {
        super(context, 0, movieObjects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    MovieObject movieObject=getItem(position);
        View rootView= LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        ImageView iconView= (ImageView) rootView.findViewById(R.id.list_item_icon);
        Picasso.with(getContext()).load(movieObject.getPosterURL()).into(iconView);

        TextView movieTitle= (TextView) rootView.findViewById(R.id.movie_title);
        movieTitle.setText(movieObject.getMovieName());

        return rootView;
    }
}
