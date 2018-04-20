package cse535.mobilecomputing.spring2018.group3;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.util.HashMap;

/**
 * Utility
 *
 * @author Group3 CSE535 Spring 2018
 */

class Utility {

    /**
     * Create the DB if doesn't exist
     */
    static void createDB() {
        try {
            // Create directory if not present
            File dir = new File(Constants.filePath);
            if (!dir.exists()) {
                if(!dir.mkdir()) {
                    return;
                }
            }

            // Create DB and table if not present already
            StringBuilder sqlQuery = new StringBuilder("create table " + Constants.TABLE_NAME +
                    "(" + Constants.TABLE_COLUMN_VALUE_ID + " text");
            for (int i = 1; i <= 50; i++) {
                sqlQuery.append(", ");
                sqlQuery.append(Constants.TABLE_COLUMN_VALUE_X);
                sqlQuery.append(i);
                sqlQuery.append(" float, ");
                sqlQuery.append(Constants.TABLE_COLUMN_VALUE_Y);
                sqlQuery.append(i);
                sqlQuery.append(" float, ");
                sqlQuery.append(Constants.TABLE_COLUMN_VALUE_Z);
                sqlQuery.append(i);
                sqlQuery.append(" float");
            }
            sqlQuery.append(", ");
            sqlQuery.append(Constants.TABLE_COLUMN_LABEL);
            sqlQuery.append(" text");
            sqlQuery.append(");");

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(Constants.filePath + Constants.DBNAME, null);
            db.beginTransaction();
            try {
                db.execSQL(sqlQuery.toString());
                db.setTransactionSuccessful();

            } catch (SQLiteException exp) {
                // Exception
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (SQLException exp) {
            // Exception
        }
    }


    /**
     * Create a HashMap with acitvity data from database
     * @return HashMap with activity data with axis name
     */
    static HashMap<String, double[][]> fromDatabaseGetActivityValues() {
        HashMap<String, double[][]> result = new HashMap<>();
        createDB();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        String[] activities = {
                Constants.WALK_VALUE,
                Constants.RUN_VALUE,
                Constants.JUMP_VALUE
        };

        // For each type of activity, get the data from DB and convert to HashMap
        for (String activity : activities) {
            Cursor c = db.query(Constants.TABLE_NAME, null, Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{activity}, null,null,null);
            int size = c.getCount();
            double[][] X = new double[size][Constants.LIMIT];
            double[][] Y = new double[size][Constants.LIMIT];
            double[][] Z = new double[size][Constants.LIMIT];
            int index = 0;
            if (c.moveToFirst()) {
                do {
                    double[] x_f = new double[Constants.LIMIT];
                    double[] y_f = new double[Constants.LIMIT];
                    double[] z_f = new double[Constants.LIMIT];
                    for (int i = 0; i < Constants.LIMIT; i++) {
                        int k = 3 * i + 1;
                        x_f[i] = (double) c.getFloat(k);
                        y_f[i] = (double) c.getFloat(k + 1);
                        z_f[i] = (double) c.getFloat(k + 2);
                    }
                    X[index] = x_f;
                    Y[index] = y_f;
                    Z[index] = z_f;
                    index++;
                } while (c.moveToNext());
            }
            result.put(activity.toUpperCase()+"_X", X);
            result.put(activity.toUpperCase()+"_Y", Y);
            result.put(activity.toUpperCase()+"_Z", Z);
            c.close();
        }
        db.close();
        return result;
    }
}
