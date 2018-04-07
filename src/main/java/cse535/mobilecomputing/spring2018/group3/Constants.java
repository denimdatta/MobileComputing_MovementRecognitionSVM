package cse535.mobilecomputing.spring2018.group3;

import android.os.Environment;

import java.io.File;

/**
 * Constants
 * Common Class for Constant Values
 *
 * @author Group3 CSE535 Spring 2018
 */

public class Constants {
    public static final String filePath = Environment.getExternalStorageDirectory() +
            File.separator + "Android" + File.separator + "data" + File.separator +
            "CSE535_ASSIGNMENT3" + File.separator;
    public static final String DBNAME = "Group3.db";
    public static final String TABLE_NAME = "ACTIVITY_DATA";
    static final String TABLE_COLUMN_VALUE_ID = "ID";
    static final String TABLE_COLUMN_VALUE_X = "XPos_";
    static final String TABLE_COLUMN_VALUE_Y = "YPos_";
    static final String TABLE_COLUMN_VALUE_Z = "ZPos_";
    static final String ACCELEROMETER_ACTION = "ACCELEROMETER";
    public static final String EMPTY_TEXT = "";
}
