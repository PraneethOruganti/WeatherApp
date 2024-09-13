package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    EditText zipcodeEditText;
    Button confirmButton;
    ImageView curWeatherImage, forecastImage1, forecastImage2, forecastImage3, forecastImage4;
    TextView tvDate, tvCurCondition, tvCurHigh, tvCurLow, tvQuote, tvCurTemp, tvLat, tvLon, tvCity, tvCurTime;
    TextView tvForecastHigh1, tvForecastHigh2, tvForecastHigh3, tvForecastHigh4, tvForecastLow1, tvForecastLow2, tvForecastLow3, tvForecastLow4;
    TextView tvForecastTime1, tvForecastTime2, tvForecastTime3, tvForecastTime4;

    public static final String API_Key = "51d0b1a72bb036192fafa2b459073c3c";
    private String dataString, locationDataString, zipcode, randomString, curCondition, curConditionCode;
    private int weatherImgResource;
    JSONObject locationData;
    URL url;
    URLConnection connection;
    InputStream inputStream;
    BufferedReader br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zipcodeEditText = findViewById(R.id.zipcode_edittext);
        confirmButton = findViewById(R.id.confirm_button);
        tvDate = findViewById(R.id.date);
        curWeatherImage = findViewById(R.id.cur_weather_image);
        tvCurCondition = findViewById(R.id.condition);
        tvCurTemp = findViewById(R.id.cur_temp);
        tvCurHigh = findViewById(R.id.cur_high);
        tvCurLow = findViewById(R.id.cur_low);
        tvQuote = findViewById(R.id.quote);
        tvCity = findViewById(R.id.city);
        tvCurTime = findViewById(R.id.curTime);
        tvLat = findViewById(R.id.lat);
        tvLon = findViewById(R.id.lon);
        forecastImage1 = findViewById(R.id.forecast1_image);
        forecastImage2 = findViewById(R.id.forecast2_image);
        forecastImage3 = findViewById(R.id.forecast3_image);
        forecastImage4 = findViewById(R.id.forecast4_image);
        tvForecastHigh1 = findViewById(R.id.forecast_high1);
        tvForecastHigh2 = findViewById(R.id.forecast_high2);
        tvForecastHigh3 = findViewById(R.id.forecast_high3);
        tvForecastHigh4 = findViewById(R.id.forecast_high4);
        tvForecastLow1 = findViewById(R.id.forecast_low1);
        tvForecastLow2 = findViewById(R.id.forecast_low2);
        tvForecastLow3 = findViewById(R.id.forecast_low3);
        tvForecastLow4 = findViewById(R.id.forecast_low4);
        tvForecastTime1 = findViewById(R.id.forecast_time1);
        tvForecastTime2 = findViewById(R.id.forecast_time2);
        tvForecastTime3 = findViewById(R.id.forecast_time3);
        tvForecastTime4 = findViewById(R.id.forecast_time4);






        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AsyncThread task = new AsyncThread();
                if(zipcodeEditText.getText().toString().length() == 5)
                    task.execute(zipcodeEditText.getText().toString()); //string sent to asynctask
                else
                {
                    Toast.makeText(MainActivity.this, "Invalid Zipcode", Toast.LENGTH_SHORT).show();
                    zipcodeEditText.setText("");
                }

            }
        });


    }


    private class AsyncThread extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            zipcode = strings[0]; //string taken into asynctask from userinput
            try {
                //getting latitude and longitude from zipcode
                locationDataString=""; //reset
                url = new URL("https://api.openweathermap.org/geo/1.0/zip?zip=" + zipcode + ",US&appid=" + API_Key);
                connection = url.openConnection();
                inputStream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(inputStream));
                for(String curLine = br.readLine(); curLine!= null; curLine = br.readLine())
                    locationDataString+=curLine;
                br.close();
                locationData = new JSONObject(locationDataString);



                //getting weather data from lat and lon
                dataString=""; //reset
                url = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + locationData.get("lat").toString() + "&lon=" + locationData.get("lon").toString() + "&units=imperial&appid=" + API_Key);
                Log.d("URL", url.toString()); //keep this
                connection = url.openConnection();
                inputStream = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(inputStream));
                for(String curLine = br.readLine(); curLine!= null; curLine = br.readLine())
                    dataString +=curLine;
                br.close();
                return dataString;

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String dataString)
        {
            try {
                JSONObject data = new JSONObject(dataString);
                //one time setups
                tvCity.setText(data.getJSONObject("city").getString("name"));
                randomString = "Lat: " + data.getJSONObject("city").getJSONObject("coord").getString("lat");
                tvLat.setText(randomString);
                randomString = "Lon: " + data.getJSONObject("city").getJSONObject("coord").getString("lon");
                tvLon.setText(randomString);
                JSONObject curData = (JSONObject)(data.getJSONArray("list").get(0));
                tvDate.setText(reformatTime(curData.getLong("dt"), true));
                randomString = String.valueOf(curData.getJSONObject("main").getDouble("temp")) + "°F";
                tvCurTemp.setText(randomString);

                //repeated for other forecasts
                randomString = "H: " + curData.getJSONObject("main").getDouble("temp_max") + "°F";
                tvCurHigh.setText(randomString);
                randomString = "L: " + curData.getJSONObject("main").getDouble("temp_min") + "°F";
                tvCurLow.setText(randomString);
                curCondition = curData.getJSONArray("weather").getJSONObject(0).getString("main");
                tvCurCondition.setText(curCondition);
                curConditionCode = String.valueOf(curData.getJSONArray("weather").getJSONObject(0).getInt("id"));
                switch(curConditionCode.charAt(0)) //fill this out for every weather condition description possible
                {
                    case '2':
                        weatherImgResource = R.drawable.thunderstorm;
                        tvQuote.setText("\"So this is how liberty dies...with thunderous applause\" - Padme Amidala");
                        break;
                    case '3':
                        weatherImgResource = R.drawable.rain;
                        tvQuote.setText("\"I want to go home and rethink my life\"- Death Sticks Seller");
                        break;
                    case '5':
                        weatherImgResource = R.drawable.shower_rain;
                        tvQuote.setText("\"Help me Obi-Wan Kenobi, you're my only hope\" - Leia Organa");
                        break;
                    case '6':
                        weatherImgResource = R.drawable.snow;
                        tvQuote.setText("\"There isn't enough life on this ice cube to fill a space cruiser\" - Han Solo");
                        break;
                    case '7':
                        weatherImgResource = R.drawable.mist;
                        tvQuote.setText("\"I see through the lies of the Jedi. I do not fear the Dark Side as you do!\" - Anakin Skywalker");
                        break;
                    case '8':
                        if(curConditionCode.charAt(2) == 0)
                        {
                            weatherImgResource = R.drawable.clear_sky;
                            tvQuote.setText("\"Oh my dear friend, how I've missed you!\" - C-3PO");
                        }
                        else
                        {
                            weatherImgResource = R.drawable.broken_clouds;
                            tvQuote.setText("\"The dark side clouds everything\" - Yoda");
                        }

                        break;
                    default:
                        break;
                }
                curWeatherImage.setImageResource(weatherImgResource);
                long curTimeLong = curData.getLong("dt"); //gets date and text
                tvCurTime.setText(reformatTime(curTimeLong, false));




                String[] forecastHighs = new String[4];
                String[] forecastLows = new String[4];
                Long[] unixTimes = new Long[4];
                String[] forecastWeatherCodes = new String[4];

                for(int i = 1; i <= 4; i++)
                {
                    forecastHighs[i-1] = "H: " + ((JSONObject)(data.getJSONArray("list").get(i))).getJSONObject("main").getDouble("temp_max") + "°F";
                    forecastLows[i-1] = "L: " + ((JSONObject)(data.getJSONArray("list").get(i))).getJSONObject("main").getDouble("temp_min") + "°F";
                    unixTimes[i-1] =  ((JSONObject)(data.getJSONArray("list").get(i))).getLong("dt");
                    forecastWeatherCodes[i-1] = String.valueOf(((JSONObject)(data.getJSONArray("list").get(i))).getJSONArray("weather").getJSONObject(0).getInt("id"));
                }

                tvForecastHigh1.setText(forecastHighs[0]);
                tvForecastLow1.setText(forecastLows[0]);
                switch(forecastWeatherCodes[0].charAt(0))
                {
                    case '2':
                        weatherImgResource = R.drawable.thunderstorm;
                        break;
                    case '3':
                        weatherImgResource = R.drawable.rain;
                        break;
                    case '5':
                        weatherImgResource = R.drawable.shower_rain;
                        break;
                    case '6':
                        weatherImgResource = R.drawable.snow;
                        break;
                    case '7':
                        weatherImgResource = R.drawable.mist;
                        break;
                    case '8':
                        if(curConditionCode.charAt(2) == 0)
                            weatherImgResource = R.drawable.clear_sky;
                        else
                            weatherImgResource = R.drawable.broken_clouds;
                        break;
                    default:
                        break;
                }
                forecastImage1.setImageResource(weatherImgResource);
                tvForecastTime1.setText(reformatTime(unixTimes[0], false));

                tvForecastHigh2.setText(forecastHighs[1]);
                tvForecastLow2.setText(forecastLows[1]);
                switch(forecastWeatherCodes[1].charAt(0))
                {
                    case '2':
                        weatherImgResource = R.drawable.thunderstorm;
                        break;
                    case '3':
                        weatherImgResource = R.drawable.rain;
                        break;
                    case '5':
                        weatherImgResource = R.drawable.shower_rain;
                        break;
                    case '6':
                        weatherImgResource = R.drawable.snow;
                        break;
                    case '7':
                        weatherImgResource = R.drawable.mist;
                        break;
                    case '8':
                        if(curConditionCode.charAt(2) == 0)
                            weatherImgResource = R.drawable.clear_sky;
                        else
                            weatherImgResource = R.drawable.broken_clouds;
                        break;
                    default:
                        break;
                }
                forecastImage2.setImageResource(weatherImgResource);
                tvForecastTime2.setText(reformatTime(unixTimes[1], false));

                tvForecastHigh3.setText(forecastHighs[2]);
                tvForecastLow3.setText(forecastLows[2]);
                switch(forecastWeatherCodes[2].charAt(0))
                {
                    case '2':
                        weatherImgResource = R.drawable.thunderstorm;
                        break;
                    case '3':
                        weatherImgResource = R.drawable.rain;
                        break;
                    case '5':
                        weatherImgResource = R.drawable.shower_rain;
                        break;
                    case '6':
                        weatherImgResource = R.drawable.snow;
                        break;
                    case '7':
                        weatherImgResource = R.drawable.mist;
                        break;
                    case '8':
                        if(curConditionCode.charAt(2) == 0)
                            weatherImgResource = R.drawable.clear_sky;
                        else
                            weatherImgResource = R.drawable.broken_clouds;
                        break;
                    default:
                        break;
                }
                forecastImage3.setImageResource(weatherImgResource);
                tvForecastTime3.setText(reformatTime(unixTimes[2], false));

                tvForecastHigh4.setText(forecastHighs[3]);
                tvForecastLow4.setText(forecastLows[3]);
                switch(forecastWeatherCodes[3].charAt(0))
                {
                    case '2':
                        weatherImgResource = R.drawable.thunderstorm;
                        break;
                    case '3':
                        weatherImgResource = R.drawable.rain;
                        break;
                    case '5':
                        weatherImgResource = R.drawable.shower_rain;
                        break;
                    case '6':
                        weatherImgResource = R.drawable.snow;
                        break;
                    case '7':
                        weatherImgResource = R.drawable.mist;
                        break;
                    case '8':
                        if(curConditionCode.charAt(2) == 0)
                            weatherImgResource = R.drawable.clear_sky;
                        else
                            weatherImgResource = R.drawable.broken_clouds;
                        break;
                    default:
                        break;
                }
                forecastImage4.setImageResource(weatherImgResource);
                tvForecastTime4.setText(reformatTime(unixTimes[3], false));



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public String reformatTime(long unixSeconds, boolean requestDate) //sent in date and time
        {
            Date date = new java.util.Date(unixSeconds*1000L);
            SimpleDateFormat sdf;
            if(requestDate)
                 sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            else
                sdf = new java.text.SimpleDateFormat("h:mm a z");

            sdf.setTimeZone(java.util.TimeZone.getTimeZone("EST"));
            String toReturn = sdf.format(date);
            if(toReturn.contains("G"))
                toReturn = toReturn.substring(0, toReturn.indexOf('G')) + "EST";
            return toReturn;
        }
    }
}
