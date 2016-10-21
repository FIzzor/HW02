package edu.calvin.cs262.lab06;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Reads openweathermap's RESTful API for weather forecasts.
 * The code is based on Deitel's WeatherViewer (Chapter 17), simplified based on Murach's NewsReader (Chapter 10).
 *
 * for CS 262, HW02
 *
 * @author kvlinden
 * @version summer, 2016\
 *
 * @author Jake Schott
 * @version Fall, 2016
 *
 *
 */
public class MainActivity extends AppCompatActivity {

    private EditText idText;
    private Button fetchButton;

    private List<Players> playersList = new ArrayList<>();
    private ListView itemsListView;


    /* This formater can be used as follows to format temperatures for display.
     *     numberFormat.format(SOME_DOUBLE_VALUE)
     */
    //private NumberFormat numberFormat = NumberFormat.getInstance();

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idText = (EditText) findViewById(R.id.idText);
        fetchButton = (Button) findViewById(R.id.fetchButton);
        itemsListView = (ListView) findViewById(R.id.weatherListView);

        // See comments on this formatter above.
        //numberFormat.setMaximumFractionDigits(0);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard(idText);
                new GetWeatherTask().execute(createURL(idText.getText().toString()));
            }
        });

        //This just changes the header color
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ff0000"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    /**
     * Formats a URL for the webservice specified in the string resources.
     *
     * @param id the target name
     * @return URL formatted for openweathermap.com
     */
    private URL createURL(String id) {
        try {
            String urlString = getString(R.string.web_service_url) +
                    URLEncoder.encode(id, "UTF-8");
            return new URL(urlString);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    /**
     * Deitel's method for programmatically dismissing the keyboard.
     *
     * @param view the TextView currently being edited
     */
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Inner class for GETing the current weather data from openweathermap.org asynchronously
     */
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            StringBuilder result = new StringBuilder();
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return new JSONObject(result.toString());
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            if (weather != null) {
                Log.d(TAG, weather.toString());
                convertJSONtoArrayList(weather);
                MainActivity.this.updateDisplay();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Converts the JSON weather output data to an arraylist suitable for a listview adapter
     *
     * @param output
     */
    private void convertJSONtoArrayList(JSONObject output) {
        playersList.clear(); // clear old weather data
        try {
            JSONArray list = output.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject player = list.getJSONObject(i);
                int info = player.getInt("id");
                String email = player.getString("emailaddress");
                String name = player.getString("name");
                //JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                playersList.add(new Players(
                        info,
                        email,
                        name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh the weather data on the forecast ListView through a simple adapter
     */
    private void updateDisplay() {
        if (playersList == null) {
            Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (Players item : playersList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", item.getId());
            map.put("emailaddress", item.getEmailaddress());
            map.put("name",item.getName().toString());
            data.add(map);
        }

        int resource = R.layout.activity_players;
        String[] from = {"id", "emailaddress","name"};
        int[] to = {R.id.dayTextView, R.id.summaryTextView, R.id.minTextView, R.id.maxTextView};

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
    }

}