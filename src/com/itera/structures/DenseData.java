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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}
