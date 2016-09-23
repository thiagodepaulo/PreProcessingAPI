/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.preprocess.contextexpansion;

import com.itera.structures.InputPattern;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.itera.util.Tools.join;
/**
 *
 * @author root
 */
public class ContextExpasion {

    private static JavaWord2Vec w2v = null;
    public static String w2vDir = "/media/thiagodepaulo/Dados/Thiago/wordEmbedding/wiki2vec/wiki2vec/out";
    public static int numSimilarities = 10;

    public ContextExpasion() {
        try {
            ContextExpasion.w2v = loadW2V();
        } catch (IOException ex) {
            Logger.getLogger(ContextExpasion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ContextExpasion(String w2vDir, int numSimilarities) {
        ContextExpasion.w2vDir = w2vDir;
        ContextExpasion.numSimilarities = numSimilarities;
        try {
            ContextExpasion.w2v = loadW2V();
        } catch (IOException ex) {
            Logger.getLogger(ContextExpasion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public InputPattern expand(InputPattern input) {
        // number of similarities words
        ContextExpasion.w2v.topN = numSimilarities;

        String[] words = input.getTexto().trim().split("\\s+");
        List<Pair<String, float[]>> l = ContextExpasion.w2v.mostSimilar(words);
        List<String> appended = new ArrayList<>(ContextExpasion.numSimilarities);
        for (Pair<String, float[]> p : l) {
            appended.add(p._1);
        }
        return new InputPattern(input.getId(),
                input.getTexto() + " " + join(" ", appended), input.getClasse());
    }

    public static List<InputPattern> expand(List<InputPattern> data) {
        try {
            w2v = loadW2V();
        } catch (IOException ex) {
            Logger.getLogger(ContextExpasion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            return data;
        }
        // number of similarities words
        w2v.topN = numSimilarities;
        List<InputPattern> newData = new ArrayList<>();
        for (InputPattern input : data) {
            String[] words = input.getTexto().split("\\s+");
            List<Pair<String, float[]>> l = w2v.mostSimilar(words);
            // do not contain words in W2Vec vocabulary (usually the wikipedia vocabulary)
            if (l == null) {
                newData.add(input);
                continue;
            }
            List<String> appended = new ArrayList<>(numSimilarities);
            for (Pair<String, float[]> p : l) {
                appended.add(p._1);
            }
            newData.add(new InputPattern(input.getId(),
                    input.getTexto() + " " + join(" ", appended), input.getClasse()));
        }
        return newData;
    }

    private static JavaWord2Vec loadW2V() throws IOException {
        if (w2v == null) {
            w2v = JavaWord2Vec.load(w2vDir);
        }
        return w2v;
    }

}
