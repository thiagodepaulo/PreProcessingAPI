/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.structures.Conversor;
import com.itera.structures.Data;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaTeste2 {
    
    public static void main(String args[]) throws IOException, Exception {
        int numFolds = 10;
        String arffArqName = "/home/thiagodepaulo/teste_jurídico.arff";
        BufferedReader reader = new BufferedReader(new FileReader(arffArqName));
        Instances wdata = new Instances(reader);
        reader.close();
        
        Data data = Conversor.arffToData(arffArqName);
        data.stratify(10);
        Data teste = data.testCV(10, 1);
        Data train = data.trainCV(10, 1);
        
        Instances wteste = Conversor.dataToArff(teste);
        Instances wtrain = Conversor.dataToArff(train);
        
        NaiveBayesMultinomial cls = new NaiveBayesMultinomial();
        cls.buildClassifier(wtrain);
        System.out.println(cls.classifyInstance(wteste.firstInstance()));
    }
    
}
