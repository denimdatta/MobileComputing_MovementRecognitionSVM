package cse535.mobilecomputing.spring2018.group3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

/**
 * @author Group3 CSE535 Spring 2018
 */
public class SvmParametersActivity extends AppCompatActivity {
    String CostNuOpt = "";
    StringBuilder CustomErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svm_parameters);

        final Spinner svmTypeSpn = (Spinner) findViewById(R.id.svmSpn);
        final Spinner kernelTypeSpn = (Spinner) findViewById(R.id.kernelSpn);
        final EditText costnuTx = (EditText) findViewById(R.id.costNuTxt);
        final EditText gammaTx = (EditText) findViewById(R.id.gammaTxt);
        final Spinner kcvTypeSpn = (Spinner) findViewById(R.id.kcvSpn);

        final TextView costNuTV = (TextView) findViewById(R.id.CostNuTV);
        gammaTx.setText(Double.toString(Math.round(100.0 / Constants.FEATURES) / 100.0));

        // Based on selected SVM type, set the required parameter as Cost or NU
        svmTypeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int svmID = getResources().getIntArray(R.array.svm_type_value)[svmTypeSpn.getSelectedItemPosition()];
                if (svmID == 0) {
                    costNuTV.setText("Cost :");
                    costnuTx.setText("1");
                    CostNuOpt = " -c ";
                } else if (svmID == 1) {
                    costNuTV.setText("NU :");
                    costnuTx.setText("0.5");
                    CostNuOpt = " -n ";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Train
        Button trainBtn = (Button) findViewById(R.id.TrainSVMBtn);
        trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomErrorMsg = new StringBuilder("");
                TextView resultTV = (TextView) findViewById(R.id.trainResultTV);
                try {
                    // Get the selected parameters
                    int svmType = getResources().getIntArray(R.array.svm_type_value)[svmTypeSpn.getSelectedItemPosition()];
                    int kernelType = getResources().getIntArray(R.array.kernel_type_value)[kernelTypeSpn.getSelectedItemPosition()];
                    double cost_nu = Double.parseDouble(costnuTx.getText().toString());
                    double gamma = Double.parseDouble(gammaTx.getText().toString());
                    int crossValid = Integer.parseInt(kcvTypeSpn.getSelectedItem().toString());

                    if (CostNuOpt.equalsIgnoreCase(" -n ") && (cost_nu >= 1 || cost_nu <= 0)) {
                        CustomErrorMsg.append("Nu should be >0 & <1\n");
                    }

                    if (!CustomErrorMsg.toString().isEmpty()) {
                        System.out.println("[ERROR]: " + CustomErrorMsg);
                        throw new NumberFormatException(CustomErrorMsg.toString());
                    }

                    // Construct an option to be passed to UtilitySVMTrain.train() method and call the method
                    String options = "-q -s " + svmType + " -t " + kernelType + CostNuOpt + cost_nu
                            + " -g " + gamma  + " -v "+ crossValid;

                    double accuracy = UtilitySVMTrain.train(options.split("\\s+"));

                    // Construct the result to be displayed.
                    StringBuilder result = new StringBuilder("Result\nAccuracy: " + Math.round(accuracy * 100.0) / 100.0 + "%\n\n");
                    result.append(svmTypeSpn.getSelectedItem().toString());
                    result.append("    SVM Type\n");
                    result.append(kernelTypeSpn.getSelectedItem().toString());
                    result.append(" Kernel Function\n");
                    if (CostNuOpt.equalsIgnoreCase(" -n ")) {
                        result.append("Nu Parameter:  ");
                    } else {
                        result.append("Cost Parameter:  ");
                    }
                    result.append(cost_nu);
                    result.append("\nGamma in Kernel:  ");
                    result.append(gamma);
                    result.append("\n");
                    result.append(kcvTypeSpn.getSelectedItem().toString());
                    result.append("-fold Cross Validation Technique\n");
                    double testPerc = ((double) Constants.TESTCOUNT * 100) / Constants.REPEAT;
                    double trainPerc = 100 - testPerc;
                    String TrTsResult = "Train-Test split ratio: " + trainPerc + "%-" + testPerc + "%\n";
                    result.append(TrTsResult);

                    // Display result
                    resultTV.setText(result.toString());
                } catch (IOException | NumberFormatException ex) {
                    StringBuilder error = new StringBuilder("Error in SVM train");
                    if (!CustomErrorMsg.toString().isEmpty()) {
                        error.append("\n").append(CustomErrorMsg.toString());
                    }
                    resultTV.setText(error.toString());
                    ex.printStackTrace();
                }
            }
        });
    }
}
