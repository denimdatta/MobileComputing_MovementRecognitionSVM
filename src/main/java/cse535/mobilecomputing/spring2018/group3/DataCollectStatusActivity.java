package cse535.mobilecomputing.spring2018.group3;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataCollectStatusActivity extends AppCompatActivity {
    int count;
    float curX, curY, curZ;
    boolean isSensorReceiverRegistered = false;
    final ExtendedBroadcastReceiver sdReceiver = new ExtendedBroadcastReceiver();
    final IntentFilter intentFilterSensor = new IntentFilter(Constants.ACCELEROMETER_ACTION);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect_status);

        count = 1;
        String activity = getIntent().getExtras().getString("activity");
        String id = getIntent().getExtras().getString("id");
        TextView collectStatusTV = (TextView) findViewById(R.id.CollectionStatusTV);

        if (!isSensorReceiverRegistered) {
            registerReceiver(sdReceiver, intentFilterSensor);
            startService(new Intent(DataCollectStatusActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = true;
        }

        Thread waitThread = new Thread() {
            public void run() {
                while (count <= Constants.LIMIT) {
                    try {
                        // keeping a sleep time to make the graph advanced in decent speed.
                        Thread.sleep(1000);
                    } catch (InterruptedException excp) {
                        // Do Nothing
                    }
                }

                if (isSensorReceiverRegistered) {
                    unregisterReceiver(sdReceiver);
                    stopService(new Intent(DataCollectStatusActivity.this, AccelerometerService.class));
                    isSensorReceiverRegistered = false;
                }
            }
        };
    }

    /**
     * Extended Broadcast Receiver Class
     */
    private class ExtendedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACCELEROMETER_ACTION:
                    if (count <= Constants.LIMIT) {
                        curX = intent.getFloatExtra("valX", 0);
                        curY = intent.getFloatExtra("valY", 0);
                        curZ = intent.getFloatExtra("valZ", 0);

                        System.out.println("TEST: (" + count + ") " + curX + "-" + curY + "-" + curZ);

                        count++;
                    }
                    break;

                default:
                    // Do Nothing
                    break;
            }
        }
    }
}
