package net.xisberto.ledassist.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import net.xisberto.ledassist.R;
import net.xisberto.ledassist.control.Scheduler;
import net.xisberto.ledassist.control.Settings;


public class MainActivity extends FragmentActivity implements View.OnClickListener,
        RadialTimePickerDialog.OnTimeSetListener, SharedPreferences.OnSharedPreferenceChangeListener, FragmentManager.OnBackStackChangedListener {

    private static final String KEY = "key", TARGET = "target", TAG_RADIAL_PICKER = "radial_picker";

    private CheckBox checkLed;
    private String mPreferencesKey;
    private int mTargetButton;
    private FeedbackFragment feedbackFragment;

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
        checkLed.setOnClickListener(this);
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

        findViewById(R.id.buttonFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedbackFragment == null) {
                    feedbackFragment = new FeedbackFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,
                                R.anim.slide_in_up, R.anim.slide_out_down)
                        .replace(R.id.frame_feedback, feedbackFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO restore actionbar status

        RadialTimePickerDialog dialog = (RadialTimePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(TAG_RADIAL_PICKER);
        if (dialog != null) {
            dialog.setOnTimeSetListener(this);
        }

        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);

        checkLed.setChecked(Settings.isActive(this));

        TextView textStatus = (TextView) findViewById(R.id.textStatus);
        boolean status = Settings.isLedEnabled(this);
        String str_status = getString(status ? R.string.enabled : R.string.disabled);
        textStatus.setText(getString(R.string.current_led_status, str_status));
        textStatus.setCompoundDrawablesWithIntrinsicBounds(
                status ? R.drawable.ic_led_on : R.drawable.ic_led_off,
                0, 0, 0);
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
        if (id == android.R.id.home) {
            getSupportFragmentManager()
                    .popBackStack();
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkLed) {
            Log.d("MainActivity", String.format("checkLed is %b", checkLed.isChecked()));
            Settings.setActive(this, checkLed.isChecked());
        } else {
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
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        Button button = (Button) findViewById(mTargetButton);
        Settings.setTime(this, mPreferencesKey, hourOfDay, minute);
        button.setText(Settings.getTimeString(this, hourOfDay, minute));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.KEY_START) || key.equals(Settings.KEY_END)) {
            if (Settings.isActive(this)) {
                Scheduler.startSchedule(this);
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        if (feedbackFragment != null
                && feedbackFragment.isVisible()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setHomeButtonEnabled(false);
            feedbackFragment = null;
        }
    }
}
