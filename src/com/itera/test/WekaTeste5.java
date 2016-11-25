/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.CSVLoader;
import com.itera.learning.classifier.Classifier;
import com.itera.learning.classifier.supervised.WekaClassifier;
import com.itera.learning.evaluator.Evaluator;
import com.itera.structures.Conversor;
import com.itera.structures.DenseData;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaTeste5 {
    
    public static void main(String[] args) throws Exception {
        String arq = "/home/thiagodepaulo/iris.data";
        CSVLoader loader = new CSVLoader(arq, ",", false);
        DenseData data = loader.loadDenseData();
        data.setLastAsClassIndex();
        
        Classifier cls = new WekaClassifier(new J48());               
        
        Evaluator eval = new Evaluator(data);
        eval.crossValidateModel(cls, data, 10);
        
        System.out.println(eval.toSummaryString());
    }
    
}
