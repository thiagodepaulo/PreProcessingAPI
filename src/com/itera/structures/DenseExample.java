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
public class DenseExample implements Example {

    public double[] values;
    public DenseData dataset;

    public DenseExample(DenseData data) {
        this.values = new double[data.numFeatures()];
        this.dataset = data;
    }

    public DenseExample(int numFeatures) {
        this.values = new double[numFeatures];
        this.dataset = null;
    }

    @Override
    public int getNumFeatures() {
        return this.values.length;
    }

    public void setValue(int index, String value) {
        if (dataset != null) {
            Feature feature = dataset.getFeature(index);
            if (feature.getType() != Feature.FeatureType.NOMINAL) {
                throw new RuntimeException("Attribute is not nominal!");
            }
            double valIndex = feature.valIndex(value);
            if (valIndex == -1) {
                throw new RuntimeException("Value not defined for given nominal attribute!");
            }
            setValue(index, valIndex);
        } else {
            throw new RuntimeException("Attribute is not nominal!");
        }
    }

    public Object getValue(int index) {
        if (this.dataset != null) {
            Feature feature = dataset.getFeature(index);
            if (feature.getType() == Feature.FeatureType.NOMINAL) {
                return feature.getCategories()[(int) values[index]];
            } else if (feature.getType() == Feature.FeatureType.NUMERIC) {
                return values[index];
            }
        }
        return values[index];
    }

    public void setValue(int index, double value) {
        
        if (this.getNumFeatures() <= index) {
            throw new RuntimeException("Feature index out of bound exception!");
        }
        this.values[index] = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int i = 0;
        Feature feat;
        for (; i < this.getNumFeatures() - 1; i++) {            
            if (dataset != null && dataset.getFeature(i).getType() == Feature.FeatureType.NOMINAL) {
                sb.append(dataset.getFeature(i).categories[(int) this.values[i]]);
            } else {
                sb.append(this.values[i]);
            }
            sb.append(", ");
        }
        
        if (dataset != null && dataset.getFeature(i).getType() == Feature.FeatureType.NOMINAL) {
            sb.append(dataset.getFeature(i).categories[(int) this.values[i]]);
        } else {
            sb.append(this.values[i]);
        }
        sb.append(")");
        return sb.toString();
    }

}
