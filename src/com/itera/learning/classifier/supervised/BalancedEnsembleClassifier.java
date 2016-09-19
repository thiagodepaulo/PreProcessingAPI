/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Data;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import com.itera.util.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.classifiers.trees.J48;

/**
 *
 * @author root
 */
public class BalancedEnsembleClassifier extends TextClassifier {
    
    private int nSamples = 100;
    private int nDocsPerClass = 50;
    private TextClassifier[] classifiers;
    
    public BalancedEnsembleClassifier(Data data, int nSamples, int nDocsPerClass) {
        super(data, "supervised");
        this.nSamples = nSamples;
        this.nDocsPerClass = nDocsPerClass;
        this.classifiers = new TextClassifier[nSamples];
    }
    
    @Override
    public int classifyInstance(InputPattern textInstance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public double[] distributionForInstance(InputPattern textInstance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void buildClassifier(Data data) throws Exception {
        Data[] vetData = randomSplitData(data);
        for (int i = 0; i < this.nSamples; i++) {
            this.classifiers[i] = new WekaClassifier(new J48(), learningType, vetData[i]);
            this.classifiers[i].buildClassifier(vetData[i]);
        }
    }
    
    @Override
    public int classifyInstance(List<IndexValue> instance) throws Exception {
        int[] votes = new int[this.classes.size()];
        for(int i=0; i<this.nSamples; i++) {
            int classId = this.classifiers[i].classifyInstance(instance);
            votes[classId]++;
        }
        return Tools.argmax(votes);
    }
    
    @Override
    public double[] distributionForInstance(List<IndexValue> instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private int[][] dataClassDocs(Data data) {
        
        int numClass = data.getNumClasses();
        int[][] dataClassDocs = new int[numClass][];
        int[] numDocsPerClass = data.getNumDocsPerClasses();
        int[] docPosPerClass = new int[numClass];
        for (int i = 0; i < numClass; i++) {
            dataClassDocs[i] = new int[numDocsPerClass[i]];
        }
        for (int docId : data.getDocsIds()) {
            int classId = data.getClassDocument(docId);
            if (dataClassDocs[classId] == null) {
                dataClassDocs[classId] = new int[numDocsPerClass[classId]];
            }
            dataClassDocs[classId][docPosPerClass[classId]++] = docId;
        }
        return dataClassDocs;
    }
    
    public Data[] randomSplitData(Data data) {
        Random r = new Random(System.currentTimeMillis());
        int[][] dataClassDocs = this.dataClassDocs(data);
        Data[] vetData = new Data[this.nSamples];
        for (int i = 0; i < this.nSamples; i++) {
            vetData[i] = data.newCopy();            
            for (int c = 0; c < data.getNumClasses(); c++) {
                for (int j = 0; j < this.nDocsPerClass; j++) {
                    int pos = r.nextInt(dataClassDocs[c].length);
                    int docId = dataClassDocs[c][pos];
                    int classId = data.getClassDocument(docId);
                    vetData[i].addAdjListDoc(data.getAdjListDoc(docId));
                    vetData[i].addClassDocument(docId, classId);
                }
            }
        }
        return vetData;
    }    
}
