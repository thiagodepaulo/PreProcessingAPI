//*****************************************************************************
// Author: Rafael Geraldeli Rossi
// E-mail: rgr.rossi at gmail com
// Last-Modified: January 29, 2015
// Description: 
//*****************************************************************************  
package com.itera.structures;

import java.io.Serializable;

public final class IndexValue implements Serializable {

    private Integer index;
    private Double value;

    public IndexValue() {
        setIndex(-1);
        setValue(-1.0);
    }

    public IndexValue(Integer index, Double value) {
        setIndex(index);
        setValue(value);
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getIndex() {
        return this.index;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "(" + this.index + ", " + this.value + ")";
    }
}
