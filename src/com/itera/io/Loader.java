/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.io;

import com.itera.structures.DenseData;
import com.itera.structures.InputPattern;
import java.util.List;

/**
 *
 * @author root
 */
public interface Loader {
    
    public List<InputPattern> loadTextualData();
    
    public DenseData loadDenseData();

}
