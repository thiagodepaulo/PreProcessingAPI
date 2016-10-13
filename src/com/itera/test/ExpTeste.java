/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.CSVLoader;
import com.itera.io.Loader;
import com.itera.learning.classifier.TextClassifier;
import com.itera.learning.classifier.supervised.BalancedEnsembleClassifier;
import com.itera.learning.classifier.supervised.IMHM_DocTerm;
import com.itera.learning.classifier.supervised.WekaClassifier;
import com.itera.learning.evaluator.Evaluator;
import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.preprocess.tools.Preprocessing;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.InputPattern;
import com.itera.util.Tools;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class ExpTeste {

    public static void main(String args[]) throws Exception {
        String fileName = "/media/thiagodepaulo/Dados/Thiago/publicacoes/inotebook/publicacoes.docs";
        String sep = "\\|";
        Loader loader = new CSVLoader(fileName, sep);
        List<InputPattern> linput = loader.load();
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 2, false, true, true, false, true);
        linput = Preprocessing.preprocess(linput, config);
        Data data = Conversor.listInputPatternToData(linput, config);

        Evaluator eval = new Evaluator(data);

        TextClassifier cls = new BalancedEnsembleClassifier(data, 100, 10);
        eval.crossValidateModel(cls, data, 10);

        
        PrintWriter pw = new PrintWriter(new File("/home/thiagodepaulo/out-ess.txt"));
        pw.println(eval.toSummaryString());
        pw.close();
    }
}
