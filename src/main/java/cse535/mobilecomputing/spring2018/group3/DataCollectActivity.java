package cse535.mobilecomputing.spring2018.group3;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class DataCollectActivity extends AppCompatActivity {

    static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect);

        try {
            File dir = new File(Constants.filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            StringBuilder sqlQuery = new StringBuilder("create table " + Constants.TABLE_NAME +
                    "(" + Constants.TABLE_COLUMN_VALUE_ID + " text");
            for (int i = 1; i <= 50; i++) {
                sqlQuery.append(", " );
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

            db = SQLiteDatabase.openOrCreateDatabase(Constants.filePath + Constants.DBNAME, null);
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
            Toast.makeText(DataCollectActivity.this, exp.getMessage(), Toast.LENGTH_LONG).show();
        }

        Button runBtn = (Button) findViewById(R.id.RunningBtn);
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button walkBtn = (Button) findViewById(R.id.WalkingBtn);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button jumpBtn = (Button) findViewById(R.id.JumpingBtn);
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Override OnBackPressed
     * on BackPressed, the graph plot will be stopped, and receivers will be unregistered.
     */
    @Override
    public void onBackPressed() {
        // Close DB if open.
        if (db != null) {
            db.close();
        }
        super.onBackPressed();
    }

    /**
     * Override onDestroy
     * on Destroy, receivers will be unregistered and DB will be closed.
     */
    @Override
    public void onDestroy() {
        // Close DB idf open
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }
}
