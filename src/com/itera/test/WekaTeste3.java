/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.CSVLoader;
import com.itera.io.Loader;
import com.itera.learning.classifier.TextClassifier;
import com.itera.learning.classifier.supervised.WekaClassifier;
import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.preprocess.tools.Preprocessing;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.InputPattern;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaTeste3 {

    public static void main(String args[]) throws FileNotFoundException, IOException, Exception {
        int numFolds = 10;
        String fileName = "/media/thiagodepaulo/Dados/Thiago/publicacoes/inotebook/publicacoes.docs";
        String sep = "\\|";
        Loader loader = new CSVLoader(fileName, sep);
        List<InputPattern> linput = loader.load();
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 2, false, true, true, false, true);
        linput = Preprocessing.preprocess(linput, config);
        Data myData = Conversor.listInputPatternToData(linput, config);

        myData.stratify(10);
        for (int i = 0; i < numFolds; i++) {
            System.out.println("Cross-validation " + i);
            Data train = myData.trainCV(numFolds, i);
            Instances wtrain = Conversor.dataToArff(train);

            Data test = myData.testCV(numFolds, i);
            Instances wtest = Conversor.dataToArff(test);

            AbstractClassifier wcls = new NaiveBayesMultinomial();
            TextClassifier mycls = new WekaClassifier(wcls, " ", train);

            System.out.println("Treinando...");
            wcls.buildClassifier(wtrain);
            mycls.buildClassifier(train);
            System.out.println("Fim treinamento.");

            System.out.println("classificando...");
            for (int j = 0; j < test.getNumDocs(); j++) {
                if (j % 100 == 0) {
                    System.out.println(((double) j / test.getNumDocs()) + "%");
                }
                int idmcls = mycls.classifyInstance(test.getAdjListDoc(j));
                int idwcls = (int) wcls.classifyInstance(wtest.get(j));
                if (idmcls != idwcls) {
                    javax.swing.JOptionPane.showMessageDialog(null, "diferente!");
                }
                
                int wc = (int)(wtest.get(j).value(wtest.classIndex()));
                int mc = (test.getClassDocument(j));
                if (wc != mc) {
                    javax.swing.JOptionPane.showMessageDialog(null, "diferente!");
                }
                
                
            }
        }
    }

}
