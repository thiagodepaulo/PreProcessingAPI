/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class SparseExample implements Example {
    
    private List<IndexValue> instance;    
    
    public SparseExample() {
        this(new ArrayList<IndexValue>());
    }
    
    public SparseExample(List<IndexValue> instance) {
        this.instance = instance;
    }
    
    public void add(IndexValue iv) {
        instance.add(iv);
    }
    
    public IndexValue search(int index) {
        for(IndexValue iv: instance) {
            if (iv.getIndex() == index)
                return iv;
        }
        return null;
    }

    @Override
    public int getNumFeatures() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<IndexValue> getListIndexValues() {
        return this.instance;
    }
    
    public void setListIndexValues(ArrayList<IndexValue> instance) {
        this.instance = instance;
    }

    @Override
    public Object getValue(int index) {
        return search(index).getValue();
    }

    @Override
    public void setValue(int index, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(int index, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
