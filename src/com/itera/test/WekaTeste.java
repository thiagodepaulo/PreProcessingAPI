/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaTeste {

    public static void main(String args[]) throws FileNotFoundException, IOException, Exception {
        int numFolds = 10;
        String arffArqName = "/home/thiagodepaulo/teste_jurídico.arff";
        BufferedReader reader = new BufferedReader(new FileReader(arffArqName));
        Instances data = new Instances(reader);
        reader.close();

        if (data.classIndex() < 0) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        data.stratify(10);
        for (int i = 0; i < numFolds; i++) {
            Evaluation eval = new Evaluation(data);
            Instances train = data.trainCV(numFolds, i);
            Instances test = data.testCV(numFolds, i);
            System.out.println("data: "+data.numInstances());
            System.out.println("train: "+train.numInstances());
            System.out.println("test: "+test.numInstances());
            NaiveBayesMultinomial cls = new NaiveBayesMultinomial();            
            cls.buildClassifier(train);            
            System.out.println(cls.classifyInstance(test.firstInstance()));
            eval.evaluateModel(cls, test);            
            System.out.println(eval.toSummaryString());
        }
    }

}
