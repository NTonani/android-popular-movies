package com.nathantonani.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.nathantonani.popularmovies.data.MoviesContract.GenreEntry;
import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;
import com.nathantonani.popularmovies.data.MoviesContract.MovieGenresEntry;

/**
 * Created by ntonani on 10/18/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDatabase mDbHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_ITEM = 101;
    public static final int MOVIE_FAVORITES = 102;
    public static final int MOVIE_FAVORITES_ITEM = 103;

    public static final int GENRE = 200;
    public static final int GENRE_ITEM = 201;

    public static final int MOVIE_GENRES = 300;
    public static final int MOVIES_OF_GENRE = 301;
    public static final int GENRES_OF_MOVIE = 302;

    private static final SQLiteQueryBuilder sMoviesOfGenreQueryBuilder;
    private static final SQLiteQueryBuilder sGenresOfMovieQueryBuilder;
    private static final String[] sMoviesOfGenreProjection;
    private static final String[] sGenresOfMovieProjection;

    static {
        sMoviesOfGenreQueryBuilder = new SQLiteQueryBuilder();
        sGenresOfMovieQueryBuilder = new SQLiteQueryBuilder();

        sMoviesOfGenreQueryBuilder.setTables(
                MovieEntry.TABLE_NAME + " INNER JOIN " + MovieGenresEntry.TABLE_NAME +
                " ON " + MovieEntry.TABLE_NAME + "." + MovieEntry._ID +
                " = " + MovieGenresEntry.TABLE_NAME + "." + MovieGenresEntry.COLUMN_MOVIE_ID
        );

        sGenresOfMovieQueryBuilder.setTables(
                GenreEntry.TABLE_NAME + " INNER JOIN " + MovieGenresEntry.TABLE_NAME +
                " ON " + GenreEntry.TABLE_NAME + "." + GenreEntry._ID +
                " = " + MovieGenresEntry.TABLE_NAME + "." + MovieGenresEntry.COLUMN_GENRE_ID
        );

        sMoviesOfGenreProjection = new String[] {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_OVERVIEW,
                MovieEntry.COLUMN_ADULT,
                MovieEntry.COLUMN_RELEASE_DATE,
                MovieEntry.COLUMN_POPULARITY,
                MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieEntry.COLUMN_POSTER_PATH
        };

        sGenresOfMovieProjection = new String[] {
                GenreEntry._ID,
                GenreEntry.COLUMN_GENRE_ID,
                GenreEntry.COLUMN_NAME
        };

    }


    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIE); //DIR
        matcher.addURI(authority, MoviesContract.PATH_GENRE ,GENRE); //DIR
        matcher.addURI(authority, MoviesContract.PATH_MOVIE_GENRES, MOVIE_GENRES); //DIR
        matcher.addURI(authority,MoviesContract.PATH_FAVORITES,MOVIE_FAVORITES); //DIR

        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", MOVIE_ITEM); //ITEM
        matcher.addURI(authority, MoviesContract.PATH_GENRE + "/#", GENRE_ITEM); //ITEM
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", MOVIE_FAVORITES_ITEM); //ITEM

        matcher.addURI(authority, MoviesContract.PATH_MOVIE_GENRES + "/" + MoviesContract.PATH_GENRE + "/#", MOVIES_OF_GENRE); //DIR
        matcher.addURI(authority, MoviesContract.PATH_MOVIE_GENRES + "/" + MoviesContract.PATH_MOVIE + "/#", GENRES_OF_MOVIE); //DIR
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        Cursor tempCursor = null;
        switch(sUriMatcher.match(uri)){
            case MOVIE:
                cursor = mDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case GENRE:
                cursor = mDbHelper.getReadableDatabase().query(GenreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_FAVORITES:
                cursor = mDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,projection,
                        MovieEntry.COLUMN_FAVORITE + " = ?",new String[]{"1"},null,null,sortOrder);
                break;
            case MOVIE_GENRES:
                cursor = mDbHelper.getReadableDatabase().query(MovieGenresEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_ITEM:
                int movieId = MovieEntry.getMovieIdFromUri(uri);
                cursor = mDbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ?",new String[]{movieId+""},null,null,sortOrder);
                break;
            case GENRE_ITEM:
                int genreId = GenreEntry.getGenreIdFromUri(uri);
                cursor = mDbHelper.getReadableDatabase().query(GenreEntry.TABLE_NAME,projection,
                        GenreEntry.COLUMN_GENRE_ID + " = ?",new String[]{genreId+""},null,null,sortOrder);
                break;
            case MOVIES_OF_GENRE:
                int genreIdForMovies = MovieGenresEntry.getIdFromUri(uri);
                cursor = sMoviesOfGenreQueryBuilder.query(mDbHelper.getReadableDatabase(),sMoviesOfGenreProjection,
                        MovieGenresEntry.COLUMN_GENRE_ID + " = ?",new String[]{genreIdForMovies+""},null,null,sortOrder);
                break;
            case GENRES_OF_MOVIE:
                int movieIdForGenres = MovieGenresEntry.getIdFromUri(uri);
                cursor = sGenresOfMovieQueryBuilder.query(mDbHelper.getReadableDatabase(),sGenresOfMovieProjection,
                        MovieGenresEntry.COLUMN_MOVIE_ID + " = ?",new String[]{movieIdForGenres+""},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case MOVIE:
                return MovieEntry.CONTENT_DIR_TYPE;
            case GENRE:
                return GenreEntry.CONTENT_DIR_TYPE;
            case MOVIE_GENRES:
                return MovieGenresEntry.CONTENT_DIR_TYPE;
            case MOVIE_ITEM:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case GENRE_ITEM:
                return GenreEntry.CONTENT_ITEM_TYPE;
            case MOVIES_OF_GENRE:
                return MovieGenresEntry.CONTENT_ITEM_TYPE;
            case GENRES_OF_MOVIE:
                return MovieGenresEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri retUri = null;

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                long movieId = mDbHelper.getWritableDatabase().insert(MovieEntry.TABLE_NAME,null,values);
                if(movieId > 0) retUri = MovieEntry.buildMovieUri(movieId);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case GENRE:
                long genreId = mDbHelper.getWritableDatabase().insert(GenreEntry.TABLE_NAME,null,values);
                if(genreId > 0) retUri = GenreEntry.buildGenreUri(genreId);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case MOVIE_GENRES:
                long movieGenreId = mDbHelper.getWritableDatabase().insert(MovieGenresEntry.TABLE_NAME,null,values);
                if(movieGenreId > 0) retUri = MovieGenresEntry.buildMovieGenreUri(movieGenreId);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri + " matcher id: " + sUriMatcher.match(uri));
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        int insertCount = 0;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String tableName = null;
        switch(sUriMatcher.match(uri)){
            case MOVIE:
                tableName = MovieEntry.TABLE_NAME;
                break;
            case GENRE:
                tableName = GenreEntry.TABLE_NAME;
                break;
            case MOVIE_GENRES:
                tableName = MovieGenresEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri,values);
        }

        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(tableName,null,value);
                if(id != -1) insertCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return insertCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletedRows = 0;
        if(selection == null) selection = "1";
        switch(sUriMatcher.match(uri)){
            case MOVIE:
                deletedRows = mDbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case GENRE:
                deletedRows = mDbHelper.getWritableDatabase().delete(GenreEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case MOVIE_GENRES:
                deletedRows = mDbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if(deletedRows != 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updatedRows = 0;

        switch(sUriMatcher.match(uri)){
            case MOVIE:
                updatedRows = mDbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case GENRE:
                updatedRows = mDbHelper.getWritableDatabase().update(GenreEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case MOVIE_GENRES:
                updatedRows = mDbHelper.getWritableDatabase().update(MovieGenresEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case MOVIE_ITEM: {
                int movieId = MovieEntry.getMovieIdFromUri(uri);
                updatedRows = mDbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME, values, MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{movieId + ""});
                break;
            }case MOVIE_FAVORITES_ITEM: {
                int movieId = MovieEntry.getMovieIdFromFavoritesUri(uri);
                updatedRows = mDbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME, values, MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{movieId + ""});
                break;
            }default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if(updatedRows != 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return updatedRows;
    }
}
