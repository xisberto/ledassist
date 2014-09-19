package net.xisberto.ledassist;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import java.util.Set;


public class MainActivity extends Activity implements View.OnClickListener {

    private CheckBox checkLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        final String key;
        final Button target = (Button) v;
        if (v.getId() == R.id.buttonStart) {
            key = Settings.KEY_START;
        } else {
            key = Settings.KEY_END;
        }
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Settings.setTime(MainActivity.this, key, hourOfDay, minute);
                target.setText(Settings.getTimeString(MainActivity.this, hourOfDay, minute));
            }
        };
        int hour = Settings.getHour(this, key);
        int minute = Settings.getMinute(this, key);
        TimePickerDialog dialog = new TimePickerDialog(this, listener, hour, minute,
                DateFormat.is24HourFormat(this));
        dialog.setCancelable(true);
        dialog.show();
    }
}
