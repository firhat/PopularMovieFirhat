package com.firhat.popularmoviefirhat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firhat.popularmoviefirhat.data.FavoriteContract;
import com.firhat.popularmoviefirhat.data.FavoriteDBHelper;
import com.firhat.popularmoviefirhat.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailView extends AppCompatActivity implements TrailersAdapter.ListItemClickListener,
        ReviewsAdapter.ListItemClickListener {

    private ProgressBar mLoadingIndicator;
    private List<TrailersModel> trailersModelList;
    private List<ReviewsModel> reviewsModelList;
    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    RecyclerView recyclerView;
    RecyclerView recyclerViewReviews;
    Button btnFavorite;
    private String id, title;
    private Boolean isFavorite;

    private SQLiteDatabase mDb;

    private final static String LOG_TAG = MovieDetailView.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        trailersModelList = new ArrayList<>();
        reviewsModelList  = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rc_trailer);
        recyclerViewReviews = (RecyclerView) findViewById(R.id.rc_review);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());

        trailersAdapter = new TrailersAdapter(this, trailersModelList, this);
        recyclerView.setAdapter(trailersAdapter);

        reviewsAdapter = new ReviewsAdapter(this, reviewsModelList, this);
        recyclerViewReviews.setAdapter(reviewsAdapter);

        TextView txtTitle       = (TextView) findViewById(R.id.txt_title);
        TextView txtYear        = (TextView) findViewById(R.id.txt_year);
        TextView txtRating      = (TextView) findViewById(R.id.txt_rating);
        TextView txtOverview    = (TextView) findViewById(R.id.txt_overview);
        ImageView img           = (ImageView) findViewById(R.id.img_thumb);
        btnFavorite      = (Button) findViewById(R.id.button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id           = extras.getString("id");
            title        = extras.getString("title");
            String releaseDate  = extras.getString("release_date");
            String Rating       = extras.getString("vote_average")+"/10";
            String imgUrl       = extras.getString("poster_path");
            String overview     = extras.getString("overview");

            txtTitle.setText(title);
            txtYear.setText(releaseDate != null ? releaseDate.substring(0, 4) : null);
            txtRating.setText(Rating);
            txtOverview.setText(overview);

            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185/"+imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(img);

            if(isNetworkAvailable()){
                URL url = NetworkUtils.buildUrl(id, "videos");
                new GetTrailersDataTask().execute(url);
            }else{
                Toast.makeText(MovieDetailView.this, "You are not connected to internet", Toast.LENGTH_LONG);
            }
        }
        FavoriteDBHelper dbHelper = new FavoriteDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        if(CheckIsDataAlreadyInDBorNot(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_ID, id)){
            btnFavorite.setText("Remove From Favorite");
            isFavorite = true;
        }else{
            btnFavorite.setText("Mark As Favorite");
            isFavorite = false;
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        TrailersModel trailers = trailersModelList.get(clickedItemIndex);
        String url = "https://www.youtube.com/watch?v="+trailers.getKey();
        openWebPage(url);

    }

    @Override
    public void onReviewListItemClick(int clickedItemIndex) {

    }

    public void addToFavorite(View view){
        if(!isFavorite){
            addNewFavorite(id,title);
            btnFavorite.setText("Remove From Favorite");
        }else{
            deleteFavorite(id);
            btnFavorite.setText("Mark As Favorite");
        }

    }

    private long addNewFavorite(String id, String title){
        ContentValues cv = new ContentValues();
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_ID, id);
        cv.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, title);
        return mDb.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, cv);
    }

    private String deleteFavorite(String id){
        return String.valueOf(mDb.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_ID + "=" + id, null) > 0);
    }

    private boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield,
                                                String fieldValue) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = mDb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void openWebPage(String url){
        Uri webpapage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpapage);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
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

    private class GetTrailersDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String dataResult = null;
            try {
                dataResult = NetworkUtils.getResponseFromHttpUrl(url);
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
                            JSONObject trailersObj = jArr.getJSONObject(i);
                            TrailersModel model = new TrailersModel();
                            model.setId(trailersObj.getString("id"));
                            model.setKey(trailersObj.getString("key"));
                            model.setName(trailersObj.getString("name"));
                            model.setType(trailersObj.getString("type"));

                            trailersModelList.add(model);
                        } catch (JSONException e) {
                            //Log.e("JSON ERROR", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                    trailersAdapter.notifyDataSetChanged();
                    URL url = NetworkUtils.buildUrl(id, "reviews");
                    new GetReviewsDataTask().execute(url);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class GetReviewsDataTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String dataResult = null;
            try {
                dataResult = NetworkUtils.getResponseFromHttpUrl(url);
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
                            JSONObject reviewsObj = jArr.getJSONObject(i);
                            ReviewsModel model = new ReviewsModel();
                            model.setId(reviewsObj.getString("id"));
                            model.setAuthor(reviewsObj.getString("author"));
                            model.setContent(reviewsObj.getString("content"));
                            model.setUrl(reviewsObj.getString("url"));

                            reviewsModelList.add(model);
                        } catch (JSONException e) {
                            //Log.e("JSON ERROR", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                    reviewsAdapter.notifyDataSetChanged();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
