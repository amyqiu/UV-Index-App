package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class UVIndexForecast extends AppCompatActivity {
    private String URL = "http://dd.weather.gc.ca/citypage_weather/xml/ON/s0000585_e.xml";
    private String firstURL = "http://dd.weather.gc.ca/citypage_weather/xml/";
    private String secondURL = "/";
    private String thirdURL = "_e.xml";
    private HandleXML obj;
    public static ArrayList <Location> locationResultList;
    static final int SETTINGS = 1;
    TextView ed1;
    private SharedPreferences prefs;
    private Location preferredLocation;

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
    }

    private void refreshData() {

        if (prefs == null)
            return;
        preferredLocation.setLocation_name(prefs.getString("locationName", null));
        preferredLocation.setProvince(prefs.getString("province", null));
        preferredLocation.setCode(prefs.getString("code", null));
        ed1=(TextView)findViewById(R.id.editText);
        obj = new HandleXML(firstURL + preferredLocation.getProvince() + secondURL + preferredLocation.getCode() + thirdURL);
        obj.fetchXML();
        while(obj.parsingComplete);
        ed1.setText(obj.getUVIndex());
        Log.d("Refreshed",preferredLocation.getLocationName());
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
                startActivityForResult(new Intent(this, Settings.class),SETTINGS);
                return true;

            case R.id.action_refresh:
                refreshData();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static List<Location> getLocationResultList(String locationName)
    {
        int j = 0;
        List <Location> shortLocationList = new ArrayList<>();
        for (int i = 0; i < locationResultList.size(); i++){
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

}
