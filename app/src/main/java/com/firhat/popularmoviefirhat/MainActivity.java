package com.firhat.popularmoviefirhat;

import android.content.Intent;
import android.os.AsyncTask;
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


import com.firhat.popularmoviefirhat.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    final static int numberOfColumn = 2;
    private List<MovieModel> movieModelList;
    private ProgressBar mLoadingIndicator;
    MovieAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        movieModelList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumn));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MovieAdapter(this, movieModelList, this);
        recyclerView.setAdapter(adapter);

        URL sortBy = NetworkUtils.buildUrl("popular");
        new GetMovieDataTask().execute(sortBy);

    }

    public class GetMovieDataTask extends AsyncTask<URL, Void, String> {

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
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Log.e("DATA", dataResult);

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
                            Log.e("JSON ERROR", "JSON Parsing error: " + e.getMessage());
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        MovieModel movie = movieModelList.get(clickedItemIndex);
        Intent i = new Intent(MainActivity.this, MovieDetailView.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", movie.getTitle());
        bundle.putString("overview", movie.getOverview());
        bundle.putString("release_date", movie.getRelease_date());
        bundle.putString("vote_average", String.valueOf(movie.getVote_average()));
        bundle.putString("poster_path", movie.getPoster_path());
        i.putExtras(bundle);
        startActivity(i);

        MainActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
