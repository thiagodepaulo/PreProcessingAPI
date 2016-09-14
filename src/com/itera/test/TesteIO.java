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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import weka.classifiers.trees.J48;

/**
 *
 * @author root
 */
public class TesteIO {
    
    public static void main(String[] args) throws Exception {
        boolean save = false;
        String arffArqName = "/home/thiagodepaulo/teste_jurídico.arff";
        Data data = Conversor.arffToData(arffArqName);        
        
        
        Data train = data.trainCV(10, 0);
        Data teste = data.testCV(10, 0);                
        
        if (save) {
            WekaClassifier wcls = new WekaClassifier(new J48(), "", data);
            wcls.buildClassifier(train);
            save(wcls);
        } else {
            System.out.println("loading...");
            WekaClassifier wcls = load("obj.tct");
            System.out.println("loaded");
            int clsid = wcls.classifyInstance(teste.getAdjListDoc(0));
            System.out.println(clsid);
        }
        
        
    }
    
    public static void save(Object wcls) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("obj.tct"));
        out.writeObject(wcls);
        out.close();
    }
    
    public static WekaClassifier load(String arq) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(arq));
        return (WekaClassifier)in.readObject();
    }
    
}
