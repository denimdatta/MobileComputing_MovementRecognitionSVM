package cse535.mobilecomputing.spring2018.group3;

import android.os.Environment;

import java.io.File;

/**
 * Constants
 * Common Class for Constant Values
 * @author Group3 CSE535 Spring 2018
 */

public class Constants {
    public static final String filePathCommon = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data";
    public static final String filePath = filePathCommon + File.separator + "CSE535_ASSIGNMENT3" + File.separator;
    public static final String DBNAME = "Group3.db";
    public static final String EMPTY_TEXT = "";
}
