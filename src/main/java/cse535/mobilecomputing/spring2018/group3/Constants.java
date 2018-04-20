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
    static final int PREDICTION_LIMIT = 20;
    static final int DELAY = 100;
    static final int REPEAT = 20;
    static final int TESTCOUNT = REPEAT/3; // 33% Test Data, and 67% Train Data
    static final int FEATURES = 6;
    static final String MODELFILE = "SVM_model.txt";
    static final int RUN_LABEL = 1;
    static final String RUN_VALUE = "Run";
    static final int WALK_LABEL = 2;
    static final String WALK_VALUE = "Walk";
    static final int JUMP_LABEL = 3;
    static final String JUMP_VALUE = "Jump";
}
