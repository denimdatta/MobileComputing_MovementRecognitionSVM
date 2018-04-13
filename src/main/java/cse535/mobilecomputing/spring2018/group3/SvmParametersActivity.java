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

public class SvmParametersActivity extends AppCompatActivity {
    String CostNuOpt = "";
    StringBuilder CustomErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svm_parameters);

        final TextView costNuTV = (TextView) findViewById(R.id.CostNuTV);
        final Spinner svmTypeSpn = (Spinner) findViewById(R.id.svmSpn);
        final Spinner kernelTypeSpn = (Spinner) findViewById(R.id.kernelSpn);
        final EditText costnuTx = (EditText) findViewById(R.id.costNuTxt);
        final EditText gammaTx = (EditText) findViewById(R.id.gammaTxt);
        final Spinner kcvTypeSpn = (Spinner) findViewById(R.id.kcvSpn);

        gammaTx.setText(Double.toString(Math.round(100.0/Constants.FEATURES)/100.0));

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


        Button trainBtn = (Button) findViewById(R.id.TrainSVMBtn);
        trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomErrorMsg = new StringBuilder("");
                TextView resultTV = (TextView) findViewById(R.id.trainResultTV);
                try {
                    int svmType = getResources().getIntArray(R.array.svm_type_value)[svmTypeSpn.getSelectedItemPosition()];
                    int kernelType = getResources().getIntArray(R.array.kernel_type_value)[kernelTypeSpn.getSelectedItemPosition()];
                    double cost_nu = Double.parseDouble(costnuTx.getText().toString());
                    double gamma = Double.parseDouble(gammaTx.getText().toString());
                    int crossValid = Integer.parseInt(kcvTypeSpn.getSelectedItem().toString());

                    if (CostNuOpt.equalsIgnoreCase(" -n ") && (cost_nu>=1 || cost_nu <=0)) {
                        CustomErrorMsg.append("Nu should be >0 & <1\n");
                    }

                    if (!CustomErrorMsg.toString().isEmpty()) {
                        System.out.println("[ERROR]: " + CustomErrorMsg);
                        throw new NumberFormatException(CustomErrorMsg.toString());
                    }
                    StringBuilder options = new StringBuilder("-q -s ").append(svmType).append(" -t ").append(kernelType)
                            .append(CostNuOpt + cost_nu).append(" -g ").append(gamma).append(" -v ").append(crossValid);

//                    double accuracy = UtilitySVMTrain.train("-q -s 1 -v 5 test".split(" "));
                    double accuracy = UtilitySVMTrain.train(options.toString().split("\\s+"));
                    StringBuilder result = new StringBuilder("Result\nAccuracy: " + Math.round(accuracy * 100.0) / 100.0 + "%\n\n");
                    result.append(svmTypeSpn.getSelectedItem().toString()+ "    SVM Type\n");
                    result.append(kernelTypeSpn.getSelectedItem().toString()+ " Kernel Function\n");
                    if (CostNuOpt.equalsIgnoreCase(" -n ")) {
                        result.append("Nu Parameter:  ");
                    } else {
                        result.append("Cost Parameter:  ");
                    }
                    result.append(cost_nu + "\nGamma in Kernel:  " + gamma +"\n");
                    result.append(kcvTypeSpn.getSelectedItem().toString()+ "-fold Cross Validation Technique\n");
                    resultTV.setText(result.toString());
                } catch (IOException | NumberFormatException ex) {
                    StringBuilder error = new StringBuilder("Error in SVM train");
                    if(!CustomErrorMsg.toString().isEmpty()) {
                        error.append("\n").append(CustomErrorMsg.toString());
                    }
                    resultTV.setText(error.toString());
                    ex.printStackTrace();
                }
            }
        });
    }
}
