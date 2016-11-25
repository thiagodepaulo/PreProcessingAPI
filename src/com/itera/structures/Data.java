/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author root
 */
public interface Data {
    
    public Example getExample(int exId);
    
    public Feature getFeature(int position); 
    
    public int numExamples();
    
    public int numFeatures();
    
    public void setClassIndex(int idx);
    
    public void setLastAsClassIndex();
    
    public int getClassIndex();
    
    public int getNumClasses();
    
    public Iterator<? extends Example> itrExamples();
    
    public ArrayList<Feature> listFeatures();    
    
    public String getDataName();
        
    public void stratify(int numFolds); 
    
    public Data trainCV(int numFolds, int numFold);
    
    public Data testCV(int numFolds, int numFold);
    
}
