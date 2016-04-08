package com.forever.kimoo.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KiMoo on 17/12/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "moviesDatabase";
    private static final String TABLE_NAME = "movies";

    //Movies Table column names
    private static final String ID = "_id";
    private static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_NAME = "name";
    private static final String MOVIE_POSTER = "poster";
    private static final String MOVIE_IMAGE = "image";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_RATING = "rating";
    private static final String MOVIE_DATE = "movie_date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY," + MOVIE_ID + " TEXT," + MOVIE_NAME + " TEXT," + MOVIE_POSTER + " TEXT," + MOVIE_IMAGE + " TEXT," + MOVIE_OVERVIEW + " TEXT," + MOVIE_RATING + " TEXT," + MOVIE_DATE + " TEXT)";
        db.execSQL(CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        onCreate(db);
    }

    public void addMovie(MovieObject movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, movie.getID());
        values.put(MOVIE_NAME, movie.getMovieName());
        values.put(MOVIE_POSTER, movie.getPosterURL());
        values.put(MOVIE_IMAGE, movie.getMoviePostarThumbnail());
        values.put(MOVIE_OVERVIEW, movie.getOverview());
        values.put(MOVIE_RATING, movie.getRating());
        values.put(MOVIE_DATE, movie.getReleaseDate());

        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public MovieObject getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{MOVIE_ID, MOVIE_NAME, MOVIE_POSTER, MOVIE_IMAGE, MOVIE_OVERVIEW, MOVIE_RATING, MOVIE_DATE}, ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        MovieObject movie = new MovieObject(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        return movie;
    }

    public List<MovieObject> getAllMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<MovieObject> moviesList = new ArrayList<MovieObject>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                MovieObject movie = new MovieObject(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                moviesList.add(movie);
                System.out.println(movie.getMovieName());
            }
            while (cursor.moveToNext());
        }
        return moviesList;
    }

    public void deleteMovie(String movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, MOVIE_ID + "=?", new String[]{movieId});
        db.close();
    }


}
