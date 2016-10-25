/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaTeste4 {

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        BufferedReader reader = new BufferedReader(
                new FileReader("/home/thiagodepaulo/Downloads/bank.arff"));
        Instances data = new Instances(reader);
        reader.close();                
        data.setClassIndex(data.numAttributes() - 1);
        
        AbstractClassifier cls = new J48();
        cls.buildClassifier(data);
        
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("modelo.ttc"));
        out.writeObject(cls);
        out.close();
                
        double r = cls.classifyInstance(data.get(13));
        
        System.out.println(r);
                
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(cls, data, 10, new Random());
        System.out.println(eval.toSummaryString());
        
    }
    
    

}
