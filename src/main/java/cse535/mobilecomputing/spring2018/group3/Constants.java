package cse535.mobilecomputing.spring2018.group3;

import android.os.Environment;

import java.io.File;

/**
 * Constants
 * Common Class for Constant Values
 *
 * @author Group3 CSE535 Spring 2018
 */

class Constants {
    static final String filePath = Environment.getExternalStorageDirectory() +
            File.separator + "Android" + File.separator + "data" + File.separator +
            "CSE535_ASSIGNMENT3" + File.separator;
    static final String DBNAME = "Group3.db";
    static final String TABLE_NAME = "ACTIVITY_DATA";
    static final String TABLE_COLUMN_VALUE_ID = "ID";
    static final String TABLE_COLUMN_VALUE_X = "ACCEL_X_";
    static final String TABLE_COLUMN_VALUE_Y = "ACCEL_Y_";
    static final String TABLE_COLUMN_VALUE_Z = "ACCEL_Z_";
    static final String TABLE_COLUMN_LABEL= "ACTIVITY_LABEL";
    static final String ACCELEROMETER_ACTION = "ACCELEROMETER";
    static final int LIMIT = 50;
    static final int DELAY = 100;
    static final int REPEAT = 20;
    static final int FEATURES = 6;
    static final String MODELFILE = "SVM_model.txt";
}
