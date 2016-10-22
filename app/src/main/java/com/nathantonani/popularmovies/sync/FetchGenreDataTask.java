package com.nathantonani.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ntonani on 10/21/16.
 */
public class FetchGenreDataTask extends AsyncTask<Void,Void,Void> {
    private final String LOG_TAG = FetchGenreDataTask.class.getSimpleName();

    private final Context mContext;
    //private FetchMovieDataTask mMovieDataTask;

    public FetchGenreDataTask(Context context){//, FetchMovieDataTask movieTask){
        this.mContext = context;
        //mMovieDataTask = movieTask;
    }

    protected Void doInBackground(Void... args) {

        String genreJsonString = null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            //Construct url
            final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/genre/movie/list/";
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
            genreJsonString = buffer.toString();

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

        if(genreJsonString==null)return null;

        try {
            ContentValues[] cvs = getGenreContentValuesFromJson(new JSONObject(genreJsonString));
            if(cvs!=null) {
                int updates = mContext.getContentResolver().bulkInsert(MoviesContract.GenreEntry.CONTENT_URI, cvs);
            }
        }catch(JSONException jsonE){
            Log.e(LOG_TAG,"JSON failure: "+jsonE.toString());
        }

        //Fail state
        return null;
    }

    private ContentValues[] getGenreContentValuesFromJson(JSONObject json){
        try{
            JSONArray genreArray = json.getJSONArray("genres");
            ContentValues[] cvs = new ContentValues[genreArray.length()];
            for(int i = 0; i<genreArray.length();i++){
                ContentValues cv = new ContentValues();
                cv.put(MoviesContract.GenreEntry.COLUMN_GENRE_ID,genreArray.getJSONObject(i).getInt("id"));
                cv.put(MoviesContract.GenreEntry.COLUMN_NAME,genreArray.getJSONObject(i).getString("name"));
                cvs[i] = cv;
            }
            return cvs;
        }catch(JSONException e){
            return null;
        }
    }
}
