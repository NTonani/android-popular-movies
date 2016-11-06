package com.nathantonani.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;
import com.nathantonani.popularmovies.data.MoviesContract.GenreEntry;
import com.nathantonani.popularmovies.data.MoviesContract.MovieGenresEntry;

/**
 * Created by ntonani on 10/16/16.
 */
public class MoviesDatabase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME +" ("+
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER, "+
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_ADULT + " BIT NOT NULL, "+
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, "+
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "+
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                MovieEntry.COLUMN_FAVORITE + " BIT NOT NULL," +
                "UNIQUE ("+MovieEntry.COLUMN_MOVIE_ID+") ON CONFLICT REPLACE);";

        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME +" ("+
                GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, "+
                GenreEntry.COLUMN_NAME + " TEXT NOT NULL, "+
                "UNIQUE ("+GenreEntry.COLUMN_GENRE_ID+") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_GENRES_TABLE = "CREATE TABLE " + MovieGenresEntry.TABLE_NAME + " ("+
                MovieGenresEntry._ID + " INTEGER PRIMARY KEY, "+
                MovieGenresEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                MovieGenresEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, "+
                "FOREIGN KEY ("+MovieGenresEntry.COLUMN_MOVIE_ID+") REFERENCES " + MovieEntry.TABLE_NAME + " ("+MovieEntry._ID+"), "+
                "FOREIGN KEY ("+MovieGenresEntry.COLUMN_GENRE_ID+") REFERENCES " + GenreEntry.TABLE_NAME + " ("+GenreEntry._ID+"), "+
                "UNIQUE ("+MovieGenresEntry.COLUMN_MOVIE_ID + "," + MovieGenresEntry.COLUMN_GENRE_ID+") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_GENRE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_GENRES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Wipe and recreate
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieGenresEntry.TABLE_NAME);
        onCreate(db);
    }
}
