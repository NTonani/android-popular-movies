package com.nathantonani.popularmovies.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.nathantonani.popularmovies.BuildConfig;
import com.nathantonani.popularmovies.data.Movie;
import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.activity.SettingsActivity;
import com.nathantonani.popularmovies.activity.DetailActivity;
import com.nathantonani.popularmovies.adapter.MovieAdapter;

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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * Created by ntonani on 9/11/16.
 */
public class MovieGridFragment extends Fragment{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private List<Movie> mMoviesRating;
    private List<Movie> mMoviesPopular;

    private MovieAdapter movieAdapter;
    private FetchMovieDataTask fetchPopularTask;
    private FetchMovieDataTask fetchRatingTask;
    private String sortOrder;

    @BindView(R.id.gridview_movies) GridView gridView;

    @BindString(R.string.movie_details_parcel) String movieDetails_parcel;

    @BindString(R.string.fetch_rating) String fetchRating;
    @BindString(R.string.fetch_popularity) String fetchPopularity;
    @BindString(R.string.fetch_popularity_var) String fetchPopularityVar;
    @BindString(R.string.fetch_rating_var) String fetchRatingVar;

    @BindString(R.string.pref_sort_key) String prefSortKey;
    @BindString(R.string.pref_sort_popularity) String prefSortPopularity;
    @BindString(R.string.pref_sort_rating) String prefSortRating;

    @BindString(R.string.movies_path_base) String moviePathBase;
    /*
     * Fragment lifecycle / callbacks
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate xml
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);
        ButterKnife.bind(this,rootView);

        this.setRetainInstance(true);
        setHasOptionsMenu(true);

        movieAdapter = new MovieAdapter(getContext());

        if(savedInstanceState==null)
            sortOrder = fetchPopularity;
        else{
            //Get bundled content
            mMoviesPopular = savedInstanceState.getParcelableArrayList(fetchPopularityVar);
            mMoviesRating = savedInstanceState.getParcelableArrayList(fetchRatingVar);
            sortOrder = savedInstanceState.getString(prefSortKey);
        }

        //Attach adapter to Grid View with click listener
        gridView.setAdapter(movieAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //Inflate action bar
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();


        if(id==R.id.action_settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }else if(id==R.id.action_refresh){
            //Set data to null and re-fetch
            fetchRatingTask=null;
            fetchPopularTask=null;
            mMoviesPopular=null;
            mMoviesRating=null;
            updateAdapterWithSortOrder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        //Save current sort lists and current sort
        outState.putParcelableArrayList(fetchRatingVar,(ArrayList<Movie>)mMoviesPopular);
        outState.putParcelableArrayList(fetchPopularityVar,(ArrayList<Movie>)mMoviesRating);
        outState.putString(prefSortKey,sortOrder);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateAdapterWithSortOrder();
    }

    /*
     * Adapter update
     */

    private void updateAdapterWithSortOrder() {
        //Determine current sort
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(prefSortKey, prefSortPopularity);
        sortOrder = sort;

        //If sorts are null - fetch
        if(mMoviesPopular==null || mMoviesRating==null) {
            fetchMovieData();
            return;
        }

        //Set adapter data
        if(sort.equals(prefSortPopularity))
            movieAdapter.setMovies(mMoviesPopular);
        else if(sort.equals(prefSortRating))
            movieAdapter.setMovies(mMoviesRating);
    }

    /*
     * Listeners
     */

    @OnItemClick(R.id.gridview_movies)
    public void onThumbnailClick(int position) {
        Movie movieObject = null;
        if(sortOrder.equals(prefSortPopularity))
            movieObject = mMoviesPopular.get(position);
        else if(sortOrder.equals(prefSortRating))
            movieObject=mMoviesRating.get(position);

        Intent intent = new Intent(getActivity(),DetailActivity.class);
        intent.putExtra(movieDetails_parcel,movieObject);
        startActivity(intent);
    }

    /*
     * Data fetching
     */

    public void fetchMovieData(){

        //Fetch popularity payload if null
        if(fetchPopularTask==null) {
            fetchPopularTask=new FetchMovieDataTask();
            fetchPopularTask.execute(fetchPopularity);
        }

        //Fetch rating payload if null
        if(fetchRatingTask==null) {
            fetchRatingTask=new FetchMovieDataTask();
            fetchRatingTask.execute(fetchRating);
        }
    }

    public class FetchMovieDataTask extends AsyncTask<String,Void,List<Movie>>{

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

            //Set sort lists
            if(currentSort.equals(fetchPopularity))
                mMoviesPopular=results;
            else if(currentSort.equals(fetchRating))
                mMoviesRating=results;

            updateAdapterWithSortOrder();
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
}
