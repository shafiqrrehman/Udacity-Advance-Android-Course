package com.shafiq.sunshine.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shafiq.sunshine.R;
import com.shafiq.sunshine.adapters.ForecastAdapter;
import com.shafiq.sunshine.click_listener_interface.ForecastAdapterOnClickHandler;
import com.shafiq.sunshine.data.SunshinePreferences;
import com.shafiq.sunshine.utilities.NetworkUtils;
import com.shafiq.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mRecyclerView = findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        loadWeatherData();
    }

    private void loadWeatherData() {

        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Class destinationClass = DetailActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weartherRequestUrl = NetworkUtils.buildUrl(location);

            try{
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weartherRequestUrl);

                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (weatherData != null) {

                showWeatherDataView();

                mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

    private void openLocationMap() {

        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {

            startActivity(intent);

        } else {

            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");

            Toast.makeText(this, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        }

        if (id == R.id.action_map) {
            openLocationMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
