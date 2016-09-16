//*****************************************************************************
// Author: Rafael Geraldeli Rossi
// E-mail: rgr.rossi at gmail com
// Last-Modified: February 26, 2015
// Description: Steps to generate a document-term matrix from a text collections
//*****************************************************************************
package com.itera.preprocess.tools;

import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.preprocess.contextexpansion.ContextExpasion;
import com.itera.structures.Data;
import com.itera.structures.InputPattern;
import com.itera.structures.TermFreq;
import com.itera.preprocess.contextexpansion.JavaWord2Vec;
import com.itera.preprocess.contextexpansion.Pair;
import com.itera.preprocess.stempt.Stemmer;
import com.itera.structures.Conversor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preprocessing {

    /* Function to extract features from a textual document
       file - path of textual document
       lang [port|engl] - laguage a textual document (only portugues and english
       remStopWords - remove stopwords
       stemming - replace the word by their stem
       stemTerm - mapping from stemmed temrs to original words
       termDF - document frequency of terms
       sw - instance of the StopWords class
       cln - instance of the Cleaner class
       stemPt - instance of the Stemmer class
       stemEn - instance of the StemmerEn class
     */
    static List<String> globalWords;

    public static ArrayList<TermFreq> FeatureGenerationTM(File file, String lang, boolean remStopWords, boolean stemming, HashMap<String, String> stemTerm, HashMap<String, Integer> termDF, StopWords sw, Cleaner cln, Stemmer stemPt, StemmerEn stemEn, JavaWord2Vec w2v) {
        if (globalWords == null) {
            globalWords = new ArrayList<>();
        }
        ArrayList<TermFreq> atributos = new ArrayList<>();

        StringBuffer txt = new StringBuffer();

        try {
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
            }
            String charset = CharsetRecognition.Recognize(file);
            BufferedReader txtFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

            String line;
            while ((line = txtFile.readLine()) != null) {
                txt.append(line + " ");
            } // Leitura do fileuivo texto e armazenamento na variável txt
            txtFile.close();            
            atributos = FeatureGenerationTM(txt.toString(), lang, remStopWords, stemming, stemTerm, termDF, sw, cln, stemPt, stemEn, w2v);

        } catch (Exception e) {
            System.err.println("Error when reading the file " + file.getAbsolutePath() + ".");
            e.printStackTrace();
            System.exit(0);
        }

        return atributos;
    }

    public static ArrayList<TermFreq> FeatureGenerationTM(String txt, String lang, boolean remStopWords, boolean stemming, HashMap<String, String> stemTerm, HashMap<String, Integer> termDF, StopWords sw, Cleaner cln, Stemmer stemPt, StemmerEn stemEn, JavaWord2Vec w2v) {
        if (globalWords == null) {
            globalWords = new ArrayList<>();
        }

        ArrayList<TermFreq> atributos = new ArrayList<TermFreq>();

        String cleanText = cln.clean(txt.toString()); //Text cleaning
        if (remStopWords == true) {
            cleanText = sw.removeStopWords(cleanText); //Stopwords removal
        }

        ArrayList<String> words = new ArrayList<String>();
        HashMap<String, Integer> hashTermFreq = new HashMap<String, Integer>();
        String[] allWords = cleanText.split(" "); //Stores the words of a text in a vector
        if (stemming == true) {
            if (lang.equals("portuguese")) { //Word stemming
                for (int i = 0; i < allWords.length; i++) {
                    String key = allWords[i];

                    processWithStemPT(key, stemTerm, stemPt, hashTermFreq, words, termDF);

                    if (w2v != null) {

                        if (!globalWords.contains(key)) {
                            List<Pair<String, float[]>> mostSimilar = w2v.mostSimilar(key);

                            if (mostSimilar != null) {
                                if (mostSimilar.size() > 0) {
                                    //System.out.println("Expandindo o contexto de " + key + " :");
                                    for (Pair<String, float[]> pair : mostSimilar) {
                                        String key_ = pair._1;

                                        if (key_ != null) {
                                            if (key_.length() > 0) {
                                                String cleanKey = cln.clean(key_); //Text cleaning

                                                if (!globalWords.contains(cleanKey)) {
                                                    if (!sw.isStopWord(cleanKey)) {
                                                        Double similarity = w2v.similarity(key, key_);
                                                        if (similarity < 0.99 && similarity > 0.6) {
                                                            //System.out.println("        - " + cleanKey + " " + similarity);
                                                            processWithStemPT(cleanKey, stemTerm, stemPt, hashTermFreq, words, termDF);
                                                        }
                                                    }
                                                }

                                            }
                                        }

                                    }
                                }
                            }
                        }

                    }

                    globalWords.add(key);
                }
            } else {
                for (int i = 0; i < allWords.length; i++) {
                    String key = allWords[i];

                    key = key.trim();

                    processWithStemEn(key, stemTerm, hashTermFreq, words, termDF);

                    if (w2v != null) {
                        List<Pair<String, float[]>> mostSimilar = w2v.mostSimilar(key);

                        if (mostSimilar != null) {
                            if (mostSimilar.size() > 0) {
                                for (Pair<String, float[]> pair : mostSimilar) {
                                    String key_ = pair._1;
                                    processWithStemEn(key_, stemTerm, hashTermFreq, words, termDF);
                                }
                            }
                        }

                    }
                }
            }
        } else {
            for (int i = 0; i < allWords.length; i++) {
                String key = allWords[i];

                key = key.trim();

                processWithoutStem(key, stemTerm, hashTermFreq, words, termDF);

                if (w2v != null) {
                    List<Pair<String, float[]>> mostSimilar = w2v.mostSimilar(key);

                    if (mostSimilar.size() > 0) {
                        for (Pair<String, float[]> pair : mostSimilar) {
                            String key_ = pair._1;
                            processWithStemEn(key_, stemTerm, hashTermFreq, words, termDF);
                        }
                    }
                }
            }
        }

        Set<String> termList = hashTermFreq.keySet();
        Object[] termArray = termList.toArray();
        for (int i = 0; i < termArray.length; i++) {
            String key = termArray[i].toString();
            atributos.add(new TermFreq(key, hashTermFreq.get(key)));
        }
        
        return atributos;
    }

    public static ArrayList<TermFreq> FeatureGenerationNLP(File file, String lang, boolean remStopWords, boolean stemming, HashMap<String, String> stemTerm, HashMap<String, Integer> termDF, StopWords sw, Cleaner cln, Stemmer stemPt, StemmerEn stemEn, boolean translateEN) {
        ArrayList<TermFreq> atributos = new ArrayList<TermFreq>();

        StringBuffer txt = new StringBuffer();

        //Translate.setKey("trnsl.1.1.20151117T170157Z.062c6dbb81142d42.4d7cdfdbba01c0ee3816e9f8510ec62d1570199e");
        try {
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
            }
            String charset = CharsetRecognition.Recognize(file);
            BufferedReader txtFile = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

            String line;
            while ((line = txtFile.readLine()) != null) {
                txt.append(line + " ");
            } // Leitura do fileuivo texto e armazenamento na variável txt
            txtFile.close();
        } catch (Exception e) {
            System.err.println("Error when reading the file " + file.getAbsolutePath() + ".");
            e.printStackTrace();
            System.exit(0);
        }

        if (translateEN == true) {
            try {
                //Translate.setKey("trnsl.1.1.20151117T170157Z.062c6dbb81142d42.4d7cdfdbba01c0ee3816e9f8510ec62d1570199e");
                //txt = new StringBuffer(Translate.execute(txt.toString(), Language.PORTUGUESE, Language.ENGLISH));
            } catch (Exception e) {
                System.out.println("Houve um erro ao tradzir o texto para o inglês");
                e.printStackTrace();
            }
        }

        String cleanText = cln.clean(txt.toString()); //Text cleaning
        if (remStopWords == true) {
            cleanText = sw.removeStopWords(cleanText); //Stopwords removal
        }

        ArrayList<String> words = new ArrayList<String>();
        HashMap<String, Integer> hashTermFreq = new HashMap<String, Integer>();
        String[] allWords = cleanText.split(" "); //Stores the words of a text in a vector
        if (stemming == true) {
            if (lang.equals("portuguese")) { //Word stemming
                for (int i = 0; i < allWords.length; i++) {
                    String key = allWords[i];
                    String stem;
                    if (stemTerm.containsKey(key)) {
                        stem = stemTerm.get(key);
                    } else {
                        stem = new String(stemPt.wordStemming(key));
                        stemTerm.put(stem, key);
                    }
                    if (hashTermFreq.containsKey(stem)) {
                        Integer freq = hashTermFreq.get(stem);
                        hashTermFreq.put(stem, freq + 1);
                    } else if (stem.length() > 1) {
                        hashTermFreq.put(stem, 1);
                        if (!words.contains(stem)) {
                            words.add(stem);
                            if (termDF.containsKey(stem)) {
                                int value = termDF.get(stem);
                                value++;
                                termDF.put(stem, value);
                            } else {
                                termDF.put(stem, 1);
                            }
                        }
                    }

                }
            } else {
                for (int i = 0; i < allWords.length; i++) {
                    String key = allWords[i];
                    key = key.trim();
                    String stem;
                    if (stemTerm.containsKey(key)) {
                        stem = stemTerm.get(key);
                    } else {
                        stem = new String(StemmerEn.get(key));
                        stemTerm.put(stem, key);
                    }

                    if (hashTermFreq.containsKey(stem)) {
                        Integer freq = hashTermFreq.get(stem);
                        hashTermFreq.put(stem, freq + 1);
                    } else if (stem.length() > 1) {
                        hashTermFreq.put(stem, 1);
                        if (!words.contains(stem)) {
                            words.add(stem);
                            if (termDF.containsKey(stem)) {
                                int value = termDF.get(stem);
                                value++;
                                termDF.put(stem, value);
                            } else {
                                termDF.put(stem, 1);
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < allWords.length; i++) {
                String key = allWords[i];
                key = key.trim();
                if (!stemTerm.containsKey(key)) {
                    stemTerm.put(key, key);
                }
                if (hashTermFreq.containsKey(key)) {
                    Integer freq = hashTermFreq.get(key);
                    hashTermFreq.put(key, freq + 1);
                } else if (key.length() > 1) {
                    hashTermFreq.put(key, 1);
                    if (!words.contains(key)) {
                        words.add(key);
                        if (termDF.containsKey(key)) {
                            int value = termDF.get(key);
                            value++;
                            termDF.put(key, value);
                        } else {
                            termDF.put(key, 1);
                        }
                    }
                }
            }
        }

        Set<String> termList = hashTermFreq.keySet();
        Object[] termArray = termList.toArray();
        for (int i = 0; i < termArray.length; i++) {
            String key = termArray[i].toString();
            atributos.add(new TermFreq(key, hashTermFreq.get(key)));
        }
        return atributos;
    }

    private static void processWithStemPT(String key, HashMap<String, String> stemTerm, Stemmer stemPt, HashMap<String, Integer> hashTermFreq, ArrayList<String> words, HashMap<String, Integer> termDF) {
        String stem;
        if (stemTerm.containsKey(key)) {
            stem = stemTerm.get(key);
        } else {
            stem = new String(stemPt.wordStemming(key));
            stemTerm.put(stem, key);
        }
        if (hashTermFreq.containsKey(stem)) {
            Integer freq = hashTermFreq.get(stem);
            hashTermFreq.put(stem, freq + 1);
        } else if (stem.length() > 1) {
            hashTermFreq.put(stem, 1);
            if (!words.contains(stem)) {
                words.add(stem);
                if (termDF.containsKey(stem)) {
                    int value = termDF.get(stem);
                    value++;
                    termDF.put(stem, value);
                } else {
                    termDF.put(stem, 1);
                }
            }
        }
    }

    private static void processWithStemEn(String key, HashMap<String, String> stemTerm, HashMap<String, Integer> hashTermFreq, ArrayList<String> words, HashMap<String, Integer> termDF) {
        key = key.trim();
        String stem;
        if (stemTerm.containsKey(key)) {
            stem = stemTerm.get(key);
        } else {
            stem = new String(StemmerEn.get(key));
            stemTerm.put(stem, key);
        }

        if (hashTermFreq.containsKey(stem)) {
            Integer freq = hashTermFreq.get(stem);
            hashTermFreq.put(stem, freq + 1);
        } else if (stem.length() > 1) {
            hashTermFreq.put(stem, 1);
            if (!words.contains(stem)) {
                words.add(stem);
                if (termDF.containsKey(stem)) {
                    int value = termDF.get(stem);
                    value++;
                    termDF.put(stem, value);
                } else {
                    termDF.put(stem, 1);
                }
            }
        }
    }

    private static void processWithoutStem(String key, HashMap<String, String> stemTerm, HashMap<String, Integer> hashTermFreq, ArrayList<String> words, HashMap<String, Integer> termDF) {

        if (!stemTerm.containsKey(key)) {
            stemTerm.put(key, key);
        }
        if (hashTermFreq.containsKey(key)) {
            Integer freq = hashTermFreq.get(key);
            hashTermFreq.put(key, freq + 1);
        } else if (key.length() > 1) {
            hashTermFreq.put(key, 1);
            if (!words.contains(key)) {
                words.add(key);
                if (termDF.containsKey(key)) {
                    int value = termDF.get(key);
                    value++;
                    termDF.put(key, value);
                } else {
                    termDF.put(key, 1);
                }
            }
        }
    }

    public static List<InputPattern> preprocess(List<InputPattern> lInput, PreProcessingConfig config) {
        Cleaner cln = new Cleaner();
        ptstemmer.Stemmer stemPt = null;
        try {
            stemPt = new ptstemmer.implementations.OrengoStemmer();
        } catch (ptstemmer.exceptions.PTStemmerException ex) {
            Logger.getLogger(Preprocessing.class.getName()).log(Level.SEVERE, null, ex);
        }
        StopWords sw = new StopWords(config.getLanguage()); //Objeto para remoção das stopwords dos documentos        
        InputPattern[] newInputArray = new InputPattern[lInput.size()];
        String[][] vetsWords = new String[lInput.size()][];
        HashMap<String, Integer> termDf = new HashMap<>();
        int pos = 0;
        for (InputPattern input : lInput) {
            String[] words = input.getTexto().split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (words[i].length() <= config.getWordLenghtMin()) {
                    words[i] = null;
                    continue;
                }
                if (config.isCleaning()) {
                    words[i] = cln.clean(words[i]);
                }
                if (config.isRemoveStopwords()) {
                    if (sw.isStopWord(words[i])) {
                        words[i] = null;
                        continue;
                    }
                }
                if (config.isStemmed()) {
                    if (config.getLanguage().equalsIgnoreCase(PreProcessingConfig.Language.PORTUGUESE.toString())) {
                        words[i] = stemPt.getWordStem(words[i]);
                    } else if (config.getLanguage().equalsIgnoreCase(PreProcessingConfig.Language.ENGLISH.toString())) {
                        words[i] = StemmerEn.get(words[i]);
                    }
                }
            }
            // word counting
            for (String word : words) {
                if (word != null) {
                    if (termDf.containsKey(word)) {
                        termDf.put(word, termDf.get(word) + 1);
                    } else {
                        termDf.put(word, 1);
                    }
                }
            }
            newInputArray[pos] = new InputPattern(input.getId(), input.getTexto(), input.getClasse());
            vetsWords[pos] = words;
            pos++;
        }
        // remove min DF words
        for (pos = 0; pos < lInput.size(); pos++) {
            StringBuilder sb = new StringBuilder();
            for (String word : vetsWords[pos]) {
                if (termDf.containsKey(word) && termDf.get(word) >= config.getDfMin()) {
                    sb.append(word);
                    sb.append(" ");
                }
            }
            newInputArray[pos].setTexto(sb.toString().trim());
        }

        return Arrays.asList(newInputArray);
    }

    public static void main(String args[]) {
        List<InputPattern> lInput = new ArrayList<>();
        lInput.add(new InputPattern(0, "oi, como vai você?", "a"));
        lInput.add(new InputPattern(1, "oi, caminhão carro motor oi oi", "b"));
        lInput.add(new InputPattern(2, "caminhão caminhão motor motor oi oi", "b"));

        PreProcessingConfig config = new PreProcessingConfig(PreProcessingConfig.Language.PORTUGUESE.toString(), true, 1, true, true, true, true, true);

        List<InputPattern> l = Preprocessing.preprocess(lInput, config);

        System.out.println(l);
        l = ContextExpasion.expand(l);
        System.out.println(l);
        Data data = Conversor.listInputPatternToData(l, config);
        System.out.println(data.getTerms());
        System.out.println(data);
        String s = Conversor.dataToStrArff(data);
        System.out.println(s);
    }

}
