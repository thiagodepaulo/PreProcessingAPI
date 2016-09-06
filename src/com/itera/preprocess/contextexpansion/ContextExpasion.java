/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.preprocess.contextexpansion;

import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.structures.InputPattern;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class ContextExpasion {

    private static JavaWord2Vec w2v = null;
    public static String w2vDir = null;
    public static int numSimilarities = 3;

    public static List<InputPattern> expand(List<InputPattern> data, PreProcessingConfig config) {
        for(InputPattern input: data) {
            String[] words = input.getTexto().split("\\W+");
            ArrayList<String> l = new ArrayList<>();
            for(String word: words) {
                w2v.
            }
        }
    }

    private JavaWord2Vec loadW2V() throws IOException {
        if (w2v == null) {
            w2v = JavaWord2Vec.load(w2vDir);
        }
        return w2v;
    }

}
