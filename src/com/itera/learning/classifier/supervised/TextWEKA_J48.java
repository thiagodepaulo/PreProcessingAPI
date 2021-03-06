/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier.supervised;

import com.itera.learning.classifier.TextClassifier;
import com.itera.structures.Conversor;
import com.itera.structures.Data;
import com.itera.structures.Example;
import com.itera.structures.TextData;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import com.itera.structures.SparseExample;
import java.util.List;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

/**
 *
 * @author root
 */
public class TextWEKA_J48 extends TextClassifier {

    private J48 tree;
    private Instances instances;

    public TextWEKA_J48(TextData data) {
        super(data, "Supervised");
        tree = new J48();

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
    public void buildClassifier(TextData data) throws Exception {
        this.instances = Conversor.textDataToArff(data);
        tree.buildClassifier(instances);
    }

    @Override
    public int classifyInstance(Example instance) throws Exception {
        Instance inst = new SparseInstance(this.terms_ids.size());
        for (IndexValue iv : ((SparseExample) instance).getListIndexValues()) {
            inst.setValueSparse(iv.getIndex(), iv.getValue());
        }
        this.instances.add(inst);
        return (int) tree.classifyInstance(instances.lastInstance());
    }

    @Override
    public double[] distributionForInstance(Example instance) throws Exception {
        Instance inst = new SparseInstance(this.terms_ids.size());
        for (IndexValue iv : ((SparseExample) instance).getListIndexValues()) {
            inst.setValueSparse(iv.getIndex(), iv.getValue());
        }
        this.instances.add(inst);
        return tree.distributionForInstance(instances.lastInstance());
    }

    public static void main(String args[]) {
        Instance inst = new SparseInstance(3);
        inst.setValueSparse(5, 3.);
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
