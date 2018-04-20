package cse535.mobilecomputing.spring2018.group3;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 */

class Utility {

    static void createDB() {
        try {
            File dir = new File(Constants.filePath);
            if (!dir.exists()) {
                if(!dir.mkdir()) {
                    return;
                }
            }

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


    static HashMap<String, double[][]> fromDatabaseGetActivityValues() {
        HashMap<String, double[][]> result = new HashMap<>();
        createDB();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        String[] activities = {
                Constants.WALK_VALUE,
                Constants.RUN_VALUE,
                Constants.JUMP_VALUE
        };
        for (String activity : activities) {
            Cursor c = db.query(Constants.TABLE_NAME, null, Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{activity}, null,null,null);
            double[][] X = new double[Constants.REPEAT][Constants.LIMIT];
            double[][] Y = new double[Constants.REPEAT][Constants.LIMIT];
            double[][] Z = new double[Constants.REPEAT][Constants.LIMIT];
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

    /*
    static void convertDataToSVM() {
        File file = new File(Constants.filePath + Constants.DBNAME + ".txt");
        StringBuilder content = new StringBuilder();
        Utility.createDB();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
//                float x = 0, y = 0, z = 0;
                int label = 0;
//                int columnLength = c.getColumnCount();
                double[] x_f = new double[Constants.LIMIT];
                double[] y_f = new double[Constants.LIMIT];
                double[] z_f = new double[Constants.LIMIT];
                for (int i = 0; i < Constants.LIMIT; i++) {
                    int k = 3 * i + 1;
//                    x += c.getFloat(k);
//                    y += c.getFloat(k+1);
//                    z += c.getFloat(k+2);
                    x_f[i] = (double) c.getFloat(k);
                    y_f[i] = (double) c.getFloat(k + 1);
                    z_f[i] = (double) c.getFloat(k + 2);
                }
//                x = x/Constants.LIMIT;
//                y = y/Constants.LIMIT;
//                z = z/Constants.LIMIT;

                Mean mu = new Mean();
                StandardDeviation sd = new StandardDeviation();

                String act = c.getString(3 * Constants.LIMIT + 1);
                if (act.equalsIgnoreCase("RUN")) {
                    label = 1;
                } else if (act.equalsIgnoreCase("WALK")) {
                    label = 2;
                } else if (act.equalsIgnoreCase("JUMP")) {
                    label = 3;
                }

                if (label != 0) {
                    content.append(label);
//                    content.append(" 1:").append(x);
//                    content.append(" 2:").append(y);
//                    content.append(" 3:").append(z);
                    content.append(" 1:").append(mu.evaluate(x_f, 0, x_f.length));
                    content.append(" 2:").append(mu.evaluate(y_f, 0, y_f.length));
                    content.append(" 3:").append(mu.evaluate(z_f, 0, z_f.length));
                    content.append(" 4:").append(sd.evaluate(x_f));
                    content.append(" 5:").append(sd.evaluate(y_f));
                    content.append(" 6:").append(sd.evaluate(z_f));
                    content.append("\n");
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.toString().getBytes());
            fos.close();
        } catch (IOException ex) {
            // Exception
        }
    }
    */
}
