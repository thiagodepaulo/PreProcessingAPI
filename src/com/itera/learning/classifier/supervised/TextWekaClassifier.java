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
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.Utils;

/**
 *
 * @author root
 */
public class TextWekaClassifier extends TextClassifier {

    private final AbstractClassifier wekaClassifier;
    private Instances instances;
    // it is used to replace missing values by zero
    private final double[] zeros;
    private String option;

    public TextWekaClassifier(AbstractClassifier wekaClassifier, String option, TextData data) throws Exception {
        super(data, "supervised");
        this.wekaClassifier = wekaClassifier;
        this.wekaClassifier.setOptions(Utils.splitOptions(option));
        this.zeros = new double[data.getNumTerms() + 1];
        this.option = option;
    }

    public AbstractClassifier getWekaClassifier() {
        return this.wekaClassifier;
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
        return classifyInstance(new SparseExample(linput));
    }

    @Override
    public double[] distributionForInstance(InputPattern textInstance) throws Exception {
        List<IndexValue> linput = inputPatternToListIndexValue(textInstance);
        return distributionForInstance(new SparseExample(linput));
    }

    @Override
    public void buildClassifier(TextData data) throws Exception {
        this.instances = Conversor.textDataToArff(data);
        wekaClassifier.buildClassifier(instances);
    }

    @Override
    public int classifyInstance(Example input) throws Exception {
        if (input instanceof SparseExample) {
            SparseExample ex = (SparseExample) input;
            Instance instance = indexValueToInstance(ex.getListIndexValues());
            return (int) this.wekaClassifier.classifyInstance(instance);
        } else {
            throw new RuntimeException("It is allowed only to SparseExample instances!");
        }
    }

    @Override
    public double[] distributionForInstance(Example input) throws Exception {
        if (input instanceof SparseExample) {
            SparseExample ex = (SparseExample) input;
            Instance instance = indexValueToInstance(ex.getListIndexValues());
            return this.wekaClassifier.distributionForInstance(instance);
        } else {
            throw new RuntimeException("It is allowed only to SparseExample instances!");
        }
    }

    @Override
    public void buildClassifier(Data data) throws Exception {
        this.buildClassifier((TextData)data);
    }

}
