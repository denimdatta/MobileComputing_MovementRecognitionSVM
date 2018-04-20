package cse535.mobilecomputing.spring2018.group3;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DataCollectActivity
 *
 * @author Group3 CSE535 Spring 2018
 */
public class DataCollectActivity extends AppCompatActivity {

    static SQLiteDatabase db = null;
    static long runData = 0, walkData = 0, jumpData = 0;
    Button runBtn, walkBtn, jumpBtn;
    DateFormat dateForm = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect);

        Utility.createDB();

        final Intent intent = new Intent(DataCollectActivity.this, DataCollectStatusActivity.class);

        // Run: Set intent activity as "Run" and call DataCollectStatusActivity
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

        // Walk: Set intent activity as "Walk" and call DataCollectStatusActivity
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

        // Jump: Set intent activity as "Jump" and call DataCollectStatusActivity
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

    /**
     * Get the count of collected data and shoe with the button
     */
    @Override
    public void onResume(){
        if(db == null) {
            Utility.createDB();
            db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        }
        runData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{Constants.RUN_VALUE});
        walkData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{Constants.WALK_VALUE});
        jumpData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{Constants.JUMP_VALUE});
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
