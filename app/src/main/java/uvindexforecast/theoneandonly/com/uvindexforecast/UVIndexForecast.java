package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceFragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.PreferenceChangeListener;

public class UVIndexForecast extends AppCompatActivity {

    private SharedPreferences prefs;
    private Location preferredLocation;
    private static ArrayList<Location> locationResultList;
    private static final int SETTINGS = 1;

    // Preferences
    private SharedPreferences mPrefs = null;

    // Preference change listener
    private PreferenceChangeListener mPreferenceListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uvindex_forecast);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferenceListener = new PreferenceChangeListener();
        mPrefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferredLocation = new Location();

        //Sets UV index for the day
        TextView UV = (TextView) findViewById(R.id.editText);
        if (prefs.getString("locationName", null) == null)
        {
            UV.setText(getResources().getString(R.string.choose_location));
            UV.setTextSize(40);
        }

        //Parses location list
        InputStream inputStream = getResources().openRawResource(R.raw.site_list_towns_en);
        HandleCSV csvFile = new HandleCSV(inputStream);
        locationResultList = new ArrayList<>();
        csvFile.read(locationResultList);

        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPrefs.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refreshData();
    }

    //Retrieves UV index based on city
    private String refreshData() {

        String firstURL = "http://dd.weather.gc.ca/citypage_weather/xml/";
        String secondURL = "/";
        String thirdURL = "_e.xml";

        if (prefs == null)
            return null;
        preferredLocation.setLocation_name(prefs.getString("locationName", null));
        preferredLocation.setProvince(prefs.getString("province", null));
        preferredLocation.setCode(prefs.getString("code", null));
        TextView ed1 = (TextView) findViewById(R.id.editText);
        TextView tips = (TextView) findViewById(R.id.tips);
        HandleXML obj = new HandleXML(firstURL + preferredLocation.getProvince() + secondURL + preferredLocation.getCode() + thirdURL);
        obj.fetchXML();
        while (obj.parsingComplete) ;
        ed1.setText(obj.getUVIndex());
        ed1.setTextSize(90);
        int uv = Integer.parseInt(obj.getUVIndex());
        if (uv > 6)
        {
            tips.setText(getResources().getString(R.string.high_uv_tips));
        }
        else if (uv > 3 && uv <= 6)
        {
            tips.setText(getResources().getString(R.string.medium_uv_tips));
        }
        else
        {
            tips.setText(getResources().getString(R.string.low_uv_tips));
        }
        return obj.getUVIndex();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivityForResult(new Intent(this, Settings.class), SETTINGS);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static List<Location> getLocationResultList(String locationName) {
        int j = 0;
        List<Location> shortLocationList = new ArrayList<>();
        for (int i = 0; i < locationResultList.size(); i++) {
            if (locationResultList.get(i).getLocationName() != null && locationResultList.get(i).getLocationName().startsWith(locationName)) {
                Location newLocation = new Location(locationResultList.get(i).getCode(), locationResultList.get(i).getLocationName(), locationResultList.get(i).getProvince());
                Log.d("shortLocationList", newLocation.getLocationName());
                shortLocationList.add(j, newLocation);
                Log.d("shortLocationAdd", newLocation.toString());
                j++;
            }
        }
        return shortLocationList;
    }

    //Creates daily reoccurring notification
    public void turnOnNotification() {
        if (prefs.getBoolean("Notification", false)) {
            Log.d("Notification", "Notification called");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("currentUV", String.valueOf(refreshData()));
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, prefs.getInt("Hour", 8));
            cal.set(Calendar.MINUTE, prefs.getInt("Minute", 0));
            cal.set(Calendar.SECOND, 0);

            Log.d("Hour", String.valueOf(prefs.getInt("Hour", 8)));
            Log.d("Minute", String.valueOf(prefs.getInt("Minute", 0)));

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
        }
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals("Notification")  || key.equals("Hour") || key.equals("Minute")) {
                Log.d("Notified", "Notification called");
                String currentUV = refreshData();
                if (Integer.valueOf(currentUV) >= Integer.valueOf(prefs.getString("UV_threshold", null))){
                    turnOnNotification();
                }
            }
        }
    }

}
