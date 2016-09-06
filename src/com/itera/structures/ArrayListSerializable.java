/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Dhiogo
 */
public class ArrayListSerializable<E> extends ArrayList<E> implements Serializable {

    public ArrayListSerializable(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListSerializable() {
    }

    public ArrayListSerializable(Collection<? extends E> c) {
        super(c);
    }

    
    
    
}
