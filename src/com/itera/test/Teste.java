/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.DirectoryLoader;
import com.itera.io.Loader;
import com.itera.learning.classifier.TextClassifier;
import com.itera.learning.classifier.supervised.BalancedEnsembleClassifier;
import com.itera.learning.evaluator.Evaluator;
import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.preprocess.tools.Preprocessing;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.InputPattern;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author root
 */
public class Teste {

    public static void main(String args[]) throws Exception {
        Loader loader = new DirectoryLoader("/home/thiagodepaulo/Modelo/");
        List<InputPattern> linput = loader.load();
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 2, false, true, true, false, true);
        linput = Preprocessing.preprocess(linput, config);
        Data data = Conversor.listInputPatternToData(linput, config);

        //TextClassifier wcls = new WekaClassifier(new J48(), "", data);                
        TextClassifier wcls = new BalancedEnsembleClassifier(data, 100, 50);

        Evaluator[] evals = Evaluator.crossValidateModel(wcls, data, 10);

        PrintWriter pw = new PrintWriter(new File("out-ess.txt"));
        int i = 0;
        for (Evaluator e : evals) {
            pw.println(i++);
            pw.println(e.toSummaryString());
        }
        pw.close();
    }

}
