/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.semisupervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Data;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import java.util.ArrayList;

/**
 *
 * @author root
 */
public class TCHN_DocTerm extends TextClassifier {

    private int numLabeledDocs; //Number of documents
    private int numUnlabeledDocs; //Number of documents
    private int numClasses; //Number of classes
    private int numTerms; //Number of terms

    private double errorCorrectionRate; // Error correction error
    private int maxNumberGlobalIterations; // Maximum Number of Global Iterations
    private int maxNumberLocalIterations; // Maximum Number of Local Iterations
    private double minError; // Minimum Mean Squared Error

    public TCHN_DocTerm(Data data, String learningType) {
        super(data, learningType);
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        System.out.println("- Alocando estrutura de dados...");

        numClasses = data.getNumClasses();
        numTerms = data.getNumTerms();

        ArrayList<Integer> listLabeledDocs = data.getListLabeledDocs();
        ArrayList<Integer> listUnlabeledDocs = data.getListUnlabeledDocs();

        numLabeledDocs = listLabeledDocs.size();
        numUnlabeledDocs = listUnlabeledDocs.size();

        double[][] fDocs = getRealClasses(data);
        double[][] fDocsTemp = getRealClasses(data);
        double[][] yDocs = getRealClasses(data);
        double[][] fTerms = getFTerms();

        //Adjacency lists to speed up learning
        ArrayList<ArrayList<IndexValue>> adjancecyListsDocTerm = data.getAdjListDocs();
        ArrayList<ArrayList<IndexValue>> adjacencyListsPDocTerm = new ArrayList<ArrayList<IndexValue>>();

        //Normalizing doc-term relations
        for (int doc = 0; doc < (numLabeledDocs + numUnlabeledDocs); doc++) {
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
        int numIterationsTotal = 0;
        while (exit == false) {
            System.out.print("\n-- Iteração " + (numIterationsTotal + 1) + " ");
            boolean exit2 = false;
            int numIterationsInternas = 0;
            //Optimizing class information of terms considering labeled documents
            while (exit2 == false) {
                double meanError = 0;
                nextInterval = interval;
                for (int doc = 0; doc < listLabeledDocs.size(); doc++) {
                    int idDoc = listLabeledDocs.get(doc);
                    ArrayList<IndexValue> neighbors = adjacencyListsPDocTerm.get(idDoc);
                    double[] estimatedClasses = classifyInstance2(neighbors);
                    for (int classe = 0; classe < numClasses; classe++) {
                        double error = yDocs[idDoc][classe] - estimatedClasses[classe];
                        meanError += (error * error) / (double) 2;
                        //Corrigindo os weights de cada um dos atributos conectados ao documento
                        for (int term = 0; term < neighbors.size(); term++) {
                            double currentWeight = fTerms[neighbors.get(term).getIndex()][classe];
                            double newWeight = currentWeight + (errorCorrectionRate * neighbors.get(term).getValue() * error);
                            fTerms[neighbors.get(term).getIndex()][classe] = newWeight;
                        }
                    }
                    double perc = ((doc) / (double) numLabeledDocs) * 100;
                    if (perc > nextInterval) {
                        System.out.print(".");
                        nextInterval += interval;
                    }
                }
                numIt++;
                numIterationsInternas++;
                meanError = (double) meanError / (double) numLabeledDocs;
                // Analysis of stopping criteria 
                if (numIterationsInternas > 1) {
                    if ((getMaxNumberLocalIterations() == numIterationsInternas) || (meanError < getMinError())) {
                        exit2 = true;
                    }
                }
            }

            //Propagating class information from terms to unlabeled documents
            for (int doc = 0; doc < numUnlabeledDocs; doc++) {
                int idDoc = listUnlabeledDocs.get(doc);
                fDocsTemp[idDoc] = classifyInstance2(adjacencyListsPDocTerm.get(idDoc));
            }

            //Analysis of stopping criteria
            double acmDif = 0;
            for (int doc = 0; doc < (numLabeledDocs + numUnlabeledDocs); doc++) {
                for (int classe = 0; classe < numClasses; classe++) {
                    acmDif += Math.abs(fDocsTemp[doc][classe] - fDocs[doc][classe]);
                    fDocs[doc][classe] = fDocsTemp[doc][classe];
                }
            }

            //Optimizing class information of terms considering the class information assigned to unlabeled documents
            exit2 = false;
            numIterationsInternas = 0;
            while (exit2 == false) {
                double meanError = 0;
                nextInterval = interval;
                for (int doc = 0; doc < numUnlabeledDocs; doc++) {
                    int idDoc = listUnlabeledDocs.get(doc);
                    ArrayList<IndexValue> neighbors = adjacencyListsPDocTerm.get(idDoc);;
                    double[] estimatedClasses = classifyInstanceReal(neighbors);
                    for (int classe = 0; classe < numClasses; classe++) {
                        double error = fDocs[idDoc][classe] - estimatedClasses[classe];
                        meanError += (error * error) / (double) 2;
                        for (int term = 0; term < neighbors.size(); term++) {
                            double currentWeight = fTerms[neighbors.get(term).getIndex()][classe];
                            double newWeight = currentWeight + (errorCorrectionRate * neighbors.get(term).getValue() * error);
                            fTerms[neighbors.get(term).getIndex()][classe] = newWeight;
                        }
                    }
                    double perc = ((doc) / (double) numUnlabeledDocs) * 100;
                    if (perc > nextInterval) {
                        System.out.print(".");
                        nextInterval += interval;
                    }
                }
                numIt++;
                numIterationsInternas++;
                meanError = (double) meanError / (double) numUnlabeledDocs;
                //Analysis of stopping criteria
                if (numIterationsInternas > 1) {
                    if ((getMaxNumberLocalIterations() == numIterationsInternas) || (meanError < getMinError())) {
                        exit2 = true;
                    }
                }
            }

            //Analysis of stopping criteria
            numIterationsTotal++;
            numIt++;
            if (numIterationsTotal > 1) {
                if ((acmDif == 0) || (getMaxNumberGlobalIterations() == numIterationsTotal)) {
                    exit = true;
                }
            }
        }
    }

    @Override
    public double[] distributionForInstance(InputPattern instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setMaxNumberGlobalIterations(int maxNumberGlobalIterations) {
        this.maxNumberGlobalIterations = maxNumberGlobalIterations;
    }

    public void setMaxNumberLocalIterations(int maxNumberLocalIterations) {
        this.maxNumberLocalIterations = maxNumberLocalIterations;
    }

    public void setMinError(double minError) {
        this.minError = minError;
    }

    public void setErrorCorrectionRate(double errorCorrectionRate) {
        this.errorCorrectionRate = errorCorrectionRate;
    }

    public Integer getMaxNumberGlobalIterations() {
        return maxNumberGlobalIterations;
    }

    public Integer getMaxNumberLocalIterations() {
        return maxNumberLocalIterations;
    }

    public double getMinError() {
        return this.minError;
    }

    public double getErrorCorrectionRate() {
        return this.errorCorrectionRate;
    }

    // Function to classify an instance through the propagation of class information of terms (soft classification)
    public double[] classifyInstanceReal(ArrayList<IndexValue> neighbors) {

        double[] classes = new double[numClasses];
        double total = 0;
        for (int term = 0; term < neighbors.size(); term++) {
            total += neighbors.get(term).getValue();
        }
        if (total == 0) {
            return classes;
        }

        for (int classe = 0; classe < numClasses; classe++) {
            double acmClassWeight = 0;
            for (int term = 0; term < neighbors.size(); term++) {
                acmClassWeight += neighbors.get(term).getValue() * getFTerm(neighbors.get(term).getIndex(), classe);
            }
            classes[classe] = acmClassWeight;
        }

        return classes;
    }

    // Function to return class confidences of the classification of an instance (soft classification)
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
            double acmClassWeight = 0;
            for (int term = 0; term < neighbors.size(); term++) {
                acmClassWeight += neighbors.get(term).getValue() * getFTerm(neighbors.get(term).getIndex(), classe);
            }
            classes[classe] = acmClassWeight;
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
    public int classifyInstance(InputPattern textInstance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int classifyInstance(ArrayList<IndexValue> instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] distributionForInstance(ArrayList<IndexValue> instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
