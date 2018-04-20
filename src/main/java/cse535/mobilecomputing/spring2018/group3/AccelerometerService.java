package cse535.mobilecomputing.spring2018.group3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

/**
 * AccelerometerService
 * Connects to Accelerometer Sensor, and provide data in 10Hz frequency
 *
 * @author Group3 CSE535 Spring 2018
 */
public class AccelerometerService extends Service implements SensorEventListener {
    float X, Y, Z;
    long lastTime = System.currentTimeMillis() - Constants.DELAY;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        long curTime = System.currentTimeMillis();

        // enforcing delay of 0.1 Sec between consecutive sensor data
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER &&
                (curTime - lastTime) >= Constants.DELAY) {
            lastTime = curTime;
            X = event.values[0];
            Y = event.values[1];
            Z = event.values[2];

            Intent intent = new Intent();
            intent.setAction(Constants.ACCELEROMETER_ACTION);
            intent.putExtra("valX", X);
            intent.putExtra("valY", Y);
            intent.putExtra("valZ", Z);

            sendBroadcast(intent);
        }
    }

    @Override
    public void onCreate() {
        // Register Sensor with SensorServices
        SensorManager accelerometerManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor senseAccelerometer = accelerometerManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerManager.registerListener(this, senseAccelerometer, Constants.DELAY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do Nothing
    }

    public AccelerometerService() {
    }
}
