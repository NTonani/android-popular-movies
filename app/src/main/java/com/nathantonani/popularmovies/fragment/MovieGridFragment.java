package com.nathantonani.popularmovies.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.activity.DetailActivity;
import com.nathantonani.popularmovies.activity.SettingsActivity;
import com.nathantonani.popularmovies.adapter.MovieAdapter;
import com.nathantonani.popularmovies.model.Movie;
import com.nathantonani.popularmovies.sync.FetchMovieDataTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


/**
 * Created by ntonani on 9/11/16.
 */
public class MovieGridFragment extends Fragment{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    public List<Movie> mMoviesRating;
    public List<Movie> mMoviesPopular;

    private MovieAdapter movieAdapter;
    private FetchMovieDataTask fetchPopularTask;
    private FetchMovieDataTask fetchRatingTask;
    private String sortOrder;

    @BindView(R.id.gridview_movies) GridView gridView;

    @BindString(R.string.movie_details_parcel) String movieDetails_parcel;

    @BindString(R.string.fetch_rating) public String fetchRating;
    @BindString(R.string.fetch_popularity) public String fetchPopularity;
    @BindString(R.string.fetch_popularity_var) String fetchPopularityVar;
    @BindString(R.string.fetch_rating_var) String fetchRatingVar;

    @BindString(R.string.pref_sort_key) String prefSortKey;
    @BindString(R.string.pref_sort_popularity) String prefSortPopularity;
    @BindString(R.string.pref_sort_rating) String prefSortRating;

    @BindString(R.string.movies_path_base) public String moviePathBase;
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

    public void updateAdapterWithSortOrder() {
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
            fetchPopularTask=new FetchMovieDataTask(this.getContext(),this,movieAdapter);
            fetchPopularTask.execute(fetchPopularity);
        }

        //Fetch rating payload if null
        if(fetchRatingTask==null) {
            fetchRatingTask=new FetchMovieDataTask(this.getContext(),this,movieAdapter);
            fetchRatingTask.execute(fetchRating);
        }
    }
}
