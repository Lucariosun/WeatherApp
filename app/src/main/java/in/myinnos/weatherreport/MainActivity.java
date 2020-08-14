package in.myinnos.weatherreport;

/**
 * Final Project-543.
 * Authors=Rahul, Manvitha, sharwari.
 */
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.voice.ui.VoiceInputDialogFragment;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import ai.api.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import in.myinnos.weatherreport.apiInterface.ApiClient;
import in.myinnos.weatherreport.apiInterface.ApiInterface;
import in.myinnos.weatherreport.model.ForecastMainModel;
import in.myinnos.weatherreport.model.WeatherBaseModel;
import in.myinnos.weatherreport.apiInterface.ApiForecastInterface;
import in.myinnos.weatherreport.model.ForecastBaseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

import com.google.android.material.snackbar.Snackbar;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.content.Intent;

import androidx.annotation.NonNull;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ToggleButton;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class MainActivity extends AppCompatActivity implements  AIListener, TextToSpeech.OnInitListener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.liMainLayout)
    LinearLayout liMainLayout;

    @BindView(R.id.txCityName)
    TextView txCityName;
    @BindView(R.id.txWeather)
    TextView txWeather;
    @BindView(R.id.txWind)
    TextView txWind;
    @BindView(R.id.dateView)
    TextView dateView;
    @BindView(R.id.txRain1)
    TextView txRain1;
    @BindView(R.id.txRain2)
    TextView txRain2;
    @BindView(R.id.txCloudiness)
    TextView txCloudiness;
    @BindView(R.id.txPressure)
    TextView txPressure;
    @BindView(R.id.txHumidity)
    TextView txHumidity;

    @BindView(R.id.parameters)
    TextView parametersTextView;

    @BindView(R.id.locTime)
    TextView locTime;
    @BindView(R.id.digitalClock)
    TextView digitalClock;

    private TextToSpeech tts;

    @BindView(R.id.txSpeak)
    TextView txSpeak;


    private static final int PERMISSION_REQUEST_AUDIO = 0;
    private static final String TAG = "MainActivity";

    String userLocation;

    private AIConfiguration config;
    private AIService aiService;

    private String type = "", location = "", date = "";

    private boolean listening = false;

    VoiceInputDialogFragment voiceInputDialogFragment = new VoiceInputDialogFragment();

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    @BindView(R.id.simpleToggleButton)
    ToggleButton tempToggle;

    @BindView(R.id.weatherIcon)
    ImageView weatherIcon;

    @BindView(R.id.rain1Icon)
    ImageView rain1Icon;

    @BindView(R.id.rain2Icon)
    ImageView rain2Icon;

    @BindView(R.id.myLocation)
    ImageView myLocation;

    String currentTempType = "", tempCode, tempUnit, speedUnit, apiUnit, locationTimeZone = "GMT";
    String currentTime = "";
//    List rainInfo;
    ArrayList<ForecastMainModel> rainInfo = new ArrayList<ForecastMainModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Boolean ToggleButtonState = tempToggle.isChecked(); // check current state of a toggle button (true or false).
//      toggle.getText();

        tts = new TextToSpeech(this, this);

        config = new AIConfiguration("d4729d7dd19f43e2aeb73a3df73e7a4d",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        txSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });

        tempToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String presentLocation = location != "" ? location : userLocation;
                getWeatherData(presentLocation, type, date);
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeatherData(userLocation, "all", date);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        //getWeatherData("New York");
    }


    private void listen() {
        listening = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
//            VoiceInputDialogFragment voiceInputDialogFragment = new VoiceInputDialogFragment();
//            voiceInputDialogFragment.setSuggestions(
//                    "Stony Brook",
//                    "New York",
//                    "California"
//            );
            voiceInputDialogFragment.show(getSupportFragmentManager(), "DIALOG_INPUT");
            voiceInputDialogFragment.setAutoStart(false);
            aiService.startListening();
        } else {
            // Permission is missing and must be requested.
            requestAudioPermission();
        }
    }

    private void weatherCommandBasedQuery(String location) {
        hideUIElements();

        ApiInterface apiService =
                ApiClient.getClient(true).create(ApiInterface.class);

        Call<WeatherBaseModel> weatherBaseModelCall =
                apiService.getWeatherData("e972b01b092d6945d0effe3014ff9ec9", location, apiUnit);

        String url = String.valueOf(weatherBaseModelCall.request().url());
        Log.d("TAG_WEATHER", url);

        weatherBaseModelCall.enqueue(new Callback<WeatherBaseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherBaseModel> call, Response<WeatherBaseModel> response) {
                ////////////////////////////////// content empty view

                progressBar.setVisibility(View.GONE);

                try {
                    locationTimeZone = findTimeZone(response.body().getTimezone());
                    setTime();

                    liMainLayout.setVisibility(View.VISIBLE);
                    txCityName.setText(location.toUpperCase());
                    txWeather.setText(response.body().getWeatherMainModel().getTemp() + tempCode);
//                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon01n));
                    txWind.setVisibility(View.VISIBLE);
                    txCloudiness.setVisibility(View.VISIBLE);
                    txPressure.setVisibility(View.VISIBLE);
                    txHumidity.setVisibility(View.VISIBLE);


                    String icon = "iconb" + response.body().getWeatherWeatherModel().get(0).getWeatherIcon();
                    weatherIcon.setImageResource(getResources().getIdentifier(icon, "drawable", getPackageName()));

                    txWind.setText(response.body().getWeatherWindModel().getSpeed() + speedUnit);
                    txCloudiness.setText(response.body().getWeatherWeatherModel().get(0).getDescription());
                    txPressure.setText(response.body().getWeatherMainModel().getPressure() + " hpa pressure");
                    txHumidity.setText(response.body().getWeatherMainModel().getHumidity() + "% humidity");

                    speakOut(response.body().getWeatherMainModel().getTemp() + tempUnit+ " and "
                            + response.body().getWeatherWeatherModel().get(0).getDescription());

                    Log.d("TAG_WEATHER", String.valueOf(response.body().getWeatherMainModel().getHumidity()));

                } catch (Exception ignored) {
                    liMainLayout.setVisibility(View.GONE);
//                    Toast toast = Toast.makeText(getApplicationContext(), "something went wrong! Please try with city name", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120);
//                    toast.show();
                    speakOut("Sorry, location not found");
                }
            }

            @Override
            public void onFailure(Call<WeatherBaseModel> call, Throwable t) {
                liMainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.d("TAG_WEATHER", String.valueOf(t.getMessage()));
            }
        });
    }

    private void findCurrentWeather(String location) {
        ApiInterface apiService =
                ApiClient.getClient(true).create(ApiInterface.class);

        Call<WeatherBaseModel> weatherBaseModelCall =
                apiService.getWeatherData("e972b01b092d6945d0effe3014ff9ec9", location, apiUnit);

        String url = String.valueOf(weatherBaseModelCall.request().url());
        Log.d("TAG_WEATHER", url);

        weatherBaseModelCall.enqueue(new Callback<WeatherBaseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherBaseModel> call, Response<WeatherBaseModel> response) {
                ////////////////////////////////// content empty view

                progressBar.setVisibility(View.GONE);

                try {
                    liMainLayout.setVisibility(View.VISIBLE);
                    txCityName.setText(location.toUpperCase());
                    txWeather.setText(response.body().getWeatherMainModel().getTemp() + tempCode);
                    String icon = "iconb" + response.body().getWeatherWeatherModel().get(0).getWeatherIcon();
                    weatherIcon.setImageResource(getResources().getIdentifier(icon, "drawable", getPackageName()));
                    String desc = response.body().getWeatherWeatherModel().get(0).getDescription();
                    txCloudiness.setVisibility(View.VISIBLE);
                    txCloudiness.setText(desc);
                } catch (Exception ignored) {
                    liMainLayout.setVisibility(View.GONE);
                    //speakOut("Sorry, location not found");
                }
            }

            @Override
            public void onFailure(Call<WeatherBaseModel> call, Throwable t) {
                liMainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.d("TAG_FORECAST", String.valueOf(t.getMessage()));
            }
        });
    }

    private void forecastCommandBasedQuery(String location, String date) {
        hideUIElements();
        findCurrentWeather(location);

        ApiForecastInterface apiService =
                ApiClient.getClient(true).create(ApiForecastInterface.class);

        Call<ForecastBaseModel> forecastBaseModelCall =
                apiService.getWeatherData("e972b01b092d6945d0effe3014ff9ec9", location, "metric");

        String url = String.valueOf(forecastBaseModelCall.request().url());
        Log.d("TAG_WEATHER", url);

        forecastBaseModelCall.enqueue(new Callback<ForecastBaseModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ForecastBaseModel> call, Response<ForecastBaseModel> response) {
                ////////////////////////////////// content empty view

                progressBar.setVisibility(View.GONE);

                try {

                    locationTimeZone = findTimeZone(response.body().getForecastCityModel().getTimeZone());
                    setTime();

                    updateDataToLocalTime(response.body());
                    for(int i= 0; i< response.body().getForecastMainModel().size(); i++) {
                        if(response.body().getForecastMainModel().get(i).getLocalTime().contains(date)) {

                            for (int j = 0; j < response.body().getForecastMainModel().get(i).getWeatherWeatherModel().size(); j++) {
                                if (response.body().getForecastMainModel().get(i).getWeatherWeatherModel().get(j).getMain().contains("Rain")) {
                                    Log.i(TAG, "rain present");
//                                    String tempLocalTime = convertUTCtoLocal(response.body().getForecastMainModel().get(i).getUnixUTC());
//                                    response.body().getForecastMainModel().get(i).setLocalTime(tempLocalTime);
                                    rainInfo.add(response.body().getForecastMainModel().get(i));
                                } else {
                                    Log.i(TAG, "no rain info");
                                }
                            }
                        }
                    }
                    rainInfo = removeDuplicates(rainInfo);

                    if(rainInfo.size() == 1) {
                        setDateView(date);
                        txRain1.setVisibility(View.VISIBLE);
                        rain1Icon.setVisibility(View.VISIBLE);

                        String icon1 = "iconb" + rainInfo.get(0).getWeatherWeatherModel().get(0).getWeatherIcon();
                        rain1Icon.setImageResource(getResources().getIdentifier(icon1, "drawable", getPackageName()));

                        String[] rain1Time = splitString(rainInfo.get(0).getLocalTime());
                        txRain1.setText(rain1Time[1] + " "+ rain1Time[2] + " - " +
                                rainInfo.get(0).getWeatherWeatherModel().get(0).getDescription());
                        speakOut("Yes, probability  of " +
                                rainInfo.get(0).getWeatherWeatherModel().get(0).getDescription() + " at " + rain1Time[1] + rain1Time[2] );
                    }
                    else if(rainInfo.size() > 1) {
                        setDateView(date);
                        txRain1.setVisibility(View.VISIBLE);
                        txRain2.setVisibility(View.VISIBLE);
                        rain1Icon.setVisibility(View.VISIBLE);
                        rain2Icon.setVisibility(View.VISIBLE);
                        //txRain1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon11d, 0, 0, 0 );

                        String icon1 = "iconb" + rainInfo.get(0).getWeatherWeatherModel().get(0).getWeatherIcon();
                        rain1Icon.setImageResource(getResources().getIdentifier(icon1, "drawable", getPackageName()));

                        String icon2 = "iconb" + rainInfo.get(1).getWeatherWeatherModel().get(0).getWeatherIcon();
                        rain2Icon.setImageResource(getResources().getIdentifier(icon2, "drawable", getPackageName()));

                        String[] rain1Time = splitString(rainInfo.get(0).getLocalTime());
                        String[] rain2Time = splitString(rainInfo.get(1).getLocalTime());
                        txRain1.setText(rain1Time[1] + " "+ rain1Time[2] + " - " + rainInfo.get(0).getWeatherWeatherModel().get(0).getDescription());

                        txRain2.setText(rain2Time[1] + " "+ rain2Time[2] + " - " + rainInfo.get(1).getWeatherWeatherModel().get(0).getDescription());


                        speakOut("Yes, probability  of " +
                                rainInfo.get(0).getWeatherWeatherModel().get(0).getDescription() + " at " + rain1Time[1] + rain1Time[2] +
                                " and " + rainInfo.get(1).getWeatherWeatherModel().get(0).getDescription() + " at " + rain2Time[1] + rain2Time[2]);
                    }
                    else {
                        speakOut("No further forecast of rain in" + location);
                    }

                } catch (Exception ignored) {
                    liMainLayout.setVisibility(View.GONE);
                    speakOut("Sorry, location not found");
                }
                rainInfo.clear();
            }

            @Override
            public void onFailure(Call<ForecastBaseModel> call, Throwable t) {
                liMainLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.d("TAG_FORECAST", String.valueOf(t.getMessage()));
            }
        });
    }

    private void getWeatherData(String requestedLocation, String type, String date) {

        location = requestedLocation;
        if(listening == true) {
            voiceInputDialogFragment.dismiss();
        }

        if(date.equals("")) {
//            Date today = new Date(System.currentTimeMillis());
////          SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
//            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
//            date = formatter.format(today);
            date = getDateTime("date");
        }

        progressBar.setVisibility(View.VISIBLE);


        String tempType = (String) tempToggle.getText();
        if (tempType.contains("C") && !currentTempType.contains("C")) {
            setCelsiusUnits(tempType);
        }
        else if (tempType.contains("F") && !currentTempType.contains("F")) {
            setFahrenheitUnits(tempType);
        }

        if(type.equals("rain") ) {
            forecastCommandBasedQuery(location, date);
        }
        else {
            weatherCommandBasedQuery(location);
        }

        //forecastCommandBasedQuery(requestedLocation, "2020-05-18");

    }


    /**Helper Methods*/

    private void setTime() {
        if (locationTimeZone.equalsIgnoreCase("GMT-4")) {
            digitalClock.setVisibility(View.VISIBLE);
        } else{
            currentTime = getDateTime("");
            digitalClock.setVisibility(View.GONE);
            locTime.setVisibility(View.VISIBLE);
            locTime.setText(currentTime);
        }
    }

    private String getDateTime(String value) {
    String finalResult ="";

        if (value.equals("date")) {
            Date today = new Date(System.currentTimeMillis());
//          SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
            finalResult = formatter.format(today);
        }
        else{
            Date today = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(locationTimeZone));
            finalResult = formatter.format(today);
        }

        return finalResult;

    }

    private void setDateView(String requestedDate) {
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2= new SimpleDateFormat("dd MMM yyyy");
        try {
            Date date1 = formatter1.parse(requestedDate);
            requestedDate = formatter2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateView.setVisibility(View.VISIBLE);
        dateView.setText(requestedDate);
    }

    private ArrayList removeDuplicates(ArrayList<ForecastMainModel> l) {

        Set<ForecastMainModel> s = new TreeSet<ForecastMainModel>(new Comparator<ForecastMainModel>() {

            @Override
            public int compare(ForecastMainModel o1, ForecastMainModel o2) {
                // ... compare the two object according to your requirements

                if(o1.getWeatherWeatherModel().get(0).getDescription().equalsIgnoreCase(o2.getWeatherWeatherModel().get(0).getDescription())) {
                    return 0;
                }
                return 1;

            }
        });
        s.addAll(l);
        ArrayList updatedRainInfo = new ArrayList(s);
        return updatedRainInfo;

    }

    private void updateDataToLocalTime(ForecastBaseModel response) {
        for(int i= 0; i< response.getForecastMainModel().size(); i++) {
            String tempLocalTime = convertUTCtoLocal(response.getForecastMainModel().get(i).getUnixUTC());
            response.getForecastMainModel().get(i).setLocalTime(tempLocalTime);
        }
    }

    private String findTimeZone(int timeZone) {
        double actualTimezone = (double) timeZone/3600;
        int tz = (int) Math.floor(actualTimezone);
        String timeZoneValue;
        if(tz == actualTimezone) {
            timeZoneValue = Integer.toString(tz);
        }
        else {
            timeZoneValue = tz +":30";
        }
        int value = Integer.signum(timeZone);
        if(value == 1) {
            return "GMT+"+timeZoneValue;
        }
        else {
            return "GMT"+timeZoneValue;
        }
    }

    private String convertUTCtoLocal(long unixUTC) {
//                            long unix_seconds = 1589706000;
        long unix_seconds = unixUTC;
        //convert seconds to milliseconds
        Date date = new Date(unix_seconds*1000L);
        // format of the date
//                    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//                    SimpleDateFormat jdf = new SimpleDateFormat("HH:mm:ss");
//                    SimpleDateFormat jdf = new SimpleDateFormat("MMM dd HH:mm a");
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        jdf.setTimeZone(TimeZone.getTimeZone(locationTimeZone));
        String java_date = jdf.format(date);
        System.out.println("\n"+java_date+"\n");
        Log.i(TAG, java_date);
        //speakOut(java_date);
        return java_date;
    }

    private String[] splitString(String string) {
        String str = string;
        String[] updatedString = str.split("\\s+");
        return  updatedString;
    }

    private void setCelsiusUnits(String tempType) {
        currentTempType = tempType;
        tempCode = " \u2103";
        tempUnit = " celsius";
        speedUnit = " m/s wind speed";
        apiUnit = "metric";
    }

    private void setFahrenheitUnits(String tempType) {
        currentTempType = tempType;
        tempCode = " \u2109";
        tempUnit = " fahrenheit";
        speedUnit = " miles/hour wind speed";
        apiUnit = "Imperial";
    }

    private void hideUIElements() {
        txWind.setVisibility(View.GONE);
        txCloudiness.setVisibility(View.GONE);
        txPressure.setVisibility(View.GONE);
        txHumidity.setVisibility(View.GONE);
        txRain1.setVisibility(View.GONE);
        txRain2.setVisibility(View.GONE);
        rain1Icon.setVisibility(View.GONE);
        rain2Icon.setVisibility(View.GONE);
        dateView.setVisibility(View.GONE);
        locTime.setVisibility(View.GONE);
    }

//    @OnClick(R.id.txSpeak)
//    void setTxSpeak() {
//
//        if (!Voice.isRecordAudioPermissionGranted(getApplicationContext())) {
//            new VoicePermissionDialogFragment().show(getSupportFragmentManager(), "DIALOG_PERMISSION");
//        } else {
//            VoiceInputDialogFragment voiceInputDialogFragment = new VoiceInputDialogFragment();
//            voiceInputDialogFragment.setSuggestions(
//                    "London",
//                    "New York",
//                    "Hyderabad"
//            );
//            voiceInputDialogFragment.show(getSupportFragmentManager(), "DIALOG_INPUT");
//            voiceInputDialogFragment.setAutoStart(true);
//        }
//    }

//    @Override
//    public void onResults(String[] strings) {
//        getWeatherData(strings[0]);
//    }

    /**AI listener Based Methods */

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        type="";
        date="";

        if(result.getAction().contains("smalltalk") || result.getMetadata().getIntentName().contains("Default")) {
            String speech = result.getFulfillment().getSpeech();
            speakOut(speech);
            voiceInputDialogFragment.dismiss();
        }
        else {

            final HashMap<String, JsonElement> params = result.getParameters();

            if (params != null && !params.isEmpty()) {
                if (params.containsKey("type") && params.containsKey("city")) {
                    type = params.get("type").getAsString();
                    location = params.get("city").getAsString();
                    if(params.containsKey("date")) {
                        date = params.get("date").getAsString();
                    }
                    Log.i(TAG, "city based query");
                    //type = type.replaceAll("^\"|\"$", "");
                } else if (params.containsKey("type") && params.containsKey("state")) {
                    type = params.get("type").getAsString();
                    location = params.get("state").getAsString();
                    if(params.containsKey("date")) {
                        date = params.get("date").getAsString();
                    }
                    Log.i(TAG, "state based query");
                }
                else if (params.containsKey("type") && params.containsKey("country")) {
                    type = params.get("type").getAsString();
                    location = params.get("country").getAsString();
                    if(params.containsKey("date")) {
                        date = params.get("date").getAsString();
                    }
                    Log.i(TAG, "country based query");
                }
                else if(params.containsKey("type") && params.containsKey("date")) {
                    type = params.get("type").getAsString();
                    date = params.get("date").getAsString();
                    location = userLocation;
                }
                else if(params.containsKey("type")) {
                    type = params.get("type").getAsString();
                    location = userLocation;
                }
                else if(params.containsKey("city")) {
                    type = "all";
                    location = params.get("city").getAsString();
                }
                else if(params.containsKey("state")) {
                    type = "all";
                    location = params.get("state").getAsString();
                }
                else if(params.containsKey("country")) {
                    type = "all";
                    location = params.get("country").getAsString();
                }
                Log.i(TAG, type);
                Log.i(TAG, location);
                Log.i(TAG, date);
            }

            parametersTextView.setText(getString(R.string.parameters, type + " " + location));
            getWeatherData(location, type, date);
        }
    }

    @Override
    public void onError(AIError error) {
        Log.d(TAG, "Listening error: " + error.getMessage());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {
        //listenButton.setText(R.string.ask);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**Audio Based Methods */

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //speakOut("Hi");
//                speakOut("Hi I am Miss Nimbus your weather vane");
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }

    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void requestAudioPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(findViewById(R.id.main_container), getString(R.string.permission_text_audio),
                    Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSION_REQUEST_AUDIO);
                }
            }).show();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_AUDIO);
        }
    }

    /**Location Based Methods */
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();
                                    Geocoder geocoder = new Geocoder(getApplicationContext(),
                                            Locale.getDefault());

                                    try {
                                        List<Address> listAddresses = geocoder.getFromLocation(latitude,
                                                longitude, 1);
                                        if (null != listAddresses && listAddresses.size() > 0) {
                                            String subLocality = listAddresses.get(0).getLocality();
                                            userLocation = subLocality;
                                            getWeatherData(userLocation, "", "");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//
//    }


}


