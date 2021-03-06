package cse535.mobilecomputing.spring2018.group3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Group3 CSE535 Spring 2018
 */
public class DataCollectStatusActivity extends Activity {

    static SQLiteDatabase db = null;
    int count;
    float curX, curY, curZ;
    boolean isSensorReceiverRegistered = false;
    final ExtendedBroadcastReceiver sdReceiver = new ExtendedBroadcastReceiver();
    final IntentFilter intentFilterSensor = new IntentFilter(Constants.ACCELEROMETER_ACTION);
    String activity = null, id = null;
    TextView collectStatusTV = null;
    long lastTime = System.currentTimeMillis() - Constants.DELAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect_status);

        // Get activity type and ID
        count = 1;
        activity = getIntent().getExtras().getString("activity");
        id = getIntent().getExtras().getString("id");
        collectStatusTV = (TextView) findViewById(R.id.CollectionStatusTV);

        // insert row with ID and activity type.
        // data will be set by BroadcastReceiver
        db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues insertValues = new ContentValues();
        insertValues.put(Constants.TABLE_COLUMN_VALUE_ID, id);
        insertValues.put(Constants.TABLE_COLUMN_LABEL, activity);
        db.insert(Constants.TABLE_NAME, null, insertValues);
        collectStatusTV.setText(getString(R.string.collectionProgress, activity));

        // Register with BroadcastReceiver
        if (!isSensorReceiverRegistered) {
            registerReceiver(sdReceiver, intentFilterSensor);
            startService(new Intent(DataCollectStatusActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = true;
        }
    }

    /**
     * Extended Broadcast Receiver Class
     * @author Group3 CSE535 Spring 2018
     */
    private class ExtendedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACCELEROMETER_ACTION:
                    long curTime = System.currentTimeMillis();
                    // enforcing delay of 0.1 sec between consecutive sensor data
                    if (count <= Constants.LIMIT) {
                        if((curTime - lastTime) >= Constants.DELAY) {
                            curX = intent.getFloatExtra("valX", 0);
                            curY = intent.getFloatExtra("valY", 0);
                            curZ = intent.getFloatExtra("valZ", 0);

                            // Update the row with collected data
                            ContentValues updateValues = new ContentValues();
                            updateValues.put(Constants.TABLE_COLUMN_VALUE_X + count, curX);
                            updateValues.put(Constants.TABLE_COLUMN_VALUE_Y + count, curY);
                            updateValues.put(Constants.TABLE_COLUMN_VALUE_Z + count, curZ);
                            db.update(Constants.TABLE_NAME, updateValues, Constants.TABLE_COLUMN_VALUE_ID + " = ?", new String[]{id});

                            count++;
                            lastTime = curTime;
                        }
                    } else {
                        collectStatusTV.setText(getString(R.string.collectionComplete, activity));
                        // Unregister BroadcastReceiver and close DB
                        if (isSensorReceiverRegistered) {
                            unregisterReceiver(sdReceiver);
                            stopService(new Intent(DataCollectStatusActivity.this, AccelerometerService.class));
                            isSensorReceiverRegistered = false;
                        }
                        if (db != null) {
                            db.close();
                            db = null;
                        }
                        // Finish the activity
                        finish();
                    }
                    break;

                default:
                    // Do Nothing
                    break;
            }
        }
    }

    /**
     * Override OnBackPressed
     * disable backpressed, so that we can't go back in between data collection
     */
    @Override
    public void onBackPressed() {
        // Do Nothing
    }

    /**
     * Override onDestroy
     * on Destroy, receivers will be unregistered and DB will be closed.
     */
    @Override
    public void onDestroy() {
        // Unregister the Broadcast receiver for Accelerometer sensor data and Up;pad/Download status,
        // and stop AccelerometerService
        if (isSensorReceiverRegistered) {
            unregisterReceiver(sdReceiver);
            stopService(new Intent(DataCollectStatusActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = false;
        }

        // Close DB idf open
        if (db != null) {
            db.close();
            db = null;
        }
        count = Constants.LIMIT;
        super.onDestroy();
    }
}
