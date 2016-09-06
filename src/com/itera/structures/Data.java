/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Gaia
 */
public class Data implements Serializable{
    
    private HashMap<String,Integer> terms_ids;
    private HashMap<Integer,String> ids_terms;
    
    private HashMap<String,String> terms_completeTerms;
    
    private HashMap<String,Integer> docs_ids;
    private HashMap<Integer,String> ids_docs;
    
    private ArrayList<String> classes;
    
    private ArrayList<ArrayList<IndexValue>> documents;
    private HashMap<Integer,Integer> classesDocuments;
    
    public Data(){
        setIDsTerms(new HashMap<Integer,String>());
        setTermsIDs(new HashMap<String,Integer>());
        setDocuments(new ArrayList<ArrayList<IndexValue>>());
        setMapTerms_CompleteTerms(new HashMap<String,String>());
        setClasses(new ArrayList<String>());
        setDocsIDs(new HashMap<String,Integer>());
        setIDsDocs(new HashMap<Integer,String>());
    }
        
    public Integer getNumDocs(){
        return documents.size();
    }
    
    public Integer getNumTerms(){
        return terms_ids.size();
    }
    
    public Integer getNumClasses(){
        return classes.size();
    }
    
    public Integer getClassDocument(int idDoc){
        if(classesDocuments.containsKey(idDoc)){
            return classesDocuments.get(idDoc);
        }else{
            return -1;
        }
        
    }
    
    public ArrayList<ArrayList<IndexValue>> getAdjListTerms(){
        ArrayList<ArrayList<IndexValue>> adjListTerms = new ArrayList<ArrayList<IndexValue>>();
        
        //Inicializando a lista de adjacência dos termos
        for(int term=0;term<getNumTerms();term++){
            adjListTerms.add(new ArrayList<IndexValue>());
        }
        
        for(int doc=0;doc<getNumDocs();doc++){
            ArrayList<IndexValue> neighbors = documents.get(doc);
            for(int term=0;term<neighbors.size();term++){
                int idTerm = neighbors.get(term).getIndex();
                double value = neighbors.get(term).getValue();
                IndexValue indVal = new IndexValue(doc,value);
                adjListTerms.get(idTerm).add(indVal);
            }
        }
        
        return adjListTerms;
    }
    
    public String getCompleteTerms(String stem){
        return terms_completeTerms.get(stem);
    }
    
    public ArrayList<ArrayList<IndexValue>> getAdjListDocs(){
        return this.documents;
    }
    
    public ArrayList<IndexValue> getAdjListDoc(int idDoc){
        return this.documents.get(idDoc);
    }
    
    public ArrayList<String> getClasses(){
        return this.classes;
    }
    
    public HashMap<String,Integer> getTermsIDs(){
        return this.terms_ids;
    }
    
    public ArrayList<String> getTerms(){
        ArrayList<String> listTerms = new ArrayList<String>();
        
        Object[] keys = terms_ids.keySet().toArray();
        for(int key=0;key<keys.length;key++){
            listTerms.add(keys[key].toString());
        }
        return listTerms;
    }
    
    public ArrayList<String> getDocs(){
        ArrayList<String> listDocs = new ArrayList<String>();
        Object[] keys = docs_ids.keySet().toArray();
        for(int key=0;key<keys.length;key++){
            listDocs.add(keys[key].toString());
        }
        return listDocs;
    }
    
    public ArrayList<Integer> getListLabeledDocs(){
        ArrayList<Integer> listLabeledDocs = new ArrayList<Integer>();
        
        Object[] keys =  classesDocuments.keySet().toArray();
        for(int key=0;key<keys.length;key++){
            listLabeledDocs.add((Integer)keys[key]);
        }
        
        return listLabeledDocs;
    }
    
    public ArrayList<Integer> getListUnlabeledDocs(){
        ArrayList<Integer> listUnlabeledDocs = new ArrayList<Integer>();
        
        Object[] keys =  ids_docs.keySet().toArray();
        for(int key=0;key<keys.length;key++){
            int key_id = (int)keys[key];
            if(!classesDocuments.containsKey(key_id)){
                listUnlabeledDocs.add(key_id);
            }
        }
        return listUnlabeledDocs;
    }
    
    public String getDocName(int idDoc){
        return this.ids_docs.get(idDoc);
    }

    public Integer getDocID(String docName){
        return this.docs_ids.get(docName);
    }
    
    public String getTermName(int idTerm){
        return ids_terms.get(idTerm);
    }
    
    public Integer getTermID(String termName){
        return terms_ids.get(termName);
    }
    
    public void addAdjListDoc(ArrayList<IndexValue> adjList){
        documents.add(adjList);
    }
    
    public void addClassDocument(int idDoc, int classDoc){
        this.classesDocuments.put(idDoc, classDoc);
    }
    
    public void setIDsDocs(HashMap<Integer,String> ids_docs){
        this.ids_docs = ids_docs;
    }
    
    
    public void setDocsIDs(HashMap<String,Integer> docsIDs){
        this.docs_ids = docsIDs;
    }
    
    public void setMapTerms_CompleteTerms(HashMap<String,String> terms_completeTerms){
        this.terms_completeTerms = terms_completeTerms;
    }
    
    public void setClasses(ArrayList<String> classes){
        this.classes = classes;
    }
    
    public void setClassesDocuments(HashMap<Integer,Integer> classesDocuments){
        this.classesDocuments = classesDocuments;
    }
    
    public void setDocuments(ArrayList<ArrayList<IndexValue>> documents){
        this.documents = documents;
    }
    
    public void setTermsIDs(HashMap<String,Integer> terms_ids){
        this.terms_ids = terms_ids;
    }
    
    public void setIDsTerms(HashMap<Integer,String> ids_terms){
        this.ids_terms = ids_terms;
    }
    
}
