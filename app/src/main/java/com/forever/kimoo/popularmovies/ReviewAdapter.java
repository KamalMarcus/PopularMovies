package com.forever.kimoo.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by KiMoo on 24/01/2016.
 */
public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Activity context, List<Review> reviewObjects) {
        super(context, 0, reviewObjects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review=getItem(position);
        View rootView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);

        TextView author= (TextView) rootView.findViewById(R.id.review_author);
        author.setText(review.getAuthor());

        TextView content= (TextView) rootView.findViewById(R.id.review_content);
        content.setText(review.getContent());

        return rootView;
    }
}

