/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.Classifier;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.Example;
import com.itera.structures.Feature;
import com.itera.util.VectorOps;
import java.util.ArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author root
 */
public class WekaClassifier implements Classifier{

    private final AbstractClassifier wekaClassifier;       
    private Instances instances;
    private ArrayList<Feature> features;

    public WekaClassifier(AbstractClassifier cls) {
        this.wekaClassifier = cls;                
    }
    
    @Override
    public void buildClassifier(Data data) throws Exception {
        Instances inst = Conversor.dataToArff(data);
        wekaClassifier.buildClassifier(inst);     
        this.instances = inst;    
        features = new ArrayList<>();
        for(int i=0; i< data.numFeatures(); i++) {
            features.add(data.getFeature(i));
        }
    }

    @Override
    public int classifyInstance(Example instance) throws Exception {
        Instance inst = new DenseInstance(features.size());
        inst.setDataset(this.instances);
        for(int i=0; i< this.features.size(); i++) {
            if(this.features.get(i).getType() == Feature.FeatureType.NOMINAL) {
                inst.setValue(i, (String) instance.getValue(i));
            } else {
                inst.setValue(i, (double) instance.getValue(i));
            }
        }
        return (int)this.wekaClassifier.classifyInstance(inst);
    }

    @Override
    public double[] distributionForInstance(Example instance) throws Exception {
        Instance inst = new DenseInstance(features.size());
        inst.setDataset(this.instances);
        for(int i=0; i< this.features.size(); i++) {
            if(this.features.get(i).getType() == Feature.FeatureType.NOMINAL) {
                inst.setValue(i, (String) instance.getValue(i));
            } else {
                inst.setValue(i, (double) instance.getValue(i));
            }
        }
        return this.wekaClassifier.distributionForInstance(inst);
    }
    
}
