package net.xisberto.ledassist;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;


public class MainActivity extends FragmentActivity implements View.OnClickListener, RadialTimePickerDialog.OnTimeSetListener {
    private static final String KEY = "key", TARGET = "target", TAG_RADIAL_PICKER = "radial_picker";

    private CheckBox checkLed;
    private String mPreferencesKey;
    private int mTargetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY)
                    && savedInstanceState.containsKey(TARGET)) {
                mPreferencesKey = savedInstanceState.getString(KEY);
                mTargetButton = savedInstanceState.getInt(TARGET);
            }
        }

        checkLed = (CheckBox) findViewById(R.id.checkLed);
        findViewById(R.id.textSummary).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkLed.onTouchEvent(event);
                return true;
            }
        });

        Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);
        buttonStart.setText(Settings.getTimeString(this, Settings.KEY_START));

        Button buttonEnd = (Button) findViewById(R.id.buttonEnd);
        buttonEnd.setOnClickListener(this);
        buttonEnd.setText(Settings.getTimeString(this, Settings.KEY_END));
    }

    @Override
    protected void onResume() {
        super.onResume();

        RadialTimePickerDialog dialog = (RadialTimePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(TAG_RADIAL_PICKER);
        if (dialog != null) {
            dialog.setOnTimeSetListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY, mPreferencesKey);
        outState.putInt(TARGET, mTargetButton);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        mTargetButton = v.getId();
        if (v.getId() == R.id.buttonStart) {
            mPreferencesKey = Settings.KEY_START;
        } else {
            mPreferencesKey = Settings.KEY_END;
        }

        int hour = Settings.getHour(this, mPreferencesKey);
        int minute = Settings.getMinute(this, mPreferencesKey);

        RadialTimePickerDialog dialog = RadialTimePickerDialog.newInstance(this, hour, minute,
                DateFormat.is24HourFormat(this));
        dialog.show(getSupportFragmentManager(), TAG_RADIAL_PICKER);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        Button button = (Button) findViewById(mTargetButton);
        Settings.setTime(this, mPreferencesKey, hourOfDay, minute);
        button.setText(Settings.getTimeString(this, hourOfDay, minute));
    }
}
