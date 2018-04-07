package cse535.mobilecomputing.spring2018.group3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grantPermission();

        Button collectDataBtn = (Button) findViewById(R.id.CollectDataBtn);
        collectDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }
                try {
                    File dir = new File(Constants.filePath);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    StringBuffer sqlQuery = new StringBuffer("create table " + Constants.TABLE_NAME +
                            "(" + Constants.TABLE_COLUMN_VALUE_ID + " text");
                    for (int i = 1; i <= 50; i++) {
                        sqlQuery.append(", "+ Constants.TABLE_COLUMN_VALUE_X + "_" + i + " float, " +
                                Constants.TABLE_COLUMN_VALUE_Y + "_" + i + " float, " +
                                Constants.TABLE_COLUMN_VALUE_Z + "_" + i + " float");
                    }
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
                    }
                } catch (SQLException exp) {
                    Toast.makeText(MainActivity.this, exp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button clearDataBtn = (Button) findViewById(R.id.ClearDataBtn);
        clearDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!grantPermission()) {
                    return;
                }
                File dir = new File(Constants.filePath);
                if (!dir.exists()) {
                    Toast.makeText(MainActivity.this, "Data Cleared", Toast.LENGTH_LONG).show();
                } else {
                    File file = new File(Constants.filePath + Constants.DBNAME);
                    if (!file.exists() || file.delete()) {
                        Toast.makeText(MainActivity.this, "Data Cleared", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Couldn't clear Data", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
    }


    /**
     * Override onDestroy
     * on Destroy, receivers will be unregistered and DB will be closed.
     */
    @Override
    public void onDestroy() {
        // Close DB if open
        if (db != null) {
            db.close();
        }
        super.onDestroy();
    }


    /**
     * This function checks if the required
     *
     * @return
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> permissionsList = new ArrayList<>();
        permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                permissionsList.remove(permissions[i]);
        }

        if (!permissionsList.isEmpty()) {
            for (String p : permissionsList) {
                System.out.println("[TEST_P] " + p);
            }
            String msg = "All the Permissions are required for the App to run\n" +
                    "Change the permission from Settings > App > Group3 > Permissions";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exp) {

            }
        }
    }
}
