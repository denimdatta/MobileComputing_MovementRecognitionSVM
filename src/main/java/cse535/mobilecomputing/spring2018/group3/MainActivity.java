package cse535.mobilecomputing.spring2018.group3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Group3 CSE535 Spring 2018
 */
public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase db = null;
    static long runData = 0, walkData = 0, jumpData = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for permission
        if (grantPermission()) {
            checkDB();
        }

        // Clears Data
        Button clearDataBtn = (Button) findViewById(R.id.ClearDataBtn);
        clearDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for permission
                if (!grantPermission()) {
                    return;
                }

                File dir = new File(Constants.filePath);
                if (!dir.exists()) {
                    Toast.makeText(MainActivity.this, "Data Cleared", Toast.LENGTH_LONG).show();
                } else {
                    File file = new File(Constants.filePath + Constants.DBNAME);
                    if (!file.exists()) {
                        Toast.makeText(MainActivity.this, "Data Cleared " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (file.delete()) {
                        if (db != null) {
                            db.close();
                            db = null;
                        }
                        Toast.makeText(MainActivity.this, "Data Cleared " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Couldn't clear Data", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });

        // Collect Data
        Button collectDataBtn = (Button) findViewById(R.id.CollectDataBtn);
        collectDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, DataCollectActivity.class);
                startActivity(intent);
            }
        });

        // SVM Classification
        Button classifyBtn = (Button) findViewById(R.id.ClassifyBtn);
        classifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }

                if (db == null) {
                    Utility.createDB();
                    db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
                }

                // Check if sufficient data collected. If not display message, and does not
                // proceed to SVM training
                runData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                        Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.run)});
                walkData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                        Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.walk)});
                jumpData = DatabaseUtils.queryNumEntries(db, Constants.TABLE_NAME,
                        Constants.TABLE_COLUMN_LABEL + " = ?", new String[]{getString(R.string.jump)});
                if (db != null) {
                    db.close();
                    db = null;
                }

                if (runData < Constants.REPEAT || walkData < Constants.REPEAT || jumpData < Constants.REPEAT) {
                    Toast.makeText(MainActivity.this, "Insufficient Data. Please collect 20 Instances for each Activity Type", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SvmParametersActivity.class);
                startActivity(intent);
            }
        });

        // Realtime Data Prediction
        Button predictBtn = (Button) findViewById(R.id.PredictBtn);
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }

                // Check if SVM model is trained. If not display message, and does not
                // proceed to realtime prediction
                File model = new File(Constants.filePath+Constants.MODELFILE);
                if(!model.exists()){
                    Toast.makeText(MainActivity.this, "Please Train on collected data first to get the Model", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, PredictionActivity.class);
                startActivity(intent);
            }
        });

        // Plot Graph
        Button plotBtn = (Button) findViewById(R.id.GraphPlotBtn);
        plotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }

                Intent intent = new Intent(MainActivity.this, GraphplotActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * If any row exists with incomplete data, remove that row entry
     */
    private void checkDB() {
        File dir = new File(Constants.filePath);
        if (dir.exists()) {
            File file = new File(Constants.filePath + Constants.DBNAME);
            if (file.exists()) {
                db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READWRITE);
                db.delete(Constants.TABLE_NAME, Constants.TABLE_COLUMN_VALUE_Z + Constants.LIMIT + " IS NULL", null);
                db.close();
                db = null;
            }
        }
    }


    /**
     * Override onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Override onBackPressed
     */
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * This function checks if the required permission is granted
     *
     * @return True if granted, False otherwise
     */
    private boolean grantPermission() {
        List<String> permReqList = new ArrayList<>();
        String[] permissionList = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            for (String perm : permissionList) {
                if (checkSelfPermission(perm)
                        != PackageManager.PERMISSION_GRANTED) {
                    permReqList.add(perm);
                }
            }
        }

        if (permReqList.isEmpty()) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, permReqList.toArray(new String[permReqList.size()]), 1);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> permissionsList = new ArrayList<>();
        permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                permissionsList.remove(permissions[i]);
        }

        if (!permissionsList.isEmpty()) {
            String msg = "All the Permissions are required for the App to run\n" +
                    "Change the permission from Settings > App > Group3 > Permissions";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exp) {
                // Do Nothing
            }
        } else {
            checkDB();
        }
    }
}
