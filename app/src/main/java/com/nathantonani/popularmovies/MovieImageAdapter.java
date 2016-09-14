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
public class MovieImageAdapter extends BaseAdapter {

    private final String LOG_TAG = "MovieImageAdapter";
    private Context mContext;
    private LayoutInflater inflater;
    private List<String> mMovieUrls;

    public MovieImageAdapter(Context c){
        this.mContext = c;
        mMovieUrls = new ArrayList<String> ();
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount(){
        return this.mMovieUrls.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < 0 || position > getCount()) return "";
        return this.mMovieUrls.get(position);
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
        String url = (String)getItem(position);
        Picasso.with(mContext).load(url).into((ImageView)convertView.findViewById(R.id.imageview_movies));
        return convertView;
    }

    public void setMovieUrls(List<String> movieUrls){
        this.mMovieUrls=movieUrls;
        this.notifyDataSetChanged();
    }

    public void addMovieUrl(String url){
        this.mMovieUrls.add(url);
        this.notifyDataSetChanged();
    }
}
