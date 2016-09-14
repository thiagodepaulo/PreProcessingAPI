/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import java.util.ArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 *
 * @author root
 */
public class WekaClassifier extends TextClassifier {

    private final AbstractClassifier wekaClassifier;
    private Instances instances;

    public WekaClassifier(AbstractClassifier wekaClassifier, String option, Data data) throws Exception {
        super(data, "Weka supervised");
        this.wekaClassifier = wekaClassifier;
        this.wekaClassifier.setOptions(Utils.splitOptions(option));
    }

    private Instance indexValueToInstance(ArrayList<IndexValue> input) {
        Instance inst = new SparseInstance(this.terms_ids.size());        
        inst.setDataset(this.instances);        
        for (int i=0; i< this.terms_ids.size() ; i++)
            inst.setValue(i, 0);
        for (IndexValue iv : input) {
            inst.setValue(iv.getIndex(), iv.getValue());            
        }        
        return inst;
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
        this.instances = Conversor.dataToArff(data);
        wekaClassifier.buildClassifier(instances);
    }

    @Override
    public int classifyInstance(ArrayList<IndexValue> input) throws Exception {
        Instance instance = indexValueToInstance(input);
        return (int) this.wekaClassifier.classifyInstance(instance);
    }

    @Override
    public double[] distributionForInstance(ArrayList<IndexValue> input) throws Exception {
        Instance instance = indexValueToInstance(input);
        return this.wekaClassifier.distributionForInstance(instance);
    }

}
