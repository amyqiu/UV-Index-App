package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.content.Intent;
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
    private HandleXML obj;
    public List <Location> locationResultList;
    TextView ed1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uvindex_forecast);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        InputStream inputStream = getResources().openRawResource(R.raw.site_list_towns_en);
        HandleCSV csvFile = new HandleCSV(inputStream);
        locationResultList = new ArrayList<Location>();
        csvFile.read(locationResultList);
        Log.d("Read_list", locationResultList.get(1).toString());

        ed1=(TextView)findViewById(R.id.editText);
        obj = new HandleXML(URL);
        obj.fetchXML();
        Log.d("URL_name", URL);
        //while(obj.parsingComplete);
        ed1.setText(obj.getUVIndex());

        Intent i = new Intent(this, LocationAdapter.class);
        i.putExtra("myLocationList", (Serializable)locationResultList);
        Log.d("Result", locationResultList.get(1).toString());
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
                startActivity(new Intent(this, Settings.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public List<Location> getLocationResultList()
    {
        return locationResultList;
    }
}
