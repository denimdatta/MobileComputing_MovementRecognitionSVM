package cse535.mobilecomputing.spring2018.group3;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashMap;

/**
 * @author Group3 CSE535 Spring 2018
 */
public class GraphplotActivity extends AppCompatActivity {
    WebView plotArea;
    double[][] walkX, walkY, walkZ, runX, runY, runZ, jumpX, jumpY, jumpZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphplot);

        // Get activity data from database
        final HashMap<String, double[][]> values = Utility.fromDatabaseGetActivityValues();

        // by default, all three type of check box is considered checked
        walkX = values.get(Constants.WALK_VALUE.toUpperCase() + "_X");
        walkY = values.get(Constants.WALK_VALUE.toUpperCase() + "_Y");
        walkZ = values.get(Constants.WALK_VALUE.toUpperCase() + "_Z");
        runX = values.get(Constants.RUN_VALUE.toUpperCase() + "_X");
        runY = values.get(Constants.RUN_VALUE.toUpperCase() + "_Y");
        runZ = values.get(Constants.RUN_VALUE.toUpperCase() + "_Z");
        jumpX = values.get(Constants.JUMP_VALUE.toUpperCase() + "_X");
        jumpY = values.get(Constants.JUMP_VALUE.toUpperCase() + "_Y");
        jumpZ = values.get(Constants.JUMP_VALUE.toUpperCase() + "_Z");

        plotGraph();

        // Walk CheckBox: if checked, set the data from selected data, else set null
        // no modification for Run and Jump data, and plot graph
        CheckBox walkChk = (CheckBox) findViewById(R.id.walkChk);
        walkChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    walkX = values.get(Constants.WALK_VALUE.toUpperCase() + "_X");
                    walkY = values.get(Constants.WALK_VALUE.toUpperCase() + "_Y");
                    walkZ = values.get(Constants.WALK_VALUE.toUpperCase() + "_Z");
                } else {
                    walkX = null;
                    walkY = null;
                    walkZ = null;
                }
                plotGraph();
            }
        });

        // Run CheckBox: if checked, set the data from selected data, else set null
        // no modification for Walk and Jump data, and plot graph
        CheckBox runChk = (CheckBox) findViewById(R.id.runChk);
        runChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    runX = values.get(Constants.RUN_VALUE.toUpperCase() + "_X");
                    runY = values.get(Constants.RUN_VALUE.toUpperCase() + "_Y");
                    runZ = values.get(Constants.RUN_VALUE.toUpperCase() + "_Z");
                } else {
                    runX = null;
                    runY = null;
                    runZ = null;
                }
                plotGraph();
            }
        });

        // Jump CheckBox: if checked, set the data from selected data, else set null
        // no modification for Run and Walk data, and plot graph
        CheckBox jumpChk = (CheckBox) findViewById(R.id.jumpChk);
        jumpChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    jumpX = values.get(Constants.JUMP_VALUE.toUpperCase() + "_X");
                    jumpY = values.get(Constants.JUMP_VALUE.toUpperCase() + "_Y");
                    jumpZ = values.get(Constants.JUMP_VALUE.toUpperCase() + "_Z");
                } else {
                    jumpX = null;
                    jumpY = null;
                    jumpZ = null;
                }
                plotGraph();
            }
        });
    }

    /**
     * Plots graph using Javascript
     */
    private void plotGraph() {
        // Get the webView and load html.
        // The html calls JavaScript function which plots the graph using plotly.js library
        plotArea = (WebView) findViewById(R.id.graphplotAreaWeb);
        plotArea.getSettings().setJavaScriptEnabled(true);
        plotArea.setWebViewClient(new WebViewClient());
        plotArea.addJavascriptInterface(new ActivityValues(this), "ACTIVITY_DATA");
        plotArea.loadUrl("file:///android_asset/cse535/mobilecomputing/spring2018/group3/graph.html");
    }


    /**
     * Set data to pass to the JavaScript
     */
    class ActivityValues {
        Context mContext;

        /**
         * Instantiate the interface and set the values
         */
        ActivityValues(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getRunX() {
            return formStringData(runX);
        }

        @JavascriptInterface
        public String getRunY() {
            return formStringData(runY);
        }

        @JavascriptInterface
        public String getRunZ() {
            return formStringData(runZ);
        }

        @JavascriptInterface
        public String getWalkX() {
            return formStringData(walkX);
        }

        @JavascriptInterface
        public String getWalkY() {
            return formStringData(walkY);
        }

        @JavascriptInterface
        public String getWalkZ() {
            return formStringData(walkZ);
        }

        @JavascriptInterface
        public String getJumpX() {
            return formStringData(jumpX);
        }

        @JavascriptInterface
        public String getJumpY() {
            return formStringData(jumpY);
        }

        @JavascriptInterface
        public String getJumpZ() {
            return formStringData(jumpZ);
        }

        private String formStringData(double[][] data) {
            if (data == null) {
                return "";
            }
            StringBuilder res = new StringBuilder();
            int i = 0;
            for (double[] val : data) {
                if (i != 0) {
                    res.append("::");
                }
                int j = 0;
                for (double v : val) {
                    if (j != 0) {
                        res.append(",");
                    }
                    res.append(v);
                    j++;
                }
                i++;
            }
            return res.toString();
        }
    }
}
