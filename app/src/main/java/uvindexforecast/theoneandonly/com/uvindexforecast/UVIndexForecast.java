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
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UVIndexForecast extends AppCompatActivity {

    private SharedPreferences prefs;
    private Location preferredLocation;
    private static ArrayList<Location> locationResultList;
    private static final int SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uvindex_forecast);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferredLocation = new Location();

        InputStream inputStream = getResources().openRawResource(R.raw.site_list_towns_en);
        HandleCSV csvFile = new HandleCSV(inputStream);
        locationResultList = new ArrayList<>();
        csvFile.read(locationResultList);

        refreshData();
        turnOnNotification();
    }

    private void refreshData() {
        String firstURL = "http://dd.weather.gc.ca/citypage_weather/xml/";
        String secondURL = "/";
        String thirdURL = "_e.xml";

        if (prefs == null)
            return;
        preferredLocation.setLocation_name(prefs.getString("locationName", null));
        preferredLocation.setProvince(prefs.getString("province", null));
        preferredLocation.setCode(prefs.getString("code", null));
        TextView ed1 = (TextView) findViewById(R.id.editText);
        HandleXML obj = new HandleXML(firstURL + preferredLocation.getProvince() + secondURL + preferredLocation.getCode() + thirdURL);
        obj.fetchXML();
        while (obj.parsingComplete) ;
        ed1.setText(obj.getUVIndex());
        Log.d("Refreshed", preferredLocation.getLocationName());
        showNotification();
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

            case R.id.action_refresh:
                refreshData();

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
        //Log.d("shortLocationFirst", shortLocationList.get(0).toString());
        return shortLocationList;
    }

    public void showNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent("uvindexforecast.theoneandonly.com.uvindexforecast");
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());

        mBuilder.setAutoCancel(false);
        mBuilder.setContentTitle("UV Index Alert");
        mBuilder.setTicker("ticker text here");
        mBuilder.setContentText("Wear sunscreen tomorrow!");
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_day);
        mBuilder.setContentIntent(mPendingIntent);
        //mBuilder.setOngoing(true);
        //API level 16
        mBuilder.setSubText("The predicted UV index for tomorrow is 9");
        mBuilder.setNumber(150);
        mBuilder.build();
        Notification mNotification = mBuilder.build();
        notificationManager.notify(0, mNotification);
    }

    public void turnOnNotification() {
        if (prefs.getString("Notification", null) == "True") {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");

            PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 0);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
        }
    }
}
