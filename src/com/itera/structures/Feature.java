/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.util.Objects;

/**
 *
 * @author root
 */
public class Feature {

    public enum FeatureType {
        NUMERIC, NOMINAL;
    }
    
    public String[] categories;

    private FeatureType type;

    private String featureName;

    public Feature(FeatureType type, String featureName, String[] categories) {
        this.type = type;
        this.featureName = featureName;
        this.categories = categories;
    }

    public Feature(String featureName) {
        this(FeatureType.NUMERIC, featureName, null);
    }

    public FeatureType getType() {
        return type;
    }

    public String getFeatureName() {
        return featureName;
    }

    public double valIndex(String value) { 
        if (this.categories == null) 
            return -1;
        for (int i = 0; i < this.categories.length; i++) {
            if (this.categories[i].equals(value)) {
                return i;
            }
        }
        return -1;                
    }

    public String[] getCategories() {
        if (this.type == FeatureType.NOMINAL) {
            return this.categories;
        } else {
            throw new RuntimeException("Not a nominal Feature!");
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.featureName);
        return hash;
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature) obj;
        if (this.type != other.type) {
            return false;
        }
        if (!this.featureName.equals(other.featureName)) {
            return false;
        }
        return true;
    }

}
