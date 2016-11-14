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
import com.itera.structures.SparseExample;
import com.itera.util.VectorOps;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class IMHM_DocTerm extends TextClassifier {

    private int numDocs; //Number of documents
    private int numClasses; //Number of classes
    private int numTerms; //Number of terms

    private double errorCorrectionRate; // Error correction error
    private int maxNumberIterations; // Maximum number of iterations
    private double minError; // Minimum Mean Squared Error

    public IMHM_DocTerm(TextData data, double errorCorrectionRate, double minError, int maxNumIterations) {
        super(data, "supervised");
        this.errorCorrectionRate = errorCorrectionRate;
        this.minError = minError;
        this.maxNumberIterations = maxNumIterations;
    }

    @Override
    public void buildClassifier(TextData data) throws Exception {
        System.out.println("- Alocando estrutura de dados...");

        numDocs = data.getNumDocs();
        numClasses = data.getNumClasses();
        numTerms = data.getNumTerms();

        double[][] yDoc = getRealClasses(data);
        double[][] fTerms = getFTerms();

        //Adjacency lists to speed up learning
        ArrayList<ArrayList<IndexValue>> adjancecyListsDocTerm = data.getAdjListDocs();
        ArrayList<ArrayList<IndexValue>> adjacencyListsPDocTerm = new ArrayList<ArrayList<IndexValue>>();

        //Normalizing doc-term relations
        for (int doc = 0; doc < numDocs; doc++) {
            double grau = 0;
            ArrayList<IndexValue> adjList = adjancecyListsDocTerm.get(doc);
            for (int term = 0; term < adjList.size(); term++) {
                grau += adjList.get(term).getValue();
            }

            ArrayList<IndexValue> indexValues = new ArrayList<IndexValue>();
            if (grau != 0) {
                for (int term = 0; term < adjList.size(); term++) {
                    IndexValue indVal = new IndexValue(adjList.get(term).getIndex(), (adjList.get(term).getValue() / grau));
                    indexValues.add(indVal);
                }
            }
            adjacencyListsPDocTerm.add(indexValues);
        }

        int interval = 10;
        int nextInterval = interval;

        // Learning Algorithm
        System.out.print("- Aprendendo...");
        int numIt = 0;
        boolean exit = false;
        while (exit == false) {
            System.out.print("\n-- Iteração " + (numIt + 1) + " ");
            double meanError = 0;
            //Optimizing class information of terms considering labeled documents
            nextInterval = interval;
            for (int doc = 0; doc < numDocs; doc++) {
                ArrayList<IndexValue> neighbors = adjacencyListsPDocTerm.get(doc);
                double[] estimatedClasses = classifyInstance2(neighbors);
                for (int classe = 0; classe < numClasses; classe++) {
                    double error = yDoc[doc][classe] - estimatedClasses[classe];
                    meanError += (error * error) / (double) 2;
                    for (int term = 0; term < neighbors.size(); term++) {
                        double currentWeight = fTerms[neighbors.get(term).getIndex()][classe];
                        double newWeight = currentWeight + (errorCorrectionRate * neighbors.get(term).getValue() * error);
                        fTerms[neighbors.get(term).getIndex()][classe] = newWeight;
                    }
                }
                double perc = ((doc) / (double) numDocs) * 100;
                if (perc > nextInterval) {
                    System.out.print(".");
                    nextInterval += interval;
                }
            }
            numIt++;
            meanError = (double) meanError / (double) numDocs;

            // Analysis of stopping criteria
            if (numIt > 1) {
                if ((meanError < getMinError()) || numIt > getMaxNumberIterations()) {
                    exit = true;
                }
            }
        }

    }

    public void setMaxNumIterations(int maxNumberIterations) {
        this.maxNumberIterations = maxNumberIterations;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public void setErrorCorrectionRate(double errorCorrectionRate) {
        this.errorCorrectionRate = errorCorrectionRate;
    }

    public int getMaxNumberIterations() {
        return this.maxNumberIterations;
    }

    public double getMinError() {
        return this.minError;
    }

    public double getErrorCorrectionRate() {
        return this.errorCorrectionRate;
    }

    public double[] classifyInstance2(ArrayList<IndexValue> neighbors) {

        double[] classes = new double[numClasses];
        double total = 0;
        for (int term = 0; term < neighbors.size(); term++) {
            total += neighbors.get(term).getValue();
        }
        if (total == 0) {
            return classes;
        }

        for (int classe = 0; classe < numClasses; classe++) {
            double acmPesoClasse = 0;
            for (int term = 0; term < neighbors.size(); term++) {
                acmPesoClasse += neighbors.get(term).getValue() * getFTerm(neighbors.get(term).getIndex(), classe);
            }
            classes[classe] = acmPesoClasse;
        }

        double min = Double.MAX_VALUE;
        for (int classe = 0; classe < numClasses; classe++) {
            if (classes[classe] < min) {
                min = classes[classe];
            }
        }
        if (min < 0) {
            for (int classe = 0; classe < numClasses; classe++) {
                double value = classes[classe];
                classes[classe] = value + Math.abs(min);
            }
        }
        total = 0;
        for (int classe = 0; classe < numClasses; classe++) {
            total += classes[classe];
        }
        for (int classe = 0; classe < numClasses; classe++) {
            if (total == 0) {
                classes[classe] = 0;
            } else {
                double value = classes[classe];
                classes[classe] = value / total;
            }

        }
        return classes;
    }

    @Override
    public double[] distributionForInstance(InputPattern instance) throws Exception {
        return this.distributionForInstance(new SparseExample(super.inputPatternToListIndexValue(instance)));
    }

    /**
     * REVIEW
     * @param textInstance
     * @return
     * @throws Exception 
     */
    @Override   
    public int classifyInstance(InputPattern textInstance) throws Exception {
        return classifyInstance(new SparseExample(super.inputPatternToListIndexValue(textInstance)));
    }

    @Override
    public int classifyInstance(Example instance) throws Exception {
        return VectorOps.argmax(this.distributionForInstance(instance));
    }

    

    @Override
    public double[] distributionForInstance(Example instance) throws Exception {
        if (instance instanceof SparseExample) {
            SparseExample ex = (SparseExample) instance;
            return this.classifyInstance2((ArrayList<IndexValue>) ex.getListIndexValues());
        } else {
            throw new RuntimeException("It is allowed only to SparseExample instances!");
        }        
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
