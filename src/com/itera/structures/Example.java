/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

/**
 *
 * @author root
 */
public interface Example {
    
    public int getNumFeatures();

    public Object getValue(int index);
    
    public void setValue(int index, double value);
    
    public void setValue(int index, String value);
    
        
}
