/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.preprocess.config;

import java.io.Serializable;

/**
 *
 * @author Gaia
 */
public class PreProcessingConfig implements Serializable {

    private String language;
    private boolean doStemming;
    private int dfMin;
    private boolean calcTfidf;
    private boolean directoryAsClasses;
    private boolean removeStopwords;
    private boolean translateEN;
    private boolean doCleaning;
    
    public enum Language { 
        
        PORTUGUESE("portuguese"), ENGLISH("english");
        
        private final String selectedLanguage;
        
        private Language(String lang) {
            this.selectedLanguage = lang;
        }
        
        @Override
        public String toString() {
            return this.selectedLanguage;
        }
    
    };

    /**
     * 
     * @param language
     * @param doStemming
     * @param dfMin
     * @param calcTfidf
     * @param directoryAsClasses
     * @param removeStopwords
     * @param translateEN
     * @param doCleaning 
     */
    public PreProcessingConfig(String language, boolean doStemming, int dfMin, boolean calcTfidf, boolean directoryAsClasses, boolean removeStopwords, boolean translateEN, boolean doCleaning) {
        this.language = language;
        this.doStemming = doStemming;
        this.dfMin = dfMin;
        this.calcTfidf = calcTfidf;
        this.directoryAsClasses = directoryAsClasses;
        this.removeStopwords = removeStopwords;
        this.translateEN = translateEN;
        this.doCleaning = doCleaning;
    }

    public PreProcessingConfig createDefaultPreProcessingConfig() {
        return null;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isStemmed() {
        return doStemming;
    }

    public void setStemming(boolean doStemming) {
        this.doStemming = doStemming;
    }

    public int getDfMin() {
        return dfMin;
    }

    public void setDfMin(int dfMin) {
        this.dfMin = dfMin;
    }

    public boolean isTfidf() {
        return calcTfidf;
    }

    public void setCalcTfidf(boolean calcTfidf) {
        this.calcTfidf = calcTfidf;
    }

    public boolean isDirectoryAsClasses() {
        return directoryAsClasses;
    }

    public void setDirectoryAsClasses(boolean directoryAsClasses) {
        this.directoryAsClasses = directoryAsClasses;
    }

    public boolean isRemoveStopwords() {
        return removeStopwords;
    }

    public void setRemoveStopwords(boolean removeStopwords) {
        this.removeStopwords = removeStopwords;
    }

    public boolean isTranslateEN() {
        return translateEN;
    }

    public void setTranslateEN(boolean translateEN) {
        this.translateEN = translateEN;
    }

    public boolean isCleaning() {
        return doCleaning;
    }

    public void setDoCleaning(boolean doCleaning) {
        this.doCleaning = doCleaning;
    }

}
