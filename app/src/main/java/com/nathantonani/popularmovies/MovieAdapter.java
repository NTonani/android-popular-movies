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
        //PicassoCache.getPicassoInstance(mContext).load(url).noFade().into((ImageView)convertView.findViewById(R.id.imageview_movies));
        Picasso.with(mContext).load(url).noFade().into((ImageView)convertView.findViewById(R.id.imageview_movies));
        return convertView;
    }

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
