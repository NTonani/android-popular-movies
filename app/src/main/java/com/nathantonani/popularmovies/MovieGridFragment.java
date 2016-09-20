package com.nathantonani.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * TODO:
 * 0. Remove Settings and replace with Sort By
 * 1. Cache both sort lists - add list to adapter if not current
 * 2. Wait to load fragment page until picture fetched
 * 3. Look into how Picasso caches photos? Thumbnails in fragment should be cached outside? Fetch/store image thumbnail in movei object?
 */

/**
 * Created by ntonani on 9/11/16.
 */
public class MovieGridFragment extends Fragment{

    private final String LOG_TAG = "MovieGridFragment";

    private List<Movie> mMoviesRating;
    private List<Movie> mMoviesPopular;

    private List<Movie> mRatingMovies;
    private List<Movie> mPopularityMovies;

    private MovieAdapter movieAdapter;
    private FetchMovieDataTask fetchPopular;
    private FetchMovieDataTask fetchRating;
    private FetchMovieDataTask fetchMovieDataTask;
    private String sortOrder;

    /*
     * Lifecycle
     */

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.v("MovieGridFragment","onCreate - empty instance:"+(savedInstanceState==null));
    }

    @Override
    public void onPause(){
        Log.v(LOG_TAG,"onPause");
        super.onPause();
    }

    @Override
    public void onStop(){
        Log.v(LOG_TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onResume(){
        Log.v(LOG_TAG,"onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.v(LOG_TAG,"onSaveInstanceState");

        outState.putParcelableArrayList(getString(R.string.fetch_rating_var),(ArrayList<Movie>)mMoviesPopular);
        outState.putParcelableArrayList(getString(R.string.fetch_popularity_var),(ArrayList<Movie>)mMoviesRating);
        outState.putString("sortOrder",sortOrder);
        Log.v(LOG_TAG,"outState contains movies DNE == "+(outState.getParcelableArrayList("movies")==null));

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        Log.v(LOG_TAG,"onStart");
        super.onStart();
        updateAdapterWithSortOrder();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.action_settings){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.v("MovieGridFragment","onCreateView -- Starting");

        this.setRetainInstance(true);
        if(savedInstanceState==null) {
            Log.v(LOG_TAG,"onCreateView - NULL bundle");
            sortOrder = getString(R.string.fetch_popularity);
        }else{
            Log.v(LOG_TAG,"onCreateView - NOT NULL bundle");
            mMoviesPopular = savedInstanceState.getParcelableArrayList(getString(R.string.fetch_popularity_var));
            mMoviesRating = savedInstanceState.getParcelableArrayList(getString(R.string.fetch_rating_var));
            sortOrder = savedInstanceState.getString("sortOrder");
        }

        setHasOptionsMenu(true);

        movieAdapter = new MovieAdapter(getContext());

        //Inflate xml
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        //Attach adapter to Grid View
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movieObject = null;
                if(sortOrder.equals(getString(R.string.fetch_popularity)))
                    movieObject = mMoviesPopular.get(position);
                else if(sortOrder.equals(getString(R.string.fetch_rating)))
                    movieObject=mMoviesRating.get(position);

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("movie",movieObject);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void fetchMovieData(){

        if(fetchPopular==null) {
            fetchPopular=new FetchMovieDataTask();
            fetchPopular.execute(getString(R.string.fetch_popularity));
        }

        if(fetchRating==null) {
            fetchRating=new FetchMovieDataTask();
            fetchRating.execute(getString(R.string.fetch_rating));
        }
    }

    private void updateAdapterWithSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popularity));

        if(mMoviesPopular==null || mMoviesRating==null) {
            fetchMovieData();
            return;
        }

        if(sort.equals(getString(R.string.pref_sort_popularity)))
            movieAdapter.setMovies(mMoviesPopular);
        else if(sort.equals(getString(R.string.pref_sort_rating)))
            movieAdapter.setMovies(mMoviesRating);
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
                currentSort = strings[0];
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"+currentSort+"/";
                final String QUERY_API_KEY="api_key";

                Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_API_KEY,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                Log.v("URL",uri.toString());
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

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

                movieJsonString = buffer.toString();
                //TODO: Remove logging
                Log.v(LOG_TAG,movieJsonString);

            }catch(Exception e){
                Log.e(LOG_TAG,"Error fetching data: "+e.toString());
            }finally {
                urlConnection.disconnect();
                try {
                    if(reader!=null)reader.close();
                }catch(IOException ioE){
                    Log.e(LOG_TAG,"Could not close reader: "+ioE.toString());
                }
            }

            if(movieJsonString==null)return null;

            try {
                return decodeJsonToMovies(new JSONObject(movieJsonString));
            }catch(JSONException jsonE){
                Log.e(LOG_TAG,"JSON failue: "+jsonE.toString());
            }

            //Shouldn't get here
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> results){
            if(results==null)return;
            if(currentSort.equals(getString(R.string.fetch_popularity)))
                mMoviesPopular=results;
            else if(currentSort.equals(getString(R.string.fetch_rating)))
                mMoviesRating=results;
            updateAdapterWithSortOrder();
        }

        protected List<Movie> decodeJsonToMovies(JSONObject retJson) throws JSONException{
            //TODO: decode JSON
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
