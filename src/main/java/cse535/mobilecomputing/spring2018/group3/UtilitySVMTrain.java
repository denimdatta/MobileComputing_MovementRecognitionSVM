package cse535.mobilecomputing.spring2018.group3;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import libsvm.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.io.*;
import java.util.*;

/**
 * Minor Modification of svm_train.java from libsvm library
 * modified by: Group3
 * This class train the data using SVM from libsvm library
 */
class UtilitySVMTrain {
    private svm_parameter param;        // set by parse_command_line
    private svm_problem prob;        // set by read_problem
    private int cross_validation;
    private int nr_fold;
    private double accuracy = -1;

    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    private static void exit_with_help() {
        System.out.println(
                "Usage: svm_train [options] training_set_file [model_file]\n"
                        + "options:\n"
                        + "-s svm_type : set type of SVM (default 0)\n"
                        + "	0 -- C-SVC		(multi-class classification)\n"
                        + "	1 -- nu-SVC		(multi-class classification)\n"
                        + "	2 -- one-class SVM\n"
                        + "	3 -- epsilon-SVR	(regression)\n"
                        + "	4 -- nu-SVR		(regression)\n"
                        + ""
                        + "-t kernel_type : set type of kernel function (default 2)\n"
                        + "	0 -- linear: u'*v\n"
                        + "	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
                        + "	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
                        + "	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
                        + "	4 -- precomputed kernel (kernel values in training_set_file)\n"
                        + ""
                        + "-d degree : set degree in kernel function (default 3)\n"
                        + "-g gamma : set gamma in kernel function (default 1/num_features)\n"
                        + "-r coef0 : set coef0 in kernel function (default 0)\n"
                        + "-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
                        + "-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
                        + "-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
                        + "-m cachesize : set cache memory size in MB (default 100)\n"
                        + "-e epsilon : set tolerance of termination criterion (default 0.001)\n"
                        + "-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
                        + "-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
                        + "-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
                        + "-v n : n-fold cross validation mode\n"
                        + "-q : quiet mode (no outputs)\n"
        );
        System.exit(1);
    }

    private void do_cross_validation() {
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob, param, nr_fold, target);
        if (param.svm_type == svm_parameter.EPSILON_SVR ||
                param.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < prob.l; i++) {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
            System.out.print("Cross Validation Squared correlation coefficient = " +
                    ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy)) /
                            ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy)) + "\n"
            );
        } else {
            for (i = 0; i < prob.l; i++)
                if (target[i] == prob.y[i])
                    ++total_correct;
            accuracy = 100.0 * total_correct / prob.l;
        }
    }

    private void run(String argv[]) throws IOException {
        parse_command_line(argv);
        read_problem();
        String error_msg;
        error_msg = svm.svm_check_parameter(prob, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
            System.exit(1);
        }

        svm_model model;
        if (cross_validation != 0) {
            do_cross_validation();
            model = svm.svm_train(prob, param);
            svm.svm_save_model(Constants.filePath + "svm_model.txt", model);
        } else {
            model = svm.svm_train(prob, param);
            svm.svm_save_model(Constants.filePath + Constants.MODELFILE, model);
        }
    }

    public static double train(String argv[]) throws IOException {
        UtilitySVMTrain t = new UtilitySVMTrain();
        t.run(argv);
        return t.accuracy;
    }

    private static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return (d);
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private void parse_command_line(String argv[]) {
        int i;
        svm_print_interface print_func = null;    // default printing to stdout

        param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;    // 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        cross_validation = 0;

        // parse options
        for (i = 0; i < argv.length; i++) {
            if (argv[i].charAt(0) != '-') break;
            if (++i >= argv.length)
                exit_with_help();
            switch (argv[i - 1].charAt(1)) {
                case 's':
                    param.svm_type = atoi(argv[i]);
                    break;
                case 't':
                    param.kernel_type = atoi(argv[i]);
                    break;
                case 'd':
                    param.degree = atoi(argv[i]);
                    break;
                case 'g':
                    param.gamma = atof(argv[i]);
                    break;
                case 'r':
                    param.coef0 = atof(argv[i]);
                    break;
                case 'n':
                    param.nu = atof(argv[i]);
                    break;
                case 'm':
                    param.cache_size = atof(argv[i]);
                    break;
                case 'c':
                    param.C = atof(argv[i]);
                    break;
                case 'e':
                    param.eps = atof(argv[i]);
                    break;
                case 'p':
                    param.p = atof(argv[i]);
                    break;
                case 'h':
                    param.shrinking = atoi(argv[i]);
                    break;
                case 'b':
                    param.probability = atoi(argv[i]);
                    break;
                case 'q':
                    print_func = svm_print_null;
                    i--;
                    break;
                case 'v':
                    cross_validation = 1;
                    nr_fold = atoi(argv[i]);
                    if (nr_fold < 2) {
                        System.err.print("n-fold cross validation: n must >= 2\n");
                        exit_with_help();
                    }
                    break;
                case 'w':
                    ++param.nr_weight;
                {
                    int[] old = param.weight_label;
                    param.weight_label = new int[param.nr_weight];
                    System.arraycopy(old, 0, param.weight_label, 0, param.nr_weight - 1);
                }

                {
                    double[] old = param.weight;
                    param.weight = new double[param.nr_weight];
                    System.arraycopy(old, 0, param.weight, 0, param.nr_weight - 1);
                }

                param.weight_label[param.nr_weight - 1] = atoi(argv[i - 1].substring(2));
                param.weight[param.nr_weight - 1] = atof(argv[i]);
                break;
                default:
                    System.err.print("Unknown option: " + argv[i - 1] + "\n");
                    exit_with_help();
            }
        }

        svm.svm_set_print_string_function(print_func);
    }

    // read in a problem (in svmlight format)

    private void read_problem() throws IOException {
        Vector<Double> vy = new Vector<>();
        Vector<svm_node[]> vx = new Vector<>();
        int max_index = 0;

        Utility.createDB();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(Constants.filePath + Constants.DBNAME, null, SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                int label = 0;
                double[] x_f = new double[Constants.LIMIT];
                double[] y_f = new double[Constants.LIMIT];
                double[] z_f = new double[Constants.LIMIT];
                for (int i = 0; i < Constants.LIMIT; i++) {
                    int k = 3 * i + 1;
                    x_f[i] = (double) c.getFloat(k);
                    y_f[i] = (double) c.getFloat(k + 1);
                    z_f[i] = (double) c.getFloat(k + 2);
                }

                String act = c.getString(3 * Constants.LIMIT + 1);
                if (act.equalsIgnoreCase("RUN")) {
                    label = 1;
                } else if (act.equalsIgnoreCase("WALK")) {
                    label = 2;
                } else if (act.equalsIgnoreCase("JUMP")) {
                    label = 3;
                }

                Mean mu = new Mean();
                StandardDeviation sd = new StandardDeviation();
                double[] features = {
                        mu.evaluate(x_f, 0, x_f.length),
                        mu.evaluate(y_f, 0, y_f.length),
                        mu.evaluate(z_f, 0, z_f.length),
                        sd.evaluate(x_f),
                        sd.evaluate(y_f),
                        sd.evaluate(z_f)
                };

                if (label != 0) {
                    vy.addElement((double) label);
                    int m = Constants.FEATURES;
                    svm_node[] feat = new svm_node[m];
                    for (int j = 0; j < m; j++) {
                        feat[j] = new svm_node();
                        feat[j].index = j + 1;
                        feat[j].value = features[j];
                    }
                    // if (m > 0)
                        max_index = Math.max(max_index, feat[m - 1].index);
                    vx.addElement(feat);
                }
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for (int i = 0; i < prob.l; i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for (int i = 0; i < prob.l; i++)
            prob.y[i] = vy.elementAt(i);

        if (param.gamma == 0 && max_index > 0)
            param.gamma = 1.0 / max_index;

        if (param.kernel_type == svm_parameter.PRECOMPUTED)
            for (int i = 0; i < prob.l; i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if ((int) prob.x[i][0].value <= 0 || (int) prob.x[i][0].value > max_index) {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
    }
}
