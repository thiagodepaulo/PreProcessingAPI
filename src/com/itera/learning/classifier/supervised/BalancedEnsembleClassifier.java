/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Data;
import com.itera.structures.Example;
import com.itera.structures.TextData;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import com.itera.util.Tools;
import com.itera.util.VectorOps;
import java.util.HashMap;
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

    public BalancedEnsembleClassifier(TextData data, int nSamples, int nDocsPerClass) {
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
    public void buildClassifier(TextData data) throws Exception {
        TextData[] vetData = randomSplitData(data);
        for (int i = 0; i < this.nSamples; i++) {
            this.classifiers[i] = new TextWekaClassifier(new J48(), "-C 0.25 -M 2", vetData[i]);
            this.classifiers[i].buildClassifier(vetData[i]);
        }
    }

    @Override
    public int classifyInstance(Example instance) throws Exception {
        int[] votes = new int[this.classes.size()];
        for (int i = 0; i < this.nSamples; i++) {
            int classId = this.classifiers[i].classifyInstance(instance);
            votes[classId]++;
        }
        return VectorOps.argmax(votes);
    }

    @Override
    public double[] distributionForInstance(Example instance) throws Exception {
        double[] dist = new double[this.classes.size()];
        for (int i = 0; i < this.nSamples; i++) {
            double[] r = this.classifiers[i].distributionForInstance(instance);
            VectorOps.add(dist, r);
        }
        return VectorOps.normalize(dist);
    }

    private int[][] dataClassDocs(TextData data) {

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

    public TextData[] randomSplitData(TextData data) {
        Random r = new Random(System.currentTimeMillis());
        int[][] dataClassDocs = this.dataClassDocs(data);
        TextData[] vetData = new TextData[this.nSamples];
        for (int i = 0; i < this.nSamples; i++) {
            vetData[i] = data.newCopy();
            int newDocId;
            HashMap<Integer, String> ids_doc = new HashMap<>();
            for (int c = 0; c < data.getNumClasses(); c++) {
                int nDocsPerClass = dataClassDocs[c].length;
                if (nDocsPerClass <= 0) {
                    continue;
                }
                for (int j = 0; j < this.nDocsPerClass; j++) {
                    int pos = r.nextInt(nDocsPerClass);
                    int docId = dataClassDocs[c][pos];
                    int classId = data.getClassDocument(docId);
                    newDocId = vetData[i].addAdjListDoc(data.getAdjListDoc(docId));
                    vetData[i].addClassDocument(newDocId, classId);
                    ids_doc.put(newDocId, "" + docId);
                    vetData[i].setIDsDocs(ids_doc);
                    vetData[i].setDocsIDs(Tools.invertHashMap(ids_doc));
                }
            }
        }
        return vetData;
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
