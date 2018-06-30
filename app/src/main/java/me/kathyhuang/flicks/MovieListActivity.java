package me.kathyhuang.flicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import me.kathyhuang.flicks.models.Config;
import me.kathyhuang.flicks.models.Movie;


public class MovieListActivity extends AppCompatActivity {

    //api constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";

    //tag for logging from this activity
    public final String TAG = "MovieListActivity";

    //instance fields
    AsyncHttpClient client;

    //list of currently playing movies
    ArrayList<Movie> movies;

    //recycler view
    @BindView(R.id.rvMovies) RecyclerView rvMovies;

    //adapter wired to recycler view
    MovieAdapter adapter;

    //image config
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        client = new AsyncHttpClient();
        movies = new ArrayList<>();

        //intialize the adapter
        adapter = new MovieAdapter(movies);

        //resolve recycler view and connect layout manager and adapter
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        getConfiguration();
    }

    private void getNowPlaying(){
        //create url
        String url = API_BASE_URL + "/movie/now_playing";

        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        //execute a get request expecting json response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load results into movie list
                try {
                    JSONArray results = response.getJSONArray("results");

                    //iterate through results and create new movie objects
                    for(int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);

                        adapter.notifyItemInserted(movies.size() - 1);
                    }

                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (Exception e) {
                    logError("Failed to parse now playing movies",e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    //get configuration from api
    private void getConfiguration(){
        //create url
        String url = API_BASE_URL + "/configuration";

        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        //execute a get request expecting json response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));

                    //pass config to adapter
                    adapter.setConfig(config);
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

    }

    //handle errors, log and alert user
    private void logError(String message, Throwable error, boolean alertUser){

        Log.e(TAG, message, error);

        //alert user
        if(alertUser){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
