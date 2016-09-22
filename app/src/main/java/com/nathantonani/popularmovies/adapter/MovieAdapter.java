package com.nathantonani.popularmovies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nathantonani.popularmovies.data.Movie;
import com.nathantonani.popularmovies.R;
import com.squareup.picasso.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ntonani on 9/11/16.
 */
public class MovieAdapter extends BaseAdapter {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    private List<Movie> mMovies;

    public MovieAdapter(Context context){
        this.mContext = context;
        mMovies = new ArrayList<Movie> ();
        inflater = LayoutInflater.from(mContext);
    }

    /*
     * Adapter callbacks
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Inflate image view item
        if(convertView==null){
            convertView = inflater.inflate(R.layout.fragment_main_image_view,parent,false);
        }

        //Get movie data
        Movie movie = (Movie)getItem(position);
        String url = movie.getPosterPath();

        //Load thumbnail into image view item
        try {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).load(url).noFade().into((ImageView) convertView.findViewById(R.id.imageview_movies));
        }catch(Exception e){
            Log.e(LOG_TAG,"Error loading image:" +e.toString());
        }
        return convertView;
    }

    @Override
    public int getCount(){
        return this.mMovies.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < 0 || position > getCount()) return "";
        return this.mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
     * Data updates
     */

    public void add(Movie object) {
        mMovies.add(object);
        notifyDataSetChanged();
    }

    public void clear() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> data) {
        clear();
        if(data==null)return;
        for (Movie movie : data) {
            add(movie);
        }
    }
}
