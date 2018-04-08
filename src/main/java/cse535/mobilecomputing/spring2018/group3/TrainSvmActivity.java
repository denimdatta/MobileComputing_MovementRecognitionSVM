package cse535.mobilecomputing.spring2018.group3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TrainSvmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_svm);
        Utility.convertDatoToSVM();
    }
}
