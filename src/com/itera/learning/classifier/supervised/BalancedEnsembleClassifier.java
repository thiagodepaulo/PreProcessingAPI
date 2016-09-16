/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Data;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import java.util.List;

/**
 *
 * @author root
 */
public class BalancedEnsembleClassifier extends TextClassifier {

    public BalancedEnsembleClassifier(Data data) {
        super(data, "supervised");
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
    public void buildClassifier(Data data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int classifyInstance(List<IndexValue> instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] distributionForInstance(List<IndexValue> instance) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Data[] splitTrainData(Data data) {
        
        int numClass = data.getNumClasses();
        int[][] dataClassDocs = new int[numClass][];
        int[] numDocsPerClass = data.getNumDocsPerClasses();
        for(int i=0; i<numClass; i++)
            dataClassDocs[i] = new int[numDocsPerClass[i]];
        for(int docId: data.getDocsIds()) {
            int classId = data.getClassDocument(docId);
            
        }
        return null;
    }
    
}
