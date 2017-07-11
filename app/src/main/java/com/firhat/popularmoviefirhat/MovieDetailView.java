package com.firhat.popularmoviefirhat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView txtTitle       = (TextView) findViewById(R.id.txt_title);
        TextView txtYear        = (TextView) findViewById(R.id.txt_year);
        TextView txtRating      = (TextView) findViewById(R.id.txt_rating);
        TextView txtOverview    = (TextView) findViewById(R.id.txt_overview);
        ImageView img           = (ImageView) findViewById(R.id.img_thumb);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtTitle.setText(extras.getString("title"));
            txtYear.setText(extras.getString("release_date"));
            txtRating.setText(extras.getString("vote_average"));
            txtOverview.setText(extras.getString("overview"));

            Picasso.with(this)
                    .load("http://image.tmdb.org/t/p/w185/"+extras.getString("poster_path"))
                    .into(img);
         }
    }

}
