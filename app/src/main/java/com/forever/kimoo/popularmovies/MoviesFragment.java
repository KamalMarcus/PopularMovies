package com.forever.kimoo.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

/**
 * A placeholder fragment containing a simple view.
 * https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=c0ba70d7ccb81811ba0b87f8bbbe5663
 */

public class MoviesFragment extends Fragment {

    private static final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private MoviesPostersAdapter postersAdapter;
    private ArrayList<MovieObject> results;
    public static DatabaseHandler db;
    private GridView grid;
    FetchMoviesTask moviesTask;
     public static boolean isTablet;



    public MoviesFragment() {
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        moviesTask = new FetchMoviesTask();

        switch (item.getItemId()) {

            case R.id.action_sort_rating:
                item.setChecked(true);

                moviesTask.execute("vote_average.desc");

                detailActivityResultAccess();

                return true;
            case R.id.action_sort_popularity:
                item.setChecked(true);

                moviesTask.execute("popularity.desc");

                detailActivityResultAccess();
                return true;
            case R.id.action_sort_favourite:
                item.setChecked(true);
                sortByFavourites();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void detailActivityResultAccess() {
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isTablet){
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("movies_details_list", results.get(position));

                    MovieDetailsActivityFragment fragment = new MovieDetailsActivityFragment();
                    fragment.setArguments(arguments);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_details_container, fragment, MovieDetailsActivityFragment.TAG)
                            .commit();
                }
                else {
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("movies_details_list", results.get(position));
                    startActivity(intent);
                }

            }
        });
    }

    private void sortByFavourites() {

        results = (ArrayList<MovieObject>) db.getAllMovies();

        if (results != null) {
            postersAdapter.clear();

            for (MovieObject movie : results) {
                postersAdapter.add(movie);

            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesTask = new FetchMoviesTask();
        moviesTask.execute("popularity.desc");

        db = new DatabaseHandler(getActivity());

        isTablet=checkScreenSize(getActivity().getApplicationContext());

        postersAdapter = new MoviesPostersAdapter(getActivity(), new ArrayList<MovieObject>());

        grid = (GridView) rootView.findViewById(R.id.gridview);
        grid.setAdapter(postersAdapter);
        detailActivityResultAccess();
        return rootView;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList> {

//        public boolean isOnline() { //Checks if there's Internet Connection or not
//            Runtime runtime = Runtime.getRuntime();
//            try {
//
//                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//                int exitValue = ipProcess.waitFor();
//                return (exitValue == 0);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return false;
//        }


        private ArrayList getMoviesDetailsFromJson(String movieJsonStr) throws JSONException {
            if(movieJsonStr != null) {

                final String RESULTS = "results";
                final String ID = "id";
                final String POSTERS = "poster_path";
                final String TITLE = "title";
                final String ORIGINAL_TITLE = "original_title";
                final String OVERVIEW = "overview";
                final String RATINGS = "vote_average";
                final String RELEASE_DATE = "release_date";
                final String IMAGE_THUMBNAIL = "backdrop_path";

                JSONObject moviesJson = new JSONObject(movieJsonStr);
                JSONArray moviesresultsArray = moviesJson.getJSONArray(RESULTS);

                results = new ArrayList<MovieObject>();

                for (int i = 0; i < moviesresultsArray.length(); i++) {
                    MovieObject movie = new MovieObject();

                    JSONObject movieDetails = moviesresultsArray.getJSONObject(i);

                    movie.setPosterURL("http://image.tmdb.org/t/p/w185" + movieDetails.getString(POSTERS));
                    movie.setID(movieDetails.getString(ID));
                    movie.setMovieName(movieDetails.getString(TITLE));
                    movie.setOriginalName(movieDetails.getString(ORIGINAL_TITLE));
                    movie.setOverview(movieDetails.getString(OVERVIEW));
                    movie.setRating(movieDetails.getString(RATINGS));
                    movie.setReleaseDate(movieDetails.getString(RELEASE_DATE));
                    movie.setMoviePostarThumbnail("http://image.tmdb.org/t/p/w342" + movieDetails.getString(IMAGE_THUMBNAIL));

                    results.add(movie);

                }
                for (MovieObject s : results) {
                    Log.v(LOG_TAG, "Poster entry: " + s.getPosterURL());
                }
            }
            return results;
        }

        @Override
        protected ArrayList doInBackground(String... params) {
//            if (isOnline()) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                // Will contain the raw JSON response as a string.
                String moviesJsonStr = null;


                String apiKey = "c0ba70d7ccb81811ba0b87f8bbbe5663";

                try {
                    //the whole link https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=c0ba70d7ccb81811ba0b87f8bbbe5663
                    final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                    final String SORTING_PARAM = "sort_by";
                    final String APIKEY_PARAM = "api_key";

                    Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                            .appendQueryParameter(SORTING_PARAM, params[0])
                            .appendQueryParameter(APIKEY_PARAM, apiKey)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "Built Uri :" + builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        moviesJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        moviesJsonStr = null;
                    }

                    moviesJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Movies Json String :" + moviesJsonStr);

                } catch (IOException e) {
                    Log.e("MoviesFragment", "Error ", e);
                    // If the code didn't successfully get the data, there's no point in attempting
                    // to parse it.
                    moviesJsonStr = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                try {
                    return getMoviesDetailsFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
//            }

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList result) {

                if (result != null) {
                    postersAdapter.clear();

                    for (Object movie : result) {
                        postersAdapter.add((MovieObject) movie);

                    }
                }
            else {
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection !",Toast.LENGTH_SHORT).show();
                }


        }


    }

    public boolean checkScreenSize(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
