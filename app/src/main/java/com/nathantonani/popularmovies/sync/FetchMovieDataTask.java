package com.nathantonani.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.adapter.MovieAdapter;
import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;
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

public class FetchMovieDataTask extends AsyncTask<String,Void,List<Movie>> {

    private final Context mContext;
    private MovieAdapter mMovieAdapter;
    private MovieGridFragment mMovieGridFragment;

    public FetchMovieDataTask(Context context, MovieGridFragment fragment, MovieAdapter adapter){
        this.mContext = context;
        this.mMovieAdapter = adapter;
        this.mMovieGridFragment = fragment;
    }

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

    private String currentSort;

    @Override
    protected List<Movie> doInBackground(String... strings) {

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
        return decodeJsonToMovies(new JSONObject(movieJsonString));
    }catch(JSONException jsonE){
        Log.e(LOG_TAG,"JSON failure: "+jsonE.toString());
    }

    //Fail state
    return null;
}

    @Override
    protected void onPostExecute(List<Movie> results){
        if(results==null)return;

        // TODO Query and store new Movie entries
        ContentValues[] cvs = getMovieContentValuesFromMovies(results);
        for(ContentValues cv : cvs){
            int cvId = (int) cv.get(MovieEntry.COLUMN_MOVIE_ID);
            Uri movieItemUri = MovieEntry.buildMovieUri(cvId);
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor movieCursor = contentResolver.query(movieItemUri,null,null,null,null);
            if(movieCursor==null)continue;
            if(movieCursor.moveToFirst()){
                // TODO UPDATE
                contentResolver.update(movieItemUri, cv, null, null);
            }else{
               // TODO INSERT
                contentResolver.insert(MovieEntry.CONTENT_URI,cv);
            }

            movieCursor.close();

            // TODO
            // Need to add MovieGenres support
        }

        //Set sort lists
        if(currentSort.equals(mMovieGridFragment.fetchPopularity))
            mMovieGridFragment.mMoviesPopular=results;
        else if(currentSort.equals(mMovieGridFragment.fetchRating))
            mMovieGridFragment.mMoviesRating=results;

        mMovieGridFragment.updateAdapterWithSortOrder();
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