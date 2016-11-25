/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author root
 */
public class DenseData implements Data {

    private ArrayList<DenseExample> examples;

    private HashMap<Integer, Feature> idxFeatures;

    private int classIndex = -1;

    private String datasetName;

    public DenseData(String datasetName, ArrayList<Feature> lFeatures, int classIndex) {
        this.datasetName = datasetName;
        this.idxFeatures = new HashMap<>();
        // set idxFeatures
        if (lFeatures != null) {
            for (int i = 0; i < lFeatures.size(); i++) {
                idxFeatures.put(i, lFeatures.get(i));
            }
        }
        this.classIndex = classIndex;
        this.examples = new ArrayList<>();
    }

    public DenseData(String datasetName) {
        this(datasetName,null, -1);                        
    }
    
    @Override
    public int getClassIndex() {
        return this.classIndex;
    }
    
    @Override
    public String getDataName() {
        return this.datasetName;
    }

    public boolean addExample(DenseExample example) {
        return examples.add(example);
    }

    @Override
    public int numFeatures() {
        return idxFeatures.size();
    }

    public void setFeatures(ArrayList<Feature> lFeatures) {
        for (int i = 0; i < lFeatures.size(); i++) {
            idxFeatures.put(i, lFeatures.get(i));
        }
    }

    public Feature getFeature(int position) {
        return this.idxFeatures.get(position);
    }
    
    public void setLastAsClassIndex() {
        this.setClassIndex(this.numFeatures() - 1);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(DenseExample ex: this.examples) {
            sb.append(ex.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        Feature f1 = new Feature("idade");
        Feature f2 = new Feature(Feature.FeatureType.NOMINAL, "job", new String[]{"admin.","blue-collar","entrepreneur","housemaid","management","retired"});
        Feature f3 = new Feature(Feature.FeatureType.NOMINAL, "classe", new String[]{"y", "n"});
        
        ArrayList<Feature> lFeatures = new ArrayList<>();
        lFeatures.add(f1);
        lFeatures.add(f2);
        lFeatures.add(f3);
        
        DenseData data = new DenseData("Idade e Job", lFeatures, 0);            
        data.setClassIndex(2);
        
        DenseExample ex1 = new DenseExample(data);
        ex1.setValue(0, 50);
        ex1.setValue(1, "admin.");
        ex1.setValue(2, "y");
        
        DenseExample ex2 = new DenseExample(data);
        ex2.setValue(0, 28);
        ex2.setValue(1, "blue-collar");
        ex2.setValue(2, "n");
        
        data.addExample(ex1);
        data.addExample(ex2);
        
        System.out.println(data);
    }

    @Override
    public Example getExample(int exId) {
        return this.examples.get(exId);
    }

    @Override
    public int numExamples() {
        return examples.size();
    }

    @Override
    public void setClassIndex(int idx) {
        this.classIndex = idx;
    }
    
    @Override
    public Iterator<? extends Example> itrExamples() {        
        return examples.iterator(); 
    }
    
    @Override
    public ArrayList<Feature> listFeatures() {
        ArrayList<Feature> features = new ArrayList<>(this.numFeatures());
        for(int idxFeat: this.idxFeatures.keySet()) {
            features.add(idxFeat, this.idxFeatures.get(idxFeat));
        }
        return features; 
    }

    @Override
    public int getNumClasses() {
        return this.idxFeatures.get(this.classIndex).categories.length;
    }
    
    /**
     * Stratifies a set of instances according to its class values if the class
     * attribute is nominal (so that afterwards a stratified cross-validation
     * can be performed).
     *
     * @param numFolds the number of folds in the cross-validation
     * @throws UnassignedClassException if the class is not set
     */
    public void stratify(int numFolds) {

        if (numFolds <= 1) {
            throw new IllegalArgumentException(
                    "Number of folds must be greater than 1");
        }

        // sort by class
        int index = 1;
        int numEx = this.numExamples();
        int idEx1, idEx2;
        while (index < numEx) {
            idEx1 = index - 1;
            for (int j = index; j < numEx; j++) {
                idEx2 = j;
                if (this.getClassEx(idEx1) == this.getClassEx(idEx2)) {
                    swap(index, j);
                    index++;
                }
            }
            index++;
        }
        stratStep(numFolds);
    }
    
    public void swap(int id1, int id2) {
        DenseExample ex1 = this.examples.get(id1);
        DenseExample ex2 = this.examples.get(id2);
        
        this.examples.set(id1, ex2);
        this.examples.set(id2, ex1);
    }
    
     /**
     * Help function needed for stratification of set.
     *
     * @param numFolds the number of folds for the stratification
     */
    protected void stratStep(int numFolds) {

        int numEx = this.numExamples();
        ArrayList<DenseExample> newEx = new ArrayList<>(numEx);        
        int start = 0, j;
        
        // create stratified batch
        while (newEx.size() < numEx) {
            j = start;
            while (j < numEx) {
                newEx.add(this.examples.get(j));                                
                j = j + numFolds;
            }
            start++;
        }
        this.examples = newEx;        
    }
    
    private int getClassEx(int exId) {
        return (int)this.examples.get(exId).getNumericValue(this.classIndex);
    }

    @Override
    public Data trainCV(int numFolds, int numFold) {
        int numInstForFold, first, offset;
        DenseData train;
        int numEx = this.numExamples();

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > numEx) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }
        numInstForFold = numEx / numFolds;
        if (numFold < numEx % numFolds) {
            numInstForFold++;
            offset = numFold;
        } else {
            offset = numEx % numFolds;
        }
        train = new DenseData("TRAIN_"+this.datasetName, this.listFeatures(), this.classIndex);
        first = numFold * (numEx / numFolds) + offset;
        copyInstances(0, train, first);
        copyInstances(first + numInstForFold, train, numEx - first
                - numInstForFold);

        return train;
    }   

    @Override
    public Data testCV(int numFolds, int numFold) {
                int numInstForFold, first, offset;
        DenseData test;
        int numEx = this.numExamples();

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > numEx) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }
        numInstForFold = numEx / numFolds;
        if (numFold < numEx % numFolds) {
            numInstForFold++;
            offset = numFold;
        } else {
            offset = numEx % numFolds;
        }
        test = new DenseData("TEST_"+this.datasetName, this.listFeatures(), this.classIndex);
        first = numFold * (numEx / numFolds) + offset;
        copyInstances(first, test, numInstForFold);
        return test;
    }
    
     public void copyInstances(int inic, DenseData data, int end) {        
        
        int lastDocId = data.numExamples();
        for (int i = inic; i < inic + end; i++) {
            data.examples.add(lastDocId, this.examples.get(i));
            lastDocId++;
        }
    }

}
