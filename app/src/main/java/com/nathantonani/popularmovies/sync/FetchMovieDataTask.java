package com.nathantonani.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.adapter.MovieCursorAdapter;
import com.nathantonani.popularmovies.data.MoviesContract;
import com.nathantonani.popularmovies.fragment.MovieGridFragment;
import com.nathantonani.popularmovies.model.Movie;

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


/*
 * DEPRECATED FOR SYNC ADAPTER
 */
public class FetchMovieDataTask extends AsyncTask<String,Void,Void> {

    private final Context mContext;
    private MovieCursorAdapter mMovieAdapter;
    private MovieGridFragment mMovieGridFragment;

    public FetchMovieDataTask(Context context, MovieGridFragment fragment, MovieCursorAdapter adapter){
        this.mContext = context;
        this.mMovieAdapter = adapter;
        this.mMovieGridFragment = fragment;
    }

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

    private String currentSort;

    @Override
    protected Void doInBackground(String... strings) {

        String movieJsonString = null;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    try{
        //Passed in sort
        currentSort = strings[0];

        //Construct url
        final String MOVIE_BASE_URL = mMovieGridFragment.moviePathBase+currentSort+"/";
        final String QUERY_API_KEY="api_key";

        //Add API key
        Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        URL url = new URL(uri.toString());

        //Connect
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        //Read input
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();

        if(inputStream == null) return null;

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        //Debugging purposes
        while((line=reader.readLine()) != null){
            buffer.append(line+"\n");
        }

        if(buffer.length()==0)return null;

        //Final json
        movieJsonString = buffer.toString();

    }catch(Exception e){
        Log.e(LOG_TAG,"Error fetching data: "+e.toString());
    }finally {
        if(urlConnection!=null)
            urlConnection.disconnect();
        try {
            if(reader!=null)reader.close();
        }catch(IOException ioE){
            Log.e(LOG_TAG,"Could not close reader: "+ioE.toString());
        }
    }

    if(movieJsonString==null)return null;

    try {
        //Return decoded json
        List<Movie> results = decodeJsonToMovies(new JSONObject(movieJsonString));
        ContentValues[] cvs = getMovieContentValuesFromMovies(results);
        for(ContentValues cv : cvs){
            int cvId = (int) cv.get(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
            Uri movieItemUri = MoviesContract.MovieEntry.buildMovieUri(cvId);
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor movieCursor = contentResolver.query(movieItemUri,null,null,null,null);
            if(movieCursor==null)continue;

            // TODO move to bulk update / insert for resource efficiency - prevent
            // adapter from being updated multiple times?

            if(movieCursor.moveToFirst())
                contentResolver.update(movieItemUri, cv, null, null);
            else
                contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI,cv);

            movieCursor.close();

            // TODO
            // Need to add MovieGenres support
        }

        mMovieGridFragment.updateAdapterWithSortOrder();
    }catch(JSONException jsonE){
        Log.e(LOG_TAG,"JSON failure: "+jsonE.toString());
    }

    //Fail state
    return null;
}
    protected ContentValues[] getMovieContentValuesFromMovies(List<Movie> movies){
        ContentValues[] cvs = new ContentValues[movies.size()];
        for(int i = 0;i<movies.size();i++) cvs[i] = movies.get(i).getContentValues();
        return cvs;
    }
    protected List<Movie> decodeJsonToMovies(JSONObject retJson) throws JSONException{

        //Decode JSON
        JSONArray movieObjects = retJson.getJSONArray("results");

        if(movieObjects==null)return null;
        List<Movie> fetchedMovies = new ArrayList<Movie>();

        for(int i =0;i<movieObjects.length();i++){
            fetchedMovies.add(new Movie(movieObjects.getJSONObject(i)));
        }

        return fetchedMovies;
    }
}