package com.nathantonani.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ntonani on 9/11/16.
 */
public class MovieAdapter extends BaseAdapter {

    private final String LOG_TAG = "MovieImageAdapter";
    private Context mContext;
    private LayoutInflater inflater;
    private List<Movie> mMovies;

    public MovieAdapter(Context c){
        this.mContext = c;
        mMovies = new ArrayList<Movie> ();
        inflater = LayoutInflater.from(mContext);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO: Maybe handle null convertView?
        if(convertView==null){
            convertView = inflater.inflate(R.layout.fragment_main_image_view,parent,false);
        }
        Movie movie = (Movie)getItem(position);
        String url = movie.getPosterPath();
        Picasso.with(mContext).load(url).into((ImageView)convertView.findViewById(R.id.imageview_movies));
        return convertView;
    }

    public void setMovies(List<Movie> movies){
        this.mMovies=movies;
        this.notifyDataSetChanged();
    }

    public void addMovie(Movie movie){
        this.mMovies.add(movie);
        this.notifyDataSetChanged();
    }

    public List<Movie> getMovies(){
        return this.mMovies;
    }
}
