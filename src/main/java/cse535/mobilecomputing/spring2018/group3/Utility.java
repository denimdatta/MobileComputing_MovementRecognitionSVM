package cse535.mobilecomputing.spring2018.group3;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ddat0 on 07-04-2018.
 */

public class Utility {

    public static boolean createDB() {
        try {
            File dir = new File(Constants.filePath);
            if (!dir.exists()) {
                dir.mkdir();
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
                db = null;
            }
        } catch (SQLException exp) {
            return false;
        }

        return true;
    }


    public static void convertDatoToSVM() {
        // TODO DB entries to
        File file = new File(Constants.filePath+Constants.DBNAME+".txt");
        StringBuilder content = new StringBuilder();
        Utility.createDB();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                float x = 0, y = 0, z = 0;
                int label = 0;
                int columnLength = c.getColumnCount();
                for (int i = 0; i<Constants.LIMIT; i++) {
                    int k = 3*i + 1;
                    x += c.getFloat(k);
                    y += c.getFloat(k+1);
                    z += c.getFloat(k+2);
                }
                x = x/Constants.LIMIT;
                y = y/Constants.LIMIT;
                z = z/Constants.LIMIT;

                String act = c.getString(3*Constants.LIMIT + 1);
                if (act.equalsIgnoreCase("RUN")) {
                    label = 1;
                } else if (act.equalsIgnoreCase("WALK")) {
                    label = 2;
                } else if (act.equalsIgnoreCase("JUMP")) {
                    label = 3;
                }

                if (label!=0) {
                    content.append(label);
                    content.append(" 1:").append(x);
                    content.append(" 2:").append(y);
                    content.append(" 3:").append(z);
                    content.append("\n");
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.toString().getBytes());
            fos.close();
        } catch (IOException ex) {
            // Exception
        }
    }
}