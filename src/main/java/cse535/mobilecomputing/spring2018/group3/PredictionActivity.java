package cse535.mobilecomputing.spring2018.group3;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.w3c.dom.Text;

import java.io.IOException;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class PredictionActivity extends AppCompatActivity {

    int index = 0;
    double[] x_data = new double[Constants.PREDICTION_LIMIT];
    double[] y_data = new double[Constants.PREDICTION_LIMIT];
    double[] z_data = new double[Constants.PREDICTION_LIMIT];
    boolean isSensorReceiverRegistered = false;
    final ExtendedBroadcastReceiver2 sdReceiver = new ExtendedBroadcastReceiver2();
    final IntentFilter intentFilterSensor = new IntentFilter(Constants.ACCELEROMETER_ACTION);
    long lastTime = System.currentTimeMillis() - Constants.DELAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        if (!isSensorReceiverRegistered) {
            registerReceiver(sdReceiver, intentFilterSensor);
            startService(new Intent(PredictionActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = true;
        }
    }


    /**
     * Extended Broadcast Receiver Class
     */
    private class ExtendedBroadcastReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACCELEROMETER_ACTION:
                    long curTime = System.currentTimeMillis();
                    if (index < Constants.PREDICTION_LIMIT) {
                        if((curTime - lastTime) >= Constants.DELAY) {
                            x_data[index] = (double)intent.getFloatExtra("valX", 0);
                            y_data[index] = (double)intent.getFloatExtra("valY", 0);
                            z_data[index] = (double)intent.getFloatExtra("valZ", 0);

                            index++;
                            lastTime = curTime;
                        }
                    } else {
                        Mean mu = new Mean();
                        StandardDeviation sd = new StandardDeviation();

                        double feature [] = {
                                mu.evaluate(x_data, 0, x_data.length),
                                sd.evaluate(x_data),
                                mu.evaluate(y_data, 0, y_data.length),
                                sd.evaluate(y_data),
                                mu.evaluate(z_data, 0, z_data.length),
                                sd.evaluate(z_data)
                        };

                        String predictedActivity = null;
                        try {
                            predictedActivity = UtilitySVMPredict.prediction(feature);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        TextView predictionTV = (TextView) findViewById(R.id.predictionTV);

                        if(predictedActivity != null) {
                            String msg = getString(R.string.predictionActivityMsg);

                            SpannableString span1 = new SpannableString(msg);
                            span1.setSpan(new RelativeSizeSpan(1f), 0, msg.length(), SPAN_INCLUSIVE_INCLUSIVE);

                            SpannableString span2 = new SpannableString(predictedActivity);
                            span2.setSpan(new RelativeSizeSpan(1.25f), 0, predictedActivity.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            if(predictedActivity.equalsIgnoreCase(Constants.WALK_VALUE)) {
                                span2.setSpan(new ForegroundColorSpan(Color.GREEN), 0, predictedActivity.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            } else if(predictedActivity.equalsIgnoreCase(Constants.RUN_VALUE)) {
                                span2.setSpan(new ForegroundColorSpan(Color.CYAN), 0, predictedActivity.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            } else if(predictedActivity.equalsIgnoreCase(Constants.JUMP_VALUE)) {
                                span2.setSpan(new ForegroundColorSpan(Color.RED), 0, predictedActivity.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            }

                            CharSequence finalText = TextUtils.concat(span1, " ", span2);
                            predictionTV.setText(finalText);
                        } else {
                            predictionTV.setText(getString(R.string.predictionActivityError));
                        }
                        index = 0;
                        x_data = new double[Constants.PREDICTION_LIMIT];
                        y_data = new double[Constants.PREDICTION_LIMIT];
                        z_data = new double[Constants.PREDICTION_LIMIT];
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
     */
    @Override
    public void onBackPressed() {
        // Unregister the Broadcast receiver for Accelerometer sensor data and Up;pad/Download status,
        // and stop AccelerometerService
        if (isSensorReceiverRegistered) {
            unregisterReceiver(sdReceiver);
            stopService(new Intent(PredictionActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = false;
        }

        index = Constants.LIMIT;
        super.onBackPressed();
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
            stopService(new Intent(PredictionActivity.this, AccelerometerService.class));
            isSensorReceiverRegistered = false;
        }

        index = Constants.LIMIT;
        super.onDestroy();
    }
}
