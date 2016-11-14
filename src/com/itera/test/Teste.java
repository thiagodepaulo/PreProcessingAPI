/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.CSVLoader;
import com.itera.io.DirectoryLoader;
import com.itera.io.Loader;
import com.itera.learning.classifier.Classifier;
import com.itera.learning.classifier.TextClassifier;
import com.itera.learning.classifier.supervised.BalancedEnsembleClassifier;
import com.itera.learning.classifier.supervised.TextWekaClassifier;
import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.preprocess.tools.Preprocessing;
import com.itera.structures.Conversor;
import com.itera.structures.TextData;
import com.itera.structures.InputPattern;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class Teste {

    public static void main(String args[]) throws Exception {
        String fileName = "/media/thiagodepaulo/Dados/Thiago/publicacoes/inotebook/publicacoes.docs";
        String sep = "\\|";
        Loader loader = new CSVLoader(fileName, sep);
        List<InputPattern> linput = loader.loadTextualData();
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 2, false, true, true, false, true);
        linput = Preprocessing.preprocess(linput, config);
        TextData data = Conversor.listInputPatternToTextData(linput, config);
        Instances wdata = Conversor.textDataToArff(data);

        weka.classifiers.Classifier cls = new NaiveBayesMultinomial();
        int nfolds = 10;
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(wdata);
        for(int i=0; i< nfolds; i++) {
            System.out.println("cross-validation "+i);
            Instances train = wdata.trainCV(nfolds, i);
            Instances test = wdata.testCV(nfolds, i);
            
            weka.classifiers.Classifier clsCopy = AbstractClassifier.makeCopy(cls);
            clsCopy.buildClassifier(train);           
            eval.evaluateModel(clsCopy, test);
        }
        
        

        PrintWriter pw = new PrintWriter(new File("/home/thiagodepaulo/weka-out-ess.txt"));
        pw.println(eval.toSummaryString());        
        pw.close();
    }

}
