/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.io.CSVLoader;
import com.itera.io.Loader;
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
import java.util.Random;
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
        String fileName = "/media/thiagodepaulo/Dados/Thiago/publicacoes/inotebook/publicacoes.docs";
        String sep = "\\|";
        Loader loader = new CSVLoader(fileName, sep);
        List<InputPattern> linput = loader.load();
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 2, false, true, true, false, true);
        linput = Preprocessing.preprocess(linput, config);
        Data data = Conversor.listInputPatternToData(linput, config);
        
        Instances wdata = Conversor.dataToArff(data);
        weka.classifiers.evaluation.Evaluation eval = new weka.classifiers.evaluation.Evaluation(wdata);
        eval.crossValidateModel(new NaiveBayesMultinomial(), wdata, numFolds, new Random());
        System.out.println(eval.toSummaryString());

    }

}
