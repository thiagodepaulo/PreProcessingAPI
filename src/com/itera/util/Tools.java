/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author root
 */
public class Tools {

    public static <T, P> HashMap<P, T> invertHashMap(HashMap<T, P> map) {
        HashMap<P, T> invMap = new HashMap<>();
        for (Map.Entry<T, P> entry : map.entrySet()) {
            invMap.put(entry.getValue(), entry.getKey());
        }
        return invMap;
    }

    public static int argmax(double[] a) {
        double max = a[0];
        int idx_max = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
                idx_max = i;
            }
        }
        return idx_max;
    }

}
