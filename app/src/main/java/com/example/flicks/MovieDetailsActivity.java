package com.example.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieDetailsActivity extends AppCompatActivity {

    //Movie to display
    Movie movie;

    //Views to display
    TextView tvTitle;
    TextView tvOverview;
    TextView tvReleaseDate;
    RatingBar rbVoteAverage;
    ImageView ivBackdropImage;
    String videoId;
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";
    public final static String VIDEO_ID = "video_id";
    public final static int EDIT_REQUEST_CODE = 20;



    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Initialize http client
        client = new AsyncHttpClient();

        String imageUrl = getIntent().getStringExtra("img_url");

        //Resolve views
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        tvReleaseDate = (TextView) findViewById(R.id.tvReleaseDate);
        ivBackdropImage = (ImageView) findViewById(R.id.ivBackdropImage);
        ivBackdropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                i.putExtra(VIDEO_ID, videoId);
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });


        //Unwrap the movie
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for: %s", movie.getTitle()));

        //Display title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(String.format("Release Date: %s", movie.getReleaseDate()));

        Float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage/2.0f : voteAverage);


        //load image with placeholder and rounded corners
        Glide.with(this)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .error(R.drawable.flicks_backdrop_placeholder)
                .into(ivBackdropImage);

        getVideoUrl(movie.getId());

    }




    //Get API configuration
    private void getVideoUrl(Integer id){
        //Create URL
        String url = API_BASE_URL + "/movie/" + id + "/videos";

        //Set Request Parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

//    Execute request
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject firstResult  = (JSONObject) response.getJSONArray("results").get(0);
                    videoId = firstResult.getString("key");
                } catch (JSONException e) {
                    Log.e("MovieDetails", "Failed parsing movie for id", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("MovieDetails", "Failed making request", throwable);
            }
        });
    }


}
