package com.forever.kimoo.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {

    FetchTrailersTask trailerTask;
    FetchReviewsTask reviewTask;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ListView trailerListView;
    private ListView reviewListView;

    public static final String TAG = MovieDetailsActivityFragment.class.getSimpleName();

    public MovieDetailsActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        trailerTask = new FetchTrailersTask();
        reviewTask = new FetchReviewsTask();

        List<MovieObject> moviesInDB = MoviesFragment.db.getAllMovies();

        Bundle data;
        if (MoviesFragment.isTablet) {
            data = getArguments();
        } else {
            data = getActivity().getIntent().getExtras();
        }

        final MovieObject movie = data.getParcelable("movies_details_list");

        trailerTask.execute(movie.getID());
        reviewTask.execute(movie.getID());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_thumbnail);
        Picasso.with(getActivity().getApplicationContext()).load(movie.getMoviePostarThumbnail()).into(imageView);

        TextView originalTitle = (TextView) rootView.findViewById(R.id.original_title);
        originalTitle.setText(movie.getMovieName());

        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setText("\n" + movie.getOverview() + "\n");

        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        rating.setText("Rating :               " + movie.getRating() + " / 10");

        TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        releaseDate.setText("Release Date :   " + movie.getReleaseDate());


        final ImageView imageFavourite = (ImageView) rootView.findViewById(R.id.imageFavourite);

        //////////checking if Selected Movie Exists in DB
        for (MovieObject movieInDB : moviesInDB) {
            if (movieInDB.getID().equals(movie.getID())) {
                imageFavourite.setBackground(getResources().getDrawable(R.drawable.star_true));
            }
        }


        imageFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap starFalse = ((BitmapDrawable) getResources().getDrawable(R.drawable.star_false)).getBitmap();
                Bitmap selectedStar = ((BitmapDrawable) v.getBackground()).getBitmap();


                if (selectedStar == starFalse) {
                    imageFavourite.setBackground(getResources().getDrawable(R.drawable.star_true));
                    Toast.makeText(getActivity().getApplicationContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();
                    MoviesFragment.db.addMovie(movie);
                } else {
                    imageFavourite.setBackground(getResources().getDrawable(R.drawable.star_false));
                    Toast.makeText(getActivity().getApplicationContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                    MoviesFragment.db.deleteMovie(movie.getID());
                }
            }
        });

        trailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());

        trailerListView = (ListView) rootView.findViewById(R.id.trailer_listview);
        trailerListView.setAdapter(trailerAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = trailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(intent);
            }
        });

        reviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
        reviewListView = (ListView) rootView.findViewById(R.id.review_listview);
        reviewListView.setAdapter(reviewAdapter);

        return rootView;
    }


    public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<Review> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review.getString("id"), review.getString("author"), review.getString("content")));
            }

            return results;
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "c0ba70d7ccb81811ba0b87f8bbbe5663")
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if (reviews != null) {
                reviewAdapter.clear();

                for (Object review : reviews) {
                    reviewAdapter.add((Review) review);
                }
            }
        }

    }


    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private List<Trailer> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);

                if (trailer.getString("site").contentEquals("YouTube")) {

                    results.add(new Trailer(trailer.getString("id"), trailer.getString("key"), trailer.getString("name"), trailer.getString("site"), trailer.getString("type")));
                }
            }


            return results;
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "c0ba70d7ccb81811ba0b87f8bbbe5663")
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null) {
                trailerAdapter.clear();

                for (Object trailer : trailers) {
                    trailerAdapter.add((Trailer) trailer);
                }
            }
        }
    }

}
