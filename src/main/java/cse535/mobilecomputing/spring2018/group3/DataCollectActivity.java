package cse535.mobilecomputing.spring2018.group3;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataCollectActivity extends AppCompatActivity {

    static SQLiteDatabase db = null;
    static long runData = 0, walkData = 0, jumpData = 0;
    Button runBtn, walkBtn, jumpBtn;
    DateFormat dateForm = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");

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
                db = null;
            }
        } catch (SQLException exp) {
            Toast.makeText(DataCollectActivity.this, exp.getMessage(), Toast.LENGTH_LONG).show();
        }

        final Intent intent = new Intent(DataCollectActivity.this, DataCollectStatusActivity.class);

        runBtn = (Button) findViewById(R.id.RunningBtn);
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = dateForm.format(new Date());
                intent.putExtra("id", timeStamp);
                intent.putExtra("activity", getString(R.string.run));
                startActivity(intent);
            }
        });

        walkBtn = (Button) findViewById(R.id.WalkingBtn);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = dateForm.format(new Date());
                intent.putExtra("id", timeStamp);
                intent.putExtra("activity", getString(R.string.walk));
                startActivity(intent);
            }
        });

        jumpBtn = (Button) findViewById(R.id.JumpingBtn);
        jumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = dateForm.format(new Date());
                intent.putExtra("id", timeStamp);
                intent.putExtra("activity", getString(R.string.jump));
                startActivity(intent);
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
            db = null;
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
            db = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume(){
        if(db == null) {
            db = SQLiteDatabase.openOrCreateDatabase(Constants.filePath + Constants.DBNAME, null);
        }
        runData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.run)});
        walkData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.walk)});
        jumpData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.jump)});
        System.out.println("[TEST]: " + runData + "::" + walkData + "::" + jumpData);
        if (db != null){
            db.close();
            db = null;
        }

        runBtn.setText(getString(R.string.runBtn, runData, Constants.REPEAT));
        walkBtn.setText(getString(R.string.walkBtn, walkData, Constants.REPEAT));
        jumpBtn.setText(getString(R.string.jumpBtn, jumpData, Constants.REPEAT));
        super.onResume();
    }
}
