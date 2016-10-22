package com.nathantonani.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nathantonani.popularmovies.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ntonani on 10/20/16.
 */
public class MovieCursorAdapter extends CursorAdapter{

    private final String LOG_TAG = MovieCursorAdapter.class.getSimpleName();

    public MovieCursorAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_main_image_view,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String url = null; // TODO continue

        //Load thumbnail into image view item
        try {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).load(url).noFade().into(viewHolder.imageView);
        }catch(Exception e){
            Log.e(LOG_TAG,"Error loading image:" +e.toString());
        }
    }

    static class ViewHolder {
        @BindView(R.id.imageview_movies)
        ImageView imageView;

        ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
