package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.TextView;
import java.util.Calendar;

public class Settings extends AppCompatActivity implements
        View.OnClickListener {

    private int newHour, newMinute;
    Button btnTimePicker;
    Spinner spinner;
    EditText txtTime;
    Switch switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Sets location to be the last-selected value
        AutoCompleteTextView edt = (AutoCompleteTextView) this.findViewById(R.id.edtLocation);
        LocationAdapter adpt = new LocationAdapter(this, null);
        edt.setAdapter(adpt);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        edt.setText(prefs.getString("locationName", null) + ", " + prefs.getString("province", null));
        edt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location result = (Location) parent.getItemAtPosition(position);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                //Log.d("SwA", "WOEID [" + result.getWoeid() + "]");
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("code", result.getCode());
                editor.putString("locationName", result.getLocationName());
                editor.putString("province", result.getProvince());
                editor.commit();
                //NavUtils.navigateUpFromSameTask(Settings.this);

            }
        });

        //Sets notification toggle to last selected value
        switchButton = (Switch) findViewById(R.id.switch1);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("Notification", false)) {
            switchButton.setChecked(true);
        } else {
            switchButton.setChecked(false);
        }
        //Turns on notifications if toggle is switched
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on) {
                if (on) {
                    //Do something when Switch button is on/checked
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Notification", true);
                    editor.commit();
                    Log.d("Notification_on", "On");
                } else {
                    //Do something when Switch is off/unchecked

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Notification", false);
                    editor.commit();
                    Log.d("Notification_off", "Off");
                }
            }
        });
        if (switchButton.isChecked()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("Notification", true);
            editor.commit();
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("Notification", false);
            editor.commit();
        }

        //Sets UV threshold level to last selected value
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.uv_index_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(prefs.getInt("UV_threshold_position", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.d("UV_selected", "Selected!");
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("UV_threshold", (String) parent.getItemAtPosition(pos));
                editor.putInt("UV_threshold_position", pos);
                editor.commit();
                Log.d("UV_chosen", String.valueOf(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sets time of notification to last-selected value
        txtTime = (EditText) findViewById(R.id.in_time);
        if (prefs.getInt("Hour", 0) != 0) {
            txtTime.setText(String.format("%02d:%02d", prefs.getInt("Hour", 0), prefs.getInt("Minute", 0)));
        }

        btnTimePicker = (Button) findViewById(R.id.btn_time);
        btnTimePicker.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int mHour, mMinute;

        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            txtTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                            newHour = hourOfDay;
                            newMinute = minute;

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            Log.d("newHour", String.valueOf(newHour));
                            Log.d("newMinute", String.valueOf(newMinute));
                            editor.putInt("Hour", newHour);
                            editor.putInt("Minute", newMinute);
                            editor.commit();

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }
}