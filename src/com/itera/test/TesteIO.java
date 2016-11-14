/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.learning.classifier.supervised.TextWekaClassifier;
import com.itera.learning.evaluator.Evaluator;
import com.itera.structures.Conversor;
import com.itera.structures.TextData;
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
        TextData data = Conversor.arffToData(arffArqName);        
        
        
        TextData train = data.trainCV(10, 0);
        TextData teste = data.testCV(10, 0);                
        
        if (save) {
            TextWekaClassifier wcls = new TextWekaClassifier(new J48(), "", data);
            wcls.buildClassifier(train);
            save(wcls);
        } else {
            System.out.println("loading...");
            TextWekaClassifier wcls = load("obj.tct");
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
    
    public static TextWekaClassifier load(String arq) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(arq));
        return (TextWekaClassifier)in.readObject();
    }
    
}
