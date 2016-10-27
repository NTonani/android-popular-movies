package com.nathantonani.popularmovies.adapter.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.data.MoviesContract;
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

/**
 * Created by ntonani on 10/26/16.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    private enum MovieType {
        POPULAR,RATING
    }

    public String moviePathBase = "http://api.themoviedb.org/3/movie/";
    public String fetchRating = "top_rated";
    public String fetchPopularity = "popular";

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public void syncMovies(MovieType type){
        String currentSort = fetchRating;
        if(type.equals(MovieType.POPULAR)) currentSort = fetchPopularity;
        String movieJsonString = null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{

            //Construct url
            final String MOVIE_BASE_URL = moviePathBase+currentSort+"/";
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

            if(inputStream == null) return;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            //Debugging purposes
            while((line=reader.readLine()) != null){
                buffer.append(line+"\n");
            }

            if(buffer.length()==0)return;

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

        if(movieJsonString==null)return;

        try {
            //Return decoded json
            List<Movie> results = decodeJsonToMovies(new JSONObject(movieJsonString));
            ContentValues[] cvs = getMovieContentValuesFromMovies(results);
            for(ContentValues cv : cvs){
                int cvId = (int) cv.get(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
                Uri movieItemUri = MoviesContract.MovieEntry.buildMovieUri(cvId);
                ContentResolver contentResolver = getContext().getContentResolver();
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
        }catch(JSONException jsonE){
            Log.e(LOG_TAG,"JSON failure: "+jsonE.toString());
        }

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

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        syncMovies(MovieType.POPULAR);
        syncMovies(MovieType.RATING);

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

        }
        return newAccount;
    }
}
