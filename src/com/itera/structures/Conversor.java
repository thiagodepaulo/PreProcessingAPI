/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.util.Tools;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

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
            String[] words = input.getTexto().split("\\s+");
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
                    freq = freq * (1 + (Math.log10((double) nDocs / (1 + termDf.get(wordId)))));
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
        data.setIDsDocs(Tools.invertHashMap(docsIds));
        data.setIDsTerms(Tools.invertHashMap(wordIds));
        data.setTermsIDs(wordIds);
        data.setMapTerms_CompleteTerms(null);

        return data;
    }

    public static Data arffToData(String arffArqName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arffArqName));
            Instances arff = new Instances(reader);
            reader.close();

            if (arff.classIndex() < 0) {
                arff.setClassIndex(arff.numAttributes() - 1);
            }

            int numAttr = arff.numAttributes();
            int numCls = arff.numClasses();
            int numInsts = arff.numInstances();
            int clsIdx = arff.classIndex();

            Data data = new Data();
            ArrayList<String> classes = new ArrayList<>(numCls);
            for (int i = 0; i < numCls; i++) {
                classes.add(i, null);
            }
            HashMap<Integer, Integer> classesDocuments = new HashMap<>();
            HashMap<String, Integer> docsIDs = new HashMap<>();
            ArrayList<ArrayList<IndexValue>> documents = new ArrayList<>();
            HashMap<String, Integer> terms_ids = new HashMap<>();

            for (int i = 0; i < numAttr; i++) {
                if (i != arff.classIndex()) {
                    terms_ids.put(arff.attribute(i).name(), i);
                }
            }
            for (int idx = 0; idx < numInsts; idx++) {
                Instance inst = arff.get(idx);
                int clsPos = (int) (inst.classValue());
                String classStr = inst.toString(clsIdx);
                if (!classes.contains(classStr)) {
                    classes.set(clsPos, classStr);
                }
                ArrayList<IndexValue> doc = new ArrayList<>();
                docsIDs.put("" + idx, idx);                
                classesDocuments.put(idx, clsPos);                
                for (int attrIdx = 0; attrIdx < numAttr; attrIdx++) {
                    if (attrIdx != arff.classIndex()) {
                        double val = inst.value(attrIdx);
                        if (val > 0.0001) {
                            IndexValue iv = new IndexValue(attrIdx, val);
                            doc.add(iv);
                        }
                    }
                }
                documents.add(doc);
            }

            data.setClasses(classes);
            data.setClassesDocuments(classesDocuments);
            data.setDocsIDs(docsIDs);
            data.setIDsDocs(Tools.invertHashMap(docsIDs));
            data.setDocuments(documents);
            data.setTermsIDs(terms_ids);
            data.setIDsTerms(Tools.invertHashMap(terms_ids));

            return data;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conversor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {
        Data data = Conversor.arffToData("/home/thiagodepaulo/teste_jurídico.arff");
        System.out.println(data.getTerms());
    }

    public static Instances dataToArff(Data data) throws IOException {
        Instances instances = new Instances(new StringReader(dataToStrArff(data)));
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    public static String dataToStrArff(Data data) {
        StringBuilder sb = new StringBuilder();
        String nl = "\n";
        int classIdx = data.getNumTerms();

        sb.append("@RELATION IteraDATA");
        sb.append(nl);
        sb.append(nl);
        for (int wid = 0; wid < data.getTerms().size(); wid++) {
            sb.append("@ATTRIBUTE " + data.getTermName(wid) + "  REAL" + nl);
        }
        sb.append("@ATTRIBUTE class_  {" + String.join(", ", data.getClasses()) + "}");
        sb.append(nl);
        sb.append(nl);
        sb.append(nl);
        sb.append("@DATA\n");
        for (int docId = 0; docId < data.getNumDocs(); docId++) {
            
            int classId = data.getClassDocument(docId);
            sb.append("{");

            // Sorting
            Collections.sort(data.getAdjListDoc(docId), new Comparator<IndexValue>() {
                @Override
                public int compare(IndexValue iv1, IndexValue iv2) {
                    return Integer.compare(iv1.getIndex(), iv2.getIndex());
                }
            });
            for (IndexValue iv : data.getAdjListDoc(docId)) {
                sb.append(iv.getIndex() + " " + String.format(Locale.US, "%.4f", iv.getValue()) + ", ");
            }
            sb.append(classIdx + " " + data.getClasses().get(classId) + "}");
            sb.append(nl);
        }
        return sb.toString();
    }

}
