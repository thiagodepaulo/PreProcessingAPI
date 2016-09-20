/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.preprocess.tools;

import com.itera.preprocess.config.PreProcessingConfig;
import com.itera.structures.InputPattern;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import ptstemmer.exceptions.PTStemmerException;

/**
 *
 * @author root
 */
public class SerializedPreprocessing implements Serializable {

    private final Cleaner cln;
    private final ptstemmer.Stemmer stemPt;
    private final StopWords sw;
    private final PreProcessingConfig config;
    private static final String BLANK = " ";

    public SerializedPreprocessing(PreProcessingConfig config) throws PTStemmerException {
        this.cln = new Cleaner();
        this.sw = new StopWords(config.getLanguage()); //Objeto para remoção das stopwords dos documentos                        
        this.stemPt = new ptstemmer.implementations.OrengoStemmer();
        this.config = config;
    }

    public void preprocess(InputPattern input) {

        String[] words = input.getTexto().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() <= this.config.getWordLenghtMin()) {
                words[i] = null;
                continue;
            }
            if (this.config.isCleaning()) {
                words[i] = cln.clean(words[i]);
            }
            if (this.config.isRemoveStopwords()) {
                if (sw.isStopWord(words[i])) {
                    words[i] = null;
                    continue;
                }
            }
            if (this.config.isStemmed()) {
                if (this.config.getLanguage().equalsIgnoreCase(PreProcessingConfig.Language.PORTUGUESE.toString())) {
                    words[i] = stemPt.getWordStem(words[i]);
                } else if (this.config.getLanguage().equalsIgnoreCase(PreProcessingConfig.Language.ENGLISH.toString())) {
                    words[i] = StemmerEn.get(words[i]);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (config.getDfMin() > 0) {
            HashMap<String, Integer> termDf = new HashMap<>();
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
            // remove min DF words            
            for (String word : words) {
                if (termDf.containsKey(word) && termDf.get(word) >= config.getDfMin()) {
                    sb.append(word);
                    sb.append(BLANK);
                }
            }
        } else {
            for (String word : words) {
                if (word != null) {
                    sb.append(word);
                    sb.append(BLANK);
                }
            }
        }
        input.setTexto(sb.toString());
    }

    public static void main(String args[]) throws PTStemmerException {
        PreProcessingConfig config = new PreProcessingConfig("portuguese", true, 0, true, true, true, false, true);
        SerializedPreprocessing sp = new SerializedPreprocessing(config);

        InputPattern input = new InputPattern(0, "Oi, como vai você, tudo bem? CAdê a Manifestação?", "");

        sp.preprocess(input);

        System.out.println(input);
    }

}
