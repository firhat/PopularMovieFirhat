package com.firhat.popularmoviefirhat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.firhat.popularmoviefirhat.data.FavoriteContract;
import com.firhat.popularmoviefirhat.data.FavoriteDBHelper;
import com.firhat.popularmoviefirhat.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener,
        FavoriteAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    final static int numberOfColumn = 2;
    private List<MovieModel> movieModelList;
    private ProgressBar mLoadingIndicator;
    MovieAdapter adapter;
    FavoriteAdapter favoriteAdapter;
    RecyclerView recyclerView;
    Cursor cursor;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FavoriteDBHelper dbHelper = new FavoriteDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        movieModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MovieAdapter(this, movieModelList, this);
        recyclerView.setAdapter(adapter);

        URL sortBy = NetworkUtils.buildUrl("popular");
        if (isNetworkAvailable()){
            new GetMovieDataTask().execute(sortBy);
        }else{
            Toast.makeText(MainActivity.this, "You are not connected to internet", Toast.LENGTH_LONG);
        }

        favoriteAdapter = new FavoriteAdapter(this, this);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        //Cursor cursor = getAllFavorite();


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavData = null;

            @Override
            protected void onStartLoading(){
                if(mFavData != null){
                    deliverResult(mFavData);
                }else {
                    forceLoad();
                }

            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP);
                }catch (Exception e){
                    Log.e(TAG, "Failed to async data");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data){
                mFavData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = data;
        favoriteAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favoriteAdapter.swapCursor(null);

    }

    private class GetMovieDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL movieUrl = params[0];
            String dataResult = null;
            try {
                dataResult = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return dataResult;
        }

        @Override
        protected void onPostExecute(String dataResult) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (dataResult != null && !dataResult.equals("")) {
                try {
                    JSONObject jObj = new JSONObject(dataResult);
                    JSONArray jArr = jObj.getJSONArray("results");
                    for (int i = 0; i < jArr.length(); i++) {
                        try {
                            JSONObject movieObj = jArr.getJSONObject(i);
                            MovieModel model = new MovieModel();
                            model.setId(movieObj.getInt("id"));
                            model.setTitle(movieObj.getString("title"));
                            model.setPoster_path(movieObj.getString("poster_path"));
                            model.setVote_average(movieObj.getInt("vote_average"));
                            model.setRelease_date(movieObj.getString("release_date"));
                            model.setOverview(movieObj.getString("overview"));

                            movieModelList.add(model);
                        } catch (JSONException e) {
                            //Log.e("JSON ERROR", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.e("DATA", String.valueOf(adapter.getItemCount()));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_popular) {
            movieModelList.clear();
            adapter = new MovieAdapter(this, movieModelList, this);
            recyclerView.setAdapter(adapter);
            URL sortBy = NetworkUtils.buildUrl("popular");
            new GetMovieDataTask().execute(sortBy);
            return true;
        }else if(itemThatWasClickedId == R.id.action_rated){
            movieModelList.clear();
            adapter = new MovieAdapter(this, movieModelList, this);
            recyclerView.setAdapter(adapter);
            URL sortBy = NetworkUtils.buildUrl("top_rated");
            new GetMovieDataTask().execute(sortBy);
            return true;
        }else if(itemThatWasClickedId == R.id.action_favorite){
            recyclerView.setAdapter(favoriteAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        MovieModel movie = movieModelList.get(clickedItemIndex);
        Intent i = new Intent(MainActivity.this, MovieDetailView.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(movie.getId()));
        bundle.putString("title", movie.getTitle());
        bundle.putString("overview", movie.getOverview());
        bundle.putString("release_date", movie.getRelease_date());
        bundle.putString("vote_average", String.valueOf(movie.getVote_average()));
        bundle.putString("poster_path", movie.getPoster_path());
        i.putExtras(bundle);
        startActivity(i);

        MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onFavListItemClick(int clickedItemIndex) {
        Bundle bundle = new Bundle();
        Intent i = new Intent(MainActivity.this, MovieDetailView.class);
        bundle.putString("id", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ID))));
        bundle.putString("title", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE))));
        bundle.putString("overview", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW))));
        bundle.putString("release_date", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE))));
        bundle.putString("vote_average", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE_AVERAGE))));
        bundle.putString("poster_path", String.valueOf(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH))));
        i.putExtras(bundle);
        startActivity(i);

        MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    private Cursor getAllFavorite() {
        return mDb.query(
                FavoriteContract.FavoriteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoriteContract.FavoriteEntry.COLUMN_TIMESTAMP
        );
    }
}
