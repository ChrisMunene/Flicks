package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //Declare constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";
    public final static String LOGGING_TAG = "MainActivity";

    AsyncHttpClient client;

    //Array of movies
    ArrayList<Movie>movies;
    //Recycler View
    RecyclerView rvMovies;
    //Adapter wired to recycler view
    MovieAdapter adapter;
    //Image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize http client
        client = new AsyncHttpClient();

        //Initialize movies
        movies = new ArrayList<>();

        //Initialize adapter
        adapter = new MovieAdapter(movies);

        //Resolve recycler view and connect layout manager and adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //Get config from API
        getConfiguration();

    }

    private void getNowPlaying(){
        //Create URL
        String url = API_BASE_URL + "/movie/now_playing";

        //Set Request Parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        //Query API for movie data
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
//                    Load movies into array
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        adapter.notifyItemInserted(movies.size() - 1);
                    }

                    Log.i(LOGGING_TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed parsing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to fetch movies", throwable, true);
            }
        });


    }

    //Get API configuration
    private void getConfiguration(){
        //Create URL
        String url = API_BASE_URL + "/configuration";

        //Set Request Parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

//    Execute request
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(LOGGING_TAG, String.format("Loaded config with baseUrl %s and posterSize %s", config.getImgBaseUrl(), config.getPosterSize()));
                    adapter.setConfig(config);
                    //Get now playing from API
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                logError("Failed to get configuration.", throwable, true);
            }
        });
    }

    //Error Handler
    private void logError(String message, Throwable error, Boolean alertUser){
        Log.e(LOGGING_TAG, message, error);
        //Alert User
        if(alertUser){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
