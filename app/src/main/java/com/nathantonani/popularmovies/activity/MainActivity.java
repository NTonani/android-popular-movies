package com.nathantonani.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.nathantonani.popularmovies.R;
import com.nathantonani.popularmovies.adapter.sync.MoviesSyncAdapter;
import com.nathantonani.popularmovies.fragment.MovieDetailsFragment;
import com.nathantonani.popularmovies.fragment.MovieGridFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieGridFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieGridFragment movieGridFragment;
    private MovieDetailsFragment movieDetailsFragment;

    private boolean mTwoPane;

    @BindView(R.id.movieGridFragment_container)
    FrameLayout movieGridFragment_container;

    @Nullable
    @BindView(R.id.movieDetailFragment_container)
    FrameLayout movieDetailsFragment_container;

    /*
     * Activity lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mTwoPane = movieDetailsFragment_container != null ? true : false;

        FragmentManager fm = getSupportFragmentManager();
        movieGridFragment = (MovieGridFragment)fm.findFragmentByTag(MovieGridFragment.class.getSimpleName());
        movieDetailsFragment = (MovieDetailsFragment)fm.findFragmentByTag(MovieDetailsFragment.class.getSimpleName());

        //Check grid fragment existence
        if(movieGridFragment==null){
            movieGridFragment = new MovieGridFragment();
            fm.beginTransaction().add(movieGridFragment_container.getId(),movieGridFragment, MovieGridFragment.class.getSimpleName())
                    .commit();
        }else
            movieGridFragment.getRetainInstance();

        // Handle details fragment if tablet
        if(mTwoPane){
            if(movieDetailsFragment == null){
                movieDetailsFragment = new MovieDetailsFragment();
                fm.beginTransaction().add(movieDetailsFragment_container.getId(),movieDetailsFragment,MovieDetailsFragment.class.getSimpleName())
                .commit();
            }else
                movieDetailsFragment.getRetainInstance();
        }


        MoviesSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.MOVIE_URI,movieUri);

            movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(movieDetailsFragment_container.getId(),movieDetailsFragment,MovieDetailsFragment.class.getSimpleName())
                    .commit();
        }else{
            Intent intent = new Intent(this,DetailActivity.class);
            intent.setData(movieUri);
            startActivity(intent);
        }
    }
}
