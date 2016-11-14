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
    
    public int getClassIndex();
    
    public Iterator<? extends Example> itrExamples(); 
    
    public ArrayList<Feature> listFeatures();    
    
    public String getDataName();
    
}
