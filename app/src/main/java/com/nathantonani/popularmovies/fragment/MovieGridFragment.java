package com.nathantonani.popularmovies.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.activity.SettingsActivity;
import com.nathantonani.popularmovies.adapter.MovieCursorAdapter;
import com.nathantonani.popularmovies.adapter.sync.MoviesSyncAdapter;
import com.nathantonani.popularmovies.data.MoviesContract.MovieEntry;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


/**
 * Created by ntonani on 9/11/16.
 */
public class MovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private static final int MOVIE_POPULARITY_LOADER = 0;
    private static final int MOVIE_RATING_LOADER = 1;
    private static final int MOVIE_FAVORITES_LOADER = 2;

    private static final String[] MOVIE_PROJECTION = {
            MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_POSTER_PATH
    };


    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_MOVIE_ID = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;

    public Cursor mMoviesRating;
    public Cursor mMoviesPopular;
    public Cursor mMoviesFavorite;

    private MovieCursorAdapter movieAdapter;

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
    @BindString(R.string.pref_sort_favorites) String prefSortFavorites;

    @BindString(R.string.movies_path_base) public String moviePathBase;

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }
    /*
     * Fragment lifecycle / callbacks
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Inflate xml
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);
        ButterKnife.bind(this,rootView);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        movieAdapter = new MovieCursorAdapter(getContext(),null, 0);
        sortOrder = fetchPopularity;

        //Attach adapter to Grid View with click listener
        gridView.setAdapter(movieAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_POPULARITY_LOADER,null,this);
        getLoaderManager().initLoader(MOVIE_RATING_LOADER,null,this);
        getLoaderManager().initLoader(MOVIE_FAVORITES_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
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
            fetchMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        //Set adapter data
        if(sort.equals(prefSortPopularity))
            movieAdapter.swapCursor(mMoviesPopular);
        else if(sort.equals(prefSortRating))
            movieAdapter.swapCursor(mMoviesRating);
        else if(sort.equals(prefSortFavorites))
            movieAdapter.swapCursor(mMoviesFavorite);
    }

    /*
     * Listeners
     */


    @OnItemClick(R.id.gridview_movies)
    public void onThumbnailClick(GridView gridView, View view, int position, long l) {
        Cursor cursor = (Cursor)gridView.getItemAtPosition(position);

        if(cursor == null) return;
        Uri movieUri = MovieEntry.buildMovieUri(cursor.getInt(COL_MOVIE_MOVIE_ID));
        ((Callback) getActivity()).onItemSelected(movieUri);
    }


    /*
     * Data fetching
     */

    public void fetchMovieData(){
        MoviesSyncAdapter.syncImmediately(getContext());
    }

    /*
     * Loader Callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case MOVIE_POPULARITY_LOADER:
                return new CursorLoader(getActivity(),MovieEntry.CONTENT_URI,MOVIE_PROJECTION,null,null,MovieEntry.COLUMN_POPULARITY + " DESC");
            case MOVIE_RATING_LOADER:
                return new CursorLoader(getActivity(),MovieEntry.CONTENT_URI,MOVIE_PROJECTION,null,null,MovieEntry.COLUMN_VOTE_AVERAGE + " DESC");
            case MOVIE_FAVORITES_LOADER:
                return new CursorLoader(getActivity(),MovieEntry.CONTENT_URI_FAVORITES,MOVIE_PROJECTION,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()){
            case MOVIE_POPULARITY_LOADER:
                mMoviesPopular = data;
                if(sortOrder.equals(prefSortPopularity)) movieAdapter.swapCursor(mMoviesPopular);
                break;
            case MOVIE_RATING_LOADER:
                mMoviesRating = data;
                if(sortOrder.equals(prefSortRating)) movieAdapter.swapCursor(mMoviesRating);
                break;
            case MOVIE_FAVORITES_LOADER:
                mMoviesFavorite = data;
                if(sortOrder.equals(prefSortFavorites)) movieAdapter.swapCursor(mMoviesFavorite);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()){
            case MOVIE_POPULARITY_LOADER:
                mMoviesPopular =null;
                if(sortOrder.equals(prefSortPopularity)) movieAdapter.swapCursor(mMoviesPopular);
                break;
            case MOVIE_RATING_LOADER:
                mMoviesRating = null;
                if(sortOrder.equals(prefSortRating)) movieAdapter.swapCursor(mMoviesRating);
                break;
            case MOVIE_FAVORITES_LOADER:
                mMoviesFavorite = null;
                if(sortOrder.equals(prefSortFavorites)) movieAdapter.swapCursor(mMoviesFavorite);
                break;
        }
    }
}
