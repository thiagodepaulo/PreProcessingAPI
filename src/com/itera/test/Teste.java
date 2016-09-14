/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.learning.classifier.supervised.WekaClassifier;
import com.itera.learning.evaluator.Evaluator;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import weka.classifiers.trees.J48;

/**
 *
 * @author root
 */
public class Teste {

    public static void main(String args[]) throws Exception {
        String arffArqName = "/home/thiagodepaulo/teste_jurídico.arff";
        Data data = Conversor.arffToData(arffArqName);        
        WekaClassifier wcls = new WekaClassifier(new J48(), "", data);                
        Evaluator[] evals = Evaluator.crossValidateModel(wcls, data, 10);
    }

}
