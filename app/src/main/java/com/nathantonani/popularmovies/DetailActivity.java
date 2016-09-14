package com.nathantonani.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by ntonani on 9/12/16.
 */
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Log.v("DetailActivity", " OnCreate -- Starting fragment");
            getSupportFragmentManager().beginTransaction().add(R.id.container,new MovieDetailsFragment()).commit();
        }
    }
}
