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
import java.util.List;
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
    // it is used to replace missing values by zero
    private final double[] zeros;
    private String option;

    public WekaClassifier(AbstractClassifier wekaClassifier, String option, Data data) throws Exception {
        super(data, "supervised");
        this.wekaClassifier = wekaClassifier;
        this.wekaClassifier.setOptions(Utils.splitOptions(option));
        this.zeros = new double[data.getNumTerms() + 1];
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }    

    private Instance indexValueToInstance(List<IndexValue> input) {
        // sparse instance. +1 by class index
        Instance inst = new SparseInstance(this.terms_ids.size() + 1);
        inst.setDataset(this.instances);
        for (IndexValue iv : input) {
            inst.setValue(iv.getIndex(), iv.getValue());
        }
        inst.replaceMissingValues(zeros);
        return inst;
    }

    @Override
    public int classifyInstance(InputPattern textInstance) throws Exception {
        List<IndexValue> linput = inputPatternToListIndexValue(textInstance);
        return classifyInstance(linput);
    }

    @Override
    public double[] distributionForInstance(InputPattern textInstance) throws Exception {
        List<IndexValue> linput = inputPatternToListIndexValue(textInstance);
        return distributionForInstance(linput);
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        this.instances = Conversor.dataToArff(data);
        wekaClassifier.buildClassifier(instances);
    }

    @Override
    public int classifyInstance(List<IndexValue> input) throws Exception {
        Instance instance = indexValueToInstance(input);
        return (int) this.wekaClassifier.classifyInstance(instance);
    }

    @Override
    public double[] distributionForInstance(List<IndexValue> input) throws Exception {
        Instance instance = indexValueToInstance(input);
        return this.wekaClassifier.distributionForInstance(instance);
    }

}
