package com.nathantonani.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ntonani on 10/16/16.
 */
public class MoviesContract {

    //Base unique name
    public static final String CONTENT_AUTHORITY = "com.nathantonani.popularmovies";

    //Base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_GENRE = "genre";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_MOVIE_GENRES = "movieGenres";

    public static final class GenreEntry implements BaseColumns{
        public static final Uri  CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        //Table name
        public static final String TABLE_NAME = "genre";

        /*
         * Cols
         */

        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_NAME = "name";

        /*
         * URI builders
         */

        public static Uri buildGenreUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static int getGenreIdFromUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final Uri CONTENT_URI_FAVORITES = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table name
        public static final String TABLE_NAME = "movie";

        /*
         * Cols
         */

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_FAVORITE = "favorite";

        /*
         * URI builders
         */

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMovieUriForFavorites(long id){
            return ContentUris.withAppendedId(CONTENT_URI_FAVORITES,id);
        }
        /*
         * URI parsers
         */

        public static int getMovieIdFromUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        public static int getMovieIdFromFavoritesUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class MovieGenresEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_GENRES).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENRES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_GENRES;

        //Table name
        public static final String TABLE_NAME = "movieGenres";

        /*
         * Cols
         */

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_GENRE_ID = "genre_id";

        /*
         * URI builders
         */

        public static Uri buildMovieGenreUri(long movieGenreId){
            return CONTENT_URI.buildUpon().appendPath(movieGenreId+"").build();
        }

        public static Uri buildMoviesOfGenreUri(long genreId){
            return CONTENT_URI.buildUpon().appendPath(PATH_GENRE).appendPath(genreId+"").build();
        }

        public static Uri buildGenresOfMovieUri(long movieId){
            return CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).appendPath(movieId+"").build();
        }

        /*
         * URI parsers
         */

        public static int getIdFromUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }
}
