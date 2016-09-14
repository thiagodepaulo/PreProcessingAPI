/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import com.itera.util.Tools;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gaia
 */
public class Data implements Serializable {

    private HashMap<String, Integer> terms_ids;
    private HashMap<Integer, String> ids_terms;

    private HashMap<String, String> terms_completeTerms;

    private HashMap<String, Integer> docs_ids;
    private HashMap<Integer, String> ids_docs;

    private ArrayList<String> classes;

    private ArrayList<ArrayList<IndexValue>> documents;
    private HashMap<Integer, Integer> classesDocuments;

    public Data() {
        setIDsTerms(new HashMap<Integer, String>());
        setTermsIDs(new HashMap<String, Integer>());
        setDocuments(new ArrayList<ArrayList<IndexValue>>());
        setMapTerms_CompleteTerms(new HashMap<String, String>());
        setClasses(new ArrayList<String>());
        setDocsIDs(new HashMap<String, Integer>());
        setIDsDocs(new HashMap<Integer, String>());
        setClassesDocuments(new HashMap<>());
    }

    public Integer getNumDocs() {
        return documents.size();
    }

    public Integer getNumTerms() {
        return terms_ids.size();
    }

    public Integer getNumClasses() {
        return classes.size();
    }

    public Integer getClassDocument(int idDoc) {
        if (classesDocuments.containsKey(idDoc)) {
            return classesDocuments.get(idDoc);
        } else {
            return -1;
        }

    }

    public ArrayList<ArrayList<IndexValue>> getAdjListTerms() {
        ArrayList<ArrayList<IndexValue>> adjListTerms = new ArrayList<ArrayList<IndexValue>>();

        //Inicializando a lista de adjacência dos termos
        for (int term = 0; term < getNumTerms(); term++) {
            adjListTerms.add(new ArrayList<IndexValue>());
        }

        for (int doc = 0; doc < getNumDocs(); doc++) {
            ArrayList<IndexValue> neighbors = documents.get(doc);
            for (int term = 0; term < neighbors.size(); term++) {
                int idTerm = neighbors.get(term).getIndex();
                double value = neighbors.get(term).getValue();
                IndexValue indVal = new IndexValue(doc, value);
                adjListTerms.get(idTerm).add(indVal);
            }
        }

        return adjListTerms;
    }

    public String getCompleteTerms(String stem) {
        return terms_completeTerms.get(stem);
    }

    public ArrayList<ArrayList<IndexValue>> getAdjListDocs() {
        return this.documents;
    }

    public ArrayList<IndexValue> getAdjListDoc(int idDoc) {
        return this.documents.get(idDoc);
    }

    public ArrayList<String> getClasses() {
        return this.classes;
    }

    public HashMap<String, Integer> getTermsIDs() {
        return this.terms_ids;
    }

    public ArrayList<String> getTerms() {
        ArrayList<String> listTerms = new ArrayList<String>();

        Object[] keys = terms_ids.keySet().toArray();
        for (int key = 0; key < keys.length; key++) {
            listTerms.add(keys[key].toString());
        }
        return listTerms;
    }

    public ArrayList<String> getDocs() {
        ArrayList<String> listDocs = new ArrayList<String>();
        Object[] keys = docs_ids.keySet().toArray();
        for (int key = 0; key < keys.length; key++) {
            listDocs.add(keys[key].toString());
        }
        return listDocs;
    }

    public ArrayList<Integer> getListLabeledDocs() {
        ArrayList<Integer> listLabeledDocs = new ArrayList<Integer>();

        Object[] keys = classesDocuments.keySet().toArray();
        for (int key = 0; key < keys.length; key++) {
            listLabeledDocs.add((Integer) keys[key]);
        }

        return listLabeledDocs;
    }

    public ArrayList<Integer> getListUnlabeledDocs() {
        ArrayList<Integer> listUnlabeledDocs = new ArrayList<Integer>();

        Object[] keys = ids_docs.keySet().toArray();
        for (int key = 0; key < keys.length; key++) {
            int key_id = (int) keys[key];
            if (!classesDocuments.containsKey(key_id)) {
                listUnlabeledDocs.add(key_id);
            }
        }
        return listUnlabeledDocs;
    }

    public String getDocName(int idDoc) {
        return this.ids_docs.get(idDoc);
    }

    public Integer getDocID(String docName) {
        return this.docs_ids.get(docName);
    }

    public String getTermName(int idTerm) {
        return ids_terms.get(idTerm);
    }

    public Integer getTermID(String termName) {
        return terms_ids.get(termName);
    }

    public void addAdjListDoc(ArrayList<IndexValue> adjList) {
        documents.add(adjList);
    }

    public void addClassDocument(int idDoc, int classDoc) {
        this.classesDocuments.put(idDoc, classDoc);
    }

    public void setIDsDocs(HashMap<Integer, String> ids_docs) {
        this.ids_docs = ids_docs;
    }

    public void setDocsIDs(HashMap<String, Integer> docsIDs) {
        this.docs_ids = docsIDs;
    }

    public void setMapTerms_CompleteTerms(HashMap<String, String> terms_completeTerms) {
        this.terms_completeTerms = terms_completeTerms;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    public void setClassesDocuments(HashMap<Integer, Integer> classesDocuments) {
        this.classesDocuments = classesDocuments;
    }

    public void setDocuments(ArrayList<ArrayList<IndexValue>> documents) {
        this.documents = documents;
    }

    public void setTermsIDs(HashMap<String, Integer> terms_ids) {
        this.terms_ids = terms_ids;
    }

    public void setIDsTerms(HashMap<Integer, String> ids_terms) {
        this.ids_terms = ids_terms;
    }

    public String toString() {
        return documents.toString();
    }

    /**
     * Stratifies a set of instances according to its class values if the class
     * attribute is nominal (so that afterwards a stratified cross-validation
     * can be performed).
     *
     * @param numFolds the number of folds in the cross-validation
     * @throws UnassignedClassException if the class is not set
     */
    public void stratify(int numFolds) {

        if (numFolds <= 1) {
            throw new IllegalArgumentException(
                    "Number of folds must be greater than 1");
        }

        // sort by class
        int index = 1;
        int numDocs = this.getNumDocs();
        int idDoc1, idDoc2;
        while (index < numDocs) {
            idDoc1 = index - 1;
            for (int j = index; j < numDocs; j++) {
                idDoc2 = j;
                if (this.getClassDocument(idDoc1) == this.getClassDocument(idDoc2)) {
                    swap(index, j);
                    index++;
                }
            }
            index++;
        }
        stratStep(numFolds);
    }

    /**
     * Help function needed for stratification of set.
     *
     * @param numFolds the number of folds for the stratification
     */
    protected void stratStep(int numFolds) {

        int numDocs = this.getNumDocs();
        ArrayList<ArrayList<IndexValue>> newDocs = new ArrayList<>(numDocs);
        HashMap<Integer, String> newIds_docs = new HashMap<>();
        HashMap<Integer, Integer> newClassesDocuments = new HashMap<>();
        int start = 0, j;

        int newDocId = 0;
        // create stratified batch
        while (newDocs.size() < numDocs) {
            j = start;
            while (j < numDocs) {
                newDocs.add(this.documents.get(j));
                newIds_docs.put(newDocId, this.ids_docs.get(j));
                newClassesDocuments.put(newDocId, this.classesDocuments.get(j));
                newDocId++;
                j = j + numFolds;
            }
            start++;
        }
        this.documents = newDocs;
        this.ids_docs = newIds_docs;
        this.docs_ids = Tools.invertHashMap(newIds_docs);
        this.classesDocuments = newClassesDocuments;
    }

    public void swap(int id1, int id2) {
        String name1 = ids_docs.get(id1);
        String name2 = ids_docs.get(id2);
        ArrayList<IndexValue> doc1 = this.documents.get(id1);
        ArrayList<IndexValue> doc2 = this.documents.get(id2);

        // swap id1 id2
        docs_ids.put(name1, id2);
        docs_ids.put(name2, id1);
        ids_docs.put(id1, name2);
        ids_docs.put(id2, name1);
        this.documents.set(id1, doc2);
        this.documents.set(id2, doc1);
    }

    /**
     * Creates the training set for one fold of a cross-validation on the
     * dataset.
     *
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @param numFold 0 for the first fold, 1 for the second, ...
     * @return the training set
     * @throws IllegalArgumentException if the number of folds is less than 2 or
     * greater than the number of instances.
     */
    // @ requires 2 <= numFolds && numFolds < numInstances();
    // @ requires 0 <= numFold && numFold < numFolds;
    public Data trainCV(int numFolds, int numFold) {

        int numInstForFold, first, offset;
        Data train;
        int numDocs = this.getNumDocs();

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > numDocs) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }
        numInstForFold = numDocs / numFolds;
        if (numFold < numDocs % numFolds) {
            numInstForFold++;
            offset = numFold;
        } else {
            offset = numDocs % numFolds;
        }
        train = new Data();
        first = numFold * (numDocs / numFolds) + offset;
        copyInstances(0, train, first);
        copyInstances(first + numInstForFold, train, numDocs - first
                - numInstForFold);

        return train;
    }

    /**
     * Creates the test set for one fold of a cross-validation on the dataset.
     *
     * @param numFolds the number of folds in the cross-validation. Must be
     * greater than 1.
     * @param numFold 0 for the first fold, 1 for the second, ...
     * @return the test set as a set of weighted instances
     * @throws IllegalArgumentException if the number of folds is less than 2 or
     * greater than the number of instances.
     */
    // @ requires 2 <= numFolds && numFolds < numInstances();
    // @ requires 0 <= numFold && numFold < numFolds;
    public Data testCV(int numFolds, int numFold) {

        int numInstForFold, first, offset;
        Data test;
        int numDocs = this.getNumDocs();

        if (numFolds < 2) {
            throw new IllegalArgumentException("Number of folds must be at least 2!");
        }
        if (numFolds > numDocs) {
            throw new IllegalArgumentException(
                    "Can't have more folds than instances!");
        }
        numInstForFold = numDocs / numFolds;
        if (numFold < numDocs % numFolds) {
            numInstForFold++;
            offset = numFold;
        } else {
            offset = numDocs % numFolds;
        }
        test = new Data();
        first = numFold * (numDocs / numFolds) + offset;
        copyInstances(first, test, numInstForFold);
        return test;
    }

    public void copyInstances(int inic, Data data, int end) {
        HashMap<Integer, String> ids_docs = data.ids_docs;
        data.classes = new ArrayList(this.classes);
        data.ids_terms = new HashMap<>(this.ids_terms);
        data.terms_ids = new HashMap<>(this.terms_ids);

        int lastDocId = data.documents.size();        
        for (int i = inic; i < inic + end; i++) {
            data.documents.add(lastDocId, this.documents.get(i));            
            data.classesDocuments.put(lastDocId, this.classesDocuments.get(i));
            data.ids_docs.put(lastDocId, this.ids_docs.get(i));
            data.docs_ids.put(this.ids_docs.get(i), lastDocId);
            lastDocId++;
        }
    }
}
