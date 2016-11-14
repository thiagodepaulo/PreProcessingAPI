/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.learning.classifier;

import com.itera.structures.Data;
import com.itera.structures.TextData;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import com.itera.util.Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author root
 */
public abstract class TextClassifier implements Classifier {

    private static final long serialVersionUID = -1618130927443918035L;

    protected double[][] fTerms; // modelo de classificação - peso de cada termo
    // para cada classe

    protected ArrayList<String> classes;
    protected HashMap<String, Integer> terms_ids;

    protected String learningType;

    protected List<String> outputType;

    private final String splitPattern = "\\s+";

    public TextClassifier(TextData data, String learningType) {
        this.classes = data.getClasses();
        this.terms_ids = data.getTermsIDs();
        this.fTerms = new double[data.getNumTerms()][data.getNumClasses()];
        this.learningType = learningType;
    }

    public void setLearningType(String learningType) {
        this.learningType = learningType;
    }

    public void setFTerms(double[][] fTerms) {
        this.fTerms = fTerms;
    }

    public void setTermsIDs(HashMap<String, Integer> terms_ids) {
        this.terms_ids = terms_ids;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    public int getClassId(String className) {
        for (int i = 0; i < this.classes.size(); i++) {
            if (this.classes.get(i).equals(className)) {
                return i;
            }
        }
        return -1;
    }

    public double[][] getRealClasses(TextData data) {
        double[][] yDoc = new double[data.getNumDocs()][data.getNumClasses()];
        for (int doc = 0; doc < data.getNumDocs(); doc++) {
            int classe = data.getClassDocument(doc);
            if (classe != -1) {
                yDoc[doc][classe] = 1;
            }
        }
        return yDoc;
    }

    public double[][] getFTerms() {
        return this.fTerms;
    }

    public void setfTerms(double[][] fTerms) {
        this.fTerms = fTerms;
    }

    public HashMap<String, Integer> getTerms_ids() {
        return terms_ids;
    }

    public void setTerms_ids(HashMap<String, Integer> terms_ids) {
        this.terms_ids = terms_ids;
    }

    public double getFTerm(int idTerm, int idClass) {
        return this.fTerms[idTerm][idClass];
    }

    public String getClassName(int classId) {
        return this.classes.get(classId);
    }

    public ArrayList<String> getClasses() {
        return this.classes;
    }

    public InputPattern listIndexValueToInputPattern(List<IndexValue> lInput) {
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, String> ids_terms = Tools.invertHashMap(terms_ids);
        String blank = " ";
        for (IndexValue iv : lInput) {
            for (int i = 0; i < iv.getValue(); i++) {
                sb.append(ids_terms.get(iv.getIndex()));
                sb.append(blank);
            }
        }
        return new InputPattern(0, sb.toString(), "");
    }

    public List<IndexValue> inputPatternToListIndexValue(InputPattern input) {
        String[] words = input.getTexto().split(this.splitPattern);
        int wid;
        List<IndexValue> linput = new ArrayList<>();
        HashMap<Integer, Double> map = new HashMap<>();
        for (String w : words) {
            w = w.trim();
            if (terms_ids.containsKey(w)) {
                wid = terms_ids.get(w);
                if (map.containsKey(wid)) {
                    map.put(wid, map.get(wid) + 1.);
                } else {
                    map.put(wid, 1.);
                }
            }
        }
        for (int wwid : map.keySet()) {
            linput.add(new IndexValue(wwid, map.get(wwid)));
        }
        return linput;
    }
    
    public abstract void buildClassifier(TextData data) throws Exception;

    public abstract int classifyInstance(InputPattern textInstance) throws Exception;

    public abstract double[] distributionForInstance(InputPattern textInstance) throws Exception;

}
