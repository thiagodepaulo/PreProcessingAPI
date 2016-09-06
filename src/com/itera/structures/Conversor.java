/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import com.itera.preprocess.config.PreProcessingConfig;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author root
 */
public class Conversor {

    public static Data listInputPatternToData(List<InputPattern> lInput, PreProcessingConfig config) {
        HashMap<String, Integer> classesIds = new HashMap<>();
        HashMap<Integer, Integer> classesDocs = new HashMap<>();
        HashMap<Integer, Double> termDf = new HashMap<>();
        HashMap<String, Integer> wordIds = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> allDocTermFreq = new HashMap<>();
        int nDocs = lInput.size();
        for (InputPattern input : lInput) {
            if (!classesIds.containsKey(input.getClasse())) {
                classesIds.put(input.getClasse(), classesIds.size());
            }
            classesDocs.put(input.getId(), classesIds.get(input.getClasse()));
            String[] words = input.getTexto().split("\\W+");
            HashMap<Integer, Double> docTermf = new HashMap<>();
            for (String word : words) {
                // freq by document
                int wid = -1;
                if (wordIds.containsKey(word)) {
                    wid = wordIds.get(word);
                } else {
                    wid = wordIds.size();
                    wordIds.put(word, wid);
                }
                if (docTermf.containsKey(wid)) {
                    docTermf.put(wid, docTermf.get(wid) + 1.);
                } else {
                    docTermf.put(wid, 1.);
                }
                // freq for all doc collection
                if (termDf.containsKey(wid)) {
                    termDf.put(wid, termDf.get(wid) + 1.);
                } else {
                    termDf.put(wid, 1.);
                }
            }
            allDocTermFreq.put(input.getId(), docTermf);
        }
        // calc TF-IDF
        if (config.isTfidf()) {
            for (int docId : allDocTermFreq.keySet()) {
                for (int wordId : allDocTermFreq.get(docId).keySet()) {
                    double freq = allDocTermFreq.get(docId).get(wordId);
                    freq = freq * (Math.log10((double) nDocs / (1 + termDf.get(wordId))));
                    allDocTermFreq.get(docId).put(wordId, freq);
                }
            }
        }

        Data data = new Data();
        String[] classes = new String[classesIds.size()];
        for (Map.Entry<String, Integer> entryClasses : classesIds.entrySet()) {
            classes[entryClasses.getValue()] = entryClasses.getKey();
        }
        data.setClasses(new ArrayList<>(Arrays.asList(classes)));
        data.setClassesDocuments(classesDocs);
        HashMap<String, Integer> docsIds = new HashMap<>();
        ArrayList<IndexValue>[] documents = new ArrayList[nDocs];
        for (int docId : allDocTermFreq.keySet()) {
            docsIds.put("" + docId, docId);
            ArrayList<IndexValue> docAdjList = new ArrayList<>();
            for (int wordId : allDocTermFreq.get(docId).keySet()) {
                docAdjList.add(new IndexValue(wordId, allDocTermFreq.get(docId).get(wordId)));
            }
            documents[docId] = docAdjList;
        }
        data.setDocsIDs(docsIds);
        data.setDocuments(new ArrayList<>(Arrays.asList(documents)));
        data.setIDsDocs(invertHashMap(docsIds));
        data.setIDsTerms(invertHashMap(wordIds));
        data.setTermsIDs(wordIds);
        data.setMapTerms_CompleteTerms(null);
        return data;
    }

    public static String dataToArff(Data data) {
        StringBuilder sb = new StringBuilder();
        String nl = "\n";

        sb.append("@RELATION IteraDATA");
        sb.append(nl);
        sb.append(nl);
        for (String word : data.getTerms()) {
            sb.append("@ATTRIBUTE " + word + "  REAL" + nl);
        }
        sb.append("@ATTRIBUTE class  {" + String.join(", ", data.getClasses()) + "}");
        sb.append(nl);
        sb.append(nl);
        sb.append(nl);
        sb.append("@DATA\n");
        for (int docId = 0; docId < data.getNumDocs(); docId++) {
            String[] sFreqs = new String[data.getNumTerms()];
            for (int wid = 0; wid < data.getNumTerms(); wid++) {
                sFreqs[wid] = "0";
            }
            int classId = data.getClassDocument(docId);

            for (IndexValue iv : data.getAdjListDoc(docId)) {
                sFreqs[iv.getIndex()] = Double.toString(iv.getValue());
            }
            sb.append(String.join(",", sFreqs) + ", " + data.getClasses().get(classId));
            sb.append(nl);
        }
        return sb.toString();
    }

    public static <T, P> HashMap<P, T> invertHashMap(HashMap<T, P> map) {
        HashMap<P, T> invMap = new HashMap<>();
        for (Map.Entry<T, P> entry : map.entrySet()) {
            invMap.put(entry.getValue(), entry.getKey());
        }
        return invMap;
    }

    public static void main(String[] args) {

    }
}
