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
import com.nathantonani.popularmovies.fragment.MovieGridFragment;
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

    // newView creates a view for each cursor item, basically yielding
    // bindView wasteful after the first call. As a workaround, I've
    // overridden getView and simply return the view if it's not null,
    // rather than allowing the CursorAdapter to call bindView.
    // This does not seem like the correct behavior seeing as newView should
    // only be called for the visible image views + one row above and one row
    // below.

/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null) return super.getView(position,convertView,parent);
        return convertView;
    }
*/
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


        String url = cursor.getString(MovieGridFragment.COL_MOVIE_POSTER_PATH);// TODO continue
        //Load thumbnail into image view item
        try {
            Picasso.with(context).setIndicatorsEnabled(true);
            Picasso.with(context).load(url).noFade().into(viewHolder.imageView);
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