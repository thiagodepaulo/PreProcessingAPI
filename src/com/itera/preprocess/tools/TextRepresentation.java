//*****************************************************************************
// Author: Rafael Geraldeli Rossi
// E-mail: rgr.rossi at gmail com
// Last-Modified: January 29, 2015
// Description: Class to generate a document-term matrix in ARFF format.
//              (http://www.cs.waikato.ac.nz/ml/weka/arff.html)
//*****************************************************************************
package com.itera.preprocess.tools; 

import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.io.ListFiles;
import com.itera.preprocess.stempt.OrengoStemmer;
import com.itera.preprocess.stempt.Stemmer;
import com.itera.structures.Data;
import com.itera.structures.FeatureList;
import com.itera.structures.IndexValue;
import com.itera.structures.InputPattern;
import com.itera.preprocess.contextexpansion.JavaWord2Vec;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextRepresentation {

    //Function to read a text collection and generate a document-term matrix
    public static Data RepresentTM(String dirIn, PreProcessingConfig configuration, String w2vDir) {

        JavaWord2Vec w2v = null;

        try {
            if (!w2vDir.isEmpty()) {
                w2v = JavaWord2Vec.load(w2vDir);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Pré-processando Textos...");

        Data data = new Data();

        ArrayList<String> allClasses = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<FeatureList> terms = new ArrayList<FeatureList>(); // list constendo os atributos de cada documento da coleção
        HashMap<String, Integer> termDF = new HashMap<String, Integer>(); //armazena as DF dos atributos
        HashMap<String, String> stemPal = new HashMap<String, String>(); // dicionário do tipo palavra - stem
        ArrayList<File> filesIn = new ArrayList<File>();
        StopWords sw = null;
        if (configuration.isTranslateEN()) {
            configuration.setLanguage("english");
        }
        if (configuration.isRemoveStopwords()) {
            sw = new StopWords(configuration.getLanguage()); //Objeto para remoção das stopwords dos documentos
        }

        Cleaner cln = new Cleaner();
        Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalização em português
        StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em inglês
        ListFiles.List(new File(dirIn), filesIn); //Vetor para armazenar os documentos textuais

        Object[] orderedFiles = filesIn.toArray();
        Arrays.sort(orderedFiles);

        int interval = 10;
        System.out.print("- 0% ");
        int nextInterval = interval;

        for (int i = 0; i < orderedFiles.length; i++) { // criando vetores contendo os atributos e suas frquências em cada documento da coleção
            System.out.print(".");
            File fileIn = (File) orderedFiles[i];
            FeatureList features = new FeatureList();
            features.setFeatures(Preprocessing.FeatureGenerationTM(fileIn, configuration.getLanguage(), configuration.isRemoveStopwords(), configuration.isStemmed(), stemPal, termDF, sw, cln, stemPt, stemEn, w2v));
            terms.add(i, features);
            if (configuration.isDirectoryAsClasses()) {
                String classe = filesIn.get(i).getAbsolutePath();
                classe = classe.replace("\\", "/");
                classe = classe.substring(0, classe.lastIndexOf("/"));
                classe = classe.substring(classe.lastIndexOf("/") + 1, classe.length());
                if (!classe.equals("UNLABELED")) {
                    if (!allClasses.contains(classe)) {
                        allClasses.add(classe);
                    }
                }
            }

            double perc = ((i + 1) / (double) orderedFiles.length) * 100;

            if (perc > nextInterval) {
                System.out.println("");
                System.out.print("- " + nextInterval + "% ");
                nextInterval += interval;
            }
        }
        System.out.println("\n- 100%");

        System.out.println("- Gerando Representação Estruturada...");
        sw = null;
        cln = null;
        stemPt = null;
        stemEn = null;

        if (configuration.getDfMin() > 0) {
            names = new ArrayList<String>();
            Set<String> featureName = termDF.keySet();
            Iterator it = featureName.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (termDF.get(key) >= configuration.getDfMin()) {
                    names.add(key);
                }
            }
        }

        HashMap<String, Integer> terms_ids = new HashMap<String, Integer>();
        HashMap<Integer, String> ids_terms = new HashMap<Integer, String>();

        for (int name = 0; name < names.size(); name++) {
            terms_ids.put(names.get(name), name);
            ids_terms.put(name, names.get(name));
        }

        data.setMapTerms_CompleteTerms(stemPal);
        data.setTermsIDs(terms_ids);
        data.setIDsTerms(ids_terms);

        HashMap<Integer, Integer> classes = new HashMap<Integer, Integer>();
        HashMap<String, Integer> docs_ids = new HashMap<String, Integer>();
        HashMap<Integer, String> ids_docs = new HashMap<Integer, String>();

        int numDocs = filesIn.size();

        int idDoc = 0;
        for (int doc = 0; doc < filesIn.size(); doc++) {
            String arquivo = filesIn.get(doc).getAbsolutePath();
            arquivo = arquivo.replace("\\", "/");

            docs_ids.put(arquivo, idDoc);
            ids_docs.put(idDoc, arquivo);

            FeatureList termsFreq = terms.get(doc);

            ArrayList<IndexValue> indexValues = new ArrayList<IndexValue>();
            if (configuration.isTfidf()) {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    freq = freq * (Math.log10((double) numDocs / (double) (1 + termDF.get(termName))));
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            } else {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            }

            data.addAdjListDoc(indexValues);

            if (configuration.isDirectoryAsClasses()) {
                String classe = arquivo.substring(0, arquivo.lastIndexOf("/"));
                classe = classe.substring(classe.lastIndexOf("/") + 1, classe.length());
                if (!classe.equals("UNLABELED")) {
                    classes.put(idDoc, allClasses.indexOf(classe));
                }
            }

            idDoc++;
        }

        data.setIDsDocs(ids_docs);
        data.setDocsIDs(docs_ids);

        if (configuration.isDirectoryAsClasses()) {
            data.setClasses(allClasses);
            data.setClassesDocuments(classes);
        }

        System.out.println("Textos pré-processados");

        return data;
    }

    public static Data RepresentTM(List<InputPattern> dados, PreProcessingConfig configuration, String w2vDir) {

        JavaWord2Vec w2v = null;

        try {
            if (!w2vDir.isEmpty()) {
                w2v = JavaWord2Vec.load(w2vDir);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Pré-processando Textos...");

        Data data = new Data();

        ArrayList<String> allClasses = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<FeatureList> terms = new ArrayList<>(); // list constendo os atributos de cada documento da coleção
        HashMap<String, Integer> termDF = new HashMap<>(); //armazena as DF dos atributos
        HashMap<String, String> stemPal = new HashMap<>(); // dicionário do tipo palavra - stem
        ArrayList<File> filesIn = new ArrayList<>();
        StopWords sw = null;
        if (configuration.isTranslateEN()) {
            configuration.setLanguage("english");
        }
        if (configuration.isRemoveStopwords()) {
            sw = new StopWords(configuration.getLanguage()); //Objeto para remoção das stopwords dos documentos
        }

        Cleaner cln = new Cleaner();
        Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalização em português
        StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em inglês


        int interval = 10;
        System.out.print("- 0% ");
        int nextInterval = interval;
        
        int i = 0;
        for (InputPattern dado : dados) {
            System.out.print(".");

            String texto = dado.getTexto();
            String classe = dado.getClasse();

            FeatureList features = new FeatureList();

            features.setFeatures(Preprocessing.FeatureGenerationTM(texto, configuration.getLanguage(), configuration.isRemoveStopwords(), configuration.isStemmed(), stemPal, termDF, sw, cln, stemPt, stemEn, w2v));
            terms.add(i, features);

            if (!allClasses.contains(classe)) {
                allClasses.add(classe);
            }

            double perc = ((i + 1) / (double) dados.size()) * 100;

            if (perc > nextInterval) {
                System.out.println("");
                System.out.print("- " + nextInterval + "% ");
                nextInterval += interval;
            }
            i++;
        }

        System.out.println("\n- 100%");

        System.out.println("- Gerando Representação Estruturada...");
        sw = null;
        cln = null;
        stemPt = null;
        stemEn = null;

        if (configuration.getDFMin() > 0) {
            names = new ArrayList<>();
            Set<String> featureName = termDF.keySet();
            Iterator it = featureName.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (termDF.get(key) >= configuration.getDFMin()) {
                    names.add(key);
                }
            }
        }

        HashMap<String, Integer> terms_ids = new HashMap<>();
        HashMap<Integer, String> ids_terms = new HashMap<>();

        for (int name = 0; name < names.size(); name++) {
            terms_ids.put(names.get(name), name);
            ids_terms.put(name, names.get(name));
        }

        data.setMapTerms_CompleteTerms(stemPal);
        data.setTermsIDs(terms_ids);
        data.setIDsTerms(ids_terms);

        HashMap<Integer, Integer> classes = new HashMap<>();
        HashMap<String, Integer> docs_ids = new HashMap<>();
        HashMap<Integer, String> ids_docs = new HashMap<>();

        int numDocs = dados.size();

        int idDoc = 0;
        for (int doc = 0; doc < dados.size(); doc++) {
            String arquivo = dados.get(doc).getId() + "";
            arquivo = arquivo.replace("\\", "/");

            docs_ids.put(arquivo, idDoc);
            ids_docs.put(idDoc, arquivo);

            FeatureList termsFreq = terms.get(doc);

            ArrayList<IndexValue> indexValues = new ArrayList<>();
            if (configuration.getTFIDF()) {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    freq = freq * (Math.log10((double) numDocs / (double) (1 + termDF.get(termName))));
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            } else {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            }

            data.addAdjListDoc(indexValues);

            String classe = dados.get(doc).getClasse();
            if (!classe.equals("UNLABELED")) {
                classes.put(idDoc, allClasses.indexOf(classe));
            }

            idDoc++;
        }

        data.setIDsDocs(ids_docs);
        data.setDocsIDs(docs_ids);

        if (configuration.getDirectoryAsClasses()) {
            data.setClasses(allClasses);
            data.setClassesDocuments(classes);
        }

        System.out.println("Textos pré-processados");

        return data;
    }
    
    public static Data RepresentNLP(String dirIn, PreProcessingConfig configuration) {

        System.out.println("Pré-processando Textos...");

        Data data = new Data();

        ArrayList<String> allClasses = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<FeatureList> terms = new ArrayList<FeatureList>(); // list constendo os atributos de cada documento da coleção
        HashMap<String, Integer> termDF = new HashMap<String, Integer>(); //armazena as DF dos atributos
        HashMap<String, String> stemPal = new HashMap<String, String>(); // dicionário do tipo palavra - stem
        ArrayList<File> filesIn = new ArrayList<File>();
        StopWords sw = null;
        if (configuration.getStopWords()) {
            sw = new StopWords(configuration.getLanguage()); //Objeto para remoção das stopwords dos documentos
        }

        Cleaner cln = new Cleaner();
        Stemmer stemPt = new OrengoStemmer(); //Objeto para a radicalização em português
        StemmerEn stemEn = new StemmerEn(); //Objeto para a radicalização em inglês
        ListFiles.List(new File(dirIn), filesIn); //Vetor para armazenar os documentos textuais

        Object[] orderedFiles = filesIn.toArray();
        Arrays.sort(orderedFiles);

        int interval = 10;
        System.out.print("- 0% ");
        int nextInterval = interval;

        for (int i = 0; i < orderedFiles.length; i++) { // criando vetores contendo os atributos e suas frquências em cada documento da coleção
            System.out.print(".");
            File fileIn = (File) orderedFiles[i];
            FeatureList features = new FeatureList();
            features.setFeatures(Preprocessing.FeatureGenerationNLP(fileIn, configuration.getLanguage(), configuration.getStopWords(), configuration.getStem(), stemPal, termDF, sw, cln, stemPt, stemEn, configuration.getTranslateEN()));
            terms.add(i, features);
            if (configuration.getDirectoryAsClasses()) {
                String classe = filesIn.get(i).getAbsolutePath();
                classe = classe.replace("\\", "/");
                classe = classe.substring(0, classe.lastIndexOf("/"));
                classe = classe.substring(classe.lastIndexOf("/") + 1, classe.length());
                if (!classe.equals("UNLABELED")) {
                    if (!allClasses.contains(classe)) {
                        allClasses.add(classe);
                    }
                }
            }

            double perc = ((i + 1) / (double) orderedFiles.length) * 100;

            if (perc > nextInterval) {
                System.out.println("");
                System.out.print("- " + nextInterval + "% ");
                nextInterval += interval;
            }
        }
        System.out.println("\n- 100%");

        System.out.println("- Gerando Representação Estruturada...");
        sw = null;
        cln = null;
        stemPt = null;
        stemEn = null;

        if (configuration.getDFMin() > 0) {
            names = new ArrayList<String>();
            Set<String> featureName = termDF.keySet();
            Iterator it = featureName.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (termDF.get(key) >= configuration.getDFMin()) {
                    names.add(key);
                }
            }
        }

        HashMap<String, Integer> terms_ids = new HashMap<String, Integer>();
        HashMap<Integer, String> ids_terms = new HashMap<Integer, String>();

        for (int name = 0; name < names.size(); name++) {
            terms_ids.put(names.get(name), name);
            ids_terms.put(name, names.get(name));
        }

        data.setMapTerms_CompleteTerms(stemPal);
        data.setTermsIDs(terms_ids);
        data.setIDsTerms(ids_terms);

        HashMap<Integer, Integer> classes = new HashMap<Integer, Integer>();
        HashMap<String, Integer> docs_ids = new HashMap<String, Integer>();
        HashMap<Integer, String> ids_docs = new HashMap<Integer, String>();

        int numDocs = filesIn.size();

        int idDoc = 0;
        for (int doc = 0; doc < filesIn.size(); doc++) {
            String arquivo = filesIn.get(doc).getAbsolutePath();
            arquivo = arquivo.replace("\\", "/");

            docs_ids.put(arquivo, idDoc);
            ids_docs.put(idDoc, arquivo);

            FeatureList termsFreq = terms.get(doc);

            ArrayList<IndexValue> indexValues = new ArrayList<IndexValue>();
            if (configuration.getTFIDF()) {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    freq = freq * (Math.log10((double) numDocs / (double) (1 + termDF.get(termName))));
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            } else {
                for (int term = 0; term < terms.get(doc).getFeatures().size(); term++) {
                    String termName = terms.get(doc).getFeature(term).getFeature();
                    double freq = terms.get(doc).getFeature(term).getFrequency();
                    if (terms_ids.containsKey(termName)) {
                        IndexValue indValue = new IndexValue(terms_ids.get(termName), freq);
                        indexValues.add(indValue);
                    }
                }
            }

            data.addAdjListDoc(indexValues);

            if (configuration.getDirectoryAsClasses()) {
                String classe = arquivo.substring(0, arquivo.lastIndexOf("/"));
                classe = classe.substring(classe.lastIndexOf("/") + 1, classe.length());
                if (!classe.equals("UNLABELED")) {
                    classes.put(idDoc, allClasses.indexOf(classe));
                }
            }

            idDoc++;
        }

        data.setIDsDocs(ids_docs);
        data.setDocsIDs(docs_ids);

        if (configuration.getDirectoryAsClasses()) {
            data.setClasses(allClasses);
            data.setClassesDocuments(classes);
        }

        System.out.println("Textos pré-processados");

        return data;
    }

}
