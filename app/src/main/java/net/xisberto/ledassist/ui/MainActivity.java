package net.xisberto.ledassist.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import net.xisberto.ledassist.R;
import net.xisberto.ledassist.control.Scheduler;
import net.xisberto.ledassist.control.Settings;


public class MainActivity extends FragmentActivity implements View.OnClickListener,
        RadialTimePickerDialog.OnTimeSetListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY = "key", TARGET = "target", TAG_RADIAL_PICKER = "radial_picker";

    private CheckBox checkLed;
    private String mPreferencesKey;
    private int mTargetButton;
    private AboutFragment aboutFragment;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Settings.ACTION_LED_ENABLED.equals(intent.getAction())
                    && intent.hasExtra(Settings.EXTRA_LED_STATUS))
            updateLayout(intent.getBooleanExtra(Settings.EXTRA_LED_STATUS, true));
        }
    };

    private void updateLayout(boolean led_enabled) {
        TextView textStatus = (TextView) findViewById(R.id.textStatus);
        String str_status = getString(led_enabled ? R.string.enabled : R.string.disabled);
        textStatus.setText(getString(R.string.current_led_status, str_status));
        textStatus.setCompoundDrawablesWithIntrinsicBounds(
                led_enabled ? R.drawable.ic_led_on : R.drawable.ic_led_off,
                0, 0, 0);
    }

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
        checkLed.setChecked(Settings.isActive(this));
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
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,
                                R.anim.slide_in_up, R.anim.slide_out_down)
                        .replace(R.id.frame_feedback, aboutFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                .unregisterOnSharedPreferenceChangeListener(this);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RadialTimePickerDialog dialog = (RadialTimePickerDialog) getSupportFragmentManager()
                .findFragmentByTag(TAG_RADIAL_PICKER);
        if (dialog != null) {
            dialog.setOnTimeSetListener(this);
        }

        aboutFragment = (AboutFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frame_feedback);

        updateLayout(Settings.isLedEnabled(this));

        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(Settings.ACTION_LED_ENABLED));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY, mPreferencesKey);
        outState.putInt(TARGET, mTargetButton);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getSupportFragmentManager()
                    .popBackStack();
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
        if (key.startsWith(Settings.KEY_START)
                || key.startsWith(Settings.KEY_END)) {
            if (checkLed.isChecked()) {
                Settings.setActive(this, true);
            }
        }
    }
}
