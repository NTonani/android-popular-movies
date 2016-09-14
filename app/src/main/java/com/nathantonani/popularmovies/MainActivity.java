package com.nathantonani.popularmovies;

import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("MainActivity","onCreate - empty instance:"+(savedInstanceState==null));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("MainActivity", " OnCreate -- Starting fragment");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new MovieGridFragment())
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        Log.v("MainActivity","onSaveInstanceState -- "+(saveInstanceState==null));
        super.onSaveInstanceState(saveInstanceState);
    }
}
