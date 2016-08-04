package uvindexforecast.theoneandonly.com.uvindexforecast;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Settings extends AppCompatActivity implements
        View.OnClickListener{
    private int mHour, mMinute;
    private SharedPreferences prefs;
    Button btnTimePicker;
    EditText txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        btnTimePicker=(Button)findViewById(R.id.btn_time);
        txtTime=(EditText)findViewById(R.id.in_time);
        btnTimePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

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

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Hour", mHour);
            editor.putInt("Minute", mMinute);
            editor.commit();

        }
    }

    public class SwitchActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

        Switch switchButton = null;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            // For switch button
            switchButton = (Switch) findViewById(R.id.switch1);

            switchButton.setChecked(true);
            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                    if (bChecked) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Notification", "True");
                        editor.commit();
                    } else {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("Notification", "False");
                        editor.commit();
                    }
                }
            });
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // do something when check is selected
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Notification", "True");
                editor.commit();
            } else {
                //do something when unchecked
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Notification", "False");
                editor.commit();
            }
        }

    }

    public class SpinnerActivity extends Settings implements AdapterView.OnItemSelectedListener {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.uv_index_options, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Settings.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("UV_threshold", (String)parent.getItemAtPosition(pos));
            editor.commit();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

    }
}