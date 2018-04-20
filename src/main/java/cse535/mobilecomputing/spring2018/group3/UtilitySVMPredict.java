package cse535.mobilecomputing.spring2018.group3;

import java.io.FileNotFoundException;
import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

/**
 * Minor Modification of svm_predict.java from libsvm library
 * modified by: Group3
 * This class predict the class for current data using SVM from libsvm library
 */
class UtilitySVMPredict {

    static String prediction(double[] feature) throws IOException {
        int i, predict_probability = 0;
        try {
            svm_model model = svm.svm_load_model(Constants.filePath + Constants.MODELFILE);
            if (model == null) {
                System.err.print("can't open model file " + Constants.MODELFILE + "\n");
                System.exit(1);
            }
            if (svm.svm_check_probability_model(model) != 0) {
                System.out.print("Model supports probability estimates, but disabled in prediction.\n");
            }
            double predicted = predict(model, feature, predict_probability);

            String predictedActivity;

            // Based on predicted value, set the Activity Name
            switch ((int) Math.round(predicted)) {
                case Constants.RUN_LABEL:
                    predictedActivity = Constants.RUN_VALUE;
                    break;
                case Constants.WALK_LABEL:
                    predictedActivity = Constants.WALK_VALUE;
                    break;
                case Constants.JUMP_LABEL:
                    predictedActivity = Constants.JUMP_VALUE;
                    break;
                default:
                    predictedActivity = null;
                    break;
            }
            return predictedActivity;
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double predict(svm_model model, double[] feature, int predict_probability) throws IOException {

        int svm_type = svm.svm_get_svm_type(model);
        int nr_class = svm.svm_get_nr_class(model);
        double[] prob_estimates = null;

        if (predict_probability == 1) {
            if (svm_type == svm_parameter.EPSILON_SVR ||
                    svm_type == svm_parameter.NU_SVR) {
                System.out.print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma=" + svm.svm_get_svr_probability(model) + "\n");
            } else {
                int[] labels = new int[nr_class];
                svm.svm_get_labels(model, labels);
                prob_estimates = new double[nr_class];
            }
        }

        int m = Constants.FEATURES;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = j + 1;
            x[j].value = feature[j];
        }

        double v;
        if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
            v = svm.svm_predict_probability(model, x, prob_estimates);
        } else {
            v = svm.svm_predict(model, x);
        }
        return v;
    }
}
