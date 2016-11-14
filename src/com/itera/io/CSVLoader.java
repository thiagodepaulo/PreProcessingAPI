/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.io;

import com.itera.structures.DenseData;
import com.itera.structures.DenseExample;
import com.itera.structures.Feature;
import com.itera.structures.InputPattern;
import com.itera.util.Tools;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.itera.util.Tools.join;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author root
 */
public class CSVLoader implements Loader {

    private boolean headFirstLine = false;
    private String fileName;
    private String sep;

    public CSVLoader(String fileName) {
        this(fileName, ",", false);
    }

    public CSVLoader(String fileName, String sep) {
        this(fileName, sep, false);
    }

    public CSVLoader(String fileName, String sep, boolean headFirstLine) {
        this.fileName = fileName;
        this.sep = sep;
        this.headFirstLine = headFirstLine;
    }

    @Override
    public List<InputPattern> loadTextualData() {
        try {
            List<InputPattern> l = new ArrayList<>();
            int i = 0;
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(this.sep);
                l.add(new InputPattern(i++, join(" ", Arrays.asList(Arrays.copyOf(cols, cols.length - 1))),
                        cols[cols.length - 1].trim()));
            }
            return l;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String args[]) {
        String s = "oi, como vai você? | casa";
        String[] v = s.split("\\|");
        System.out.println(Arrays.asList(v));
    }

    @Override
    public DenseData loadDenseData() {
        try {
            ArrayList<InputPattern> l = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            String line = null;
            int lineCount = 0;
            ArrayList<Feature> lFeatures = new ArrayList<>();
            HashMap<Integer, Set<String>> typeCat = new HashMap<>();
            ArrayList<Feature.FeatureType> lFeatType = new ArrayList<>();
            ArrayList<String[]> lExamples = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (lineCount == 0 && this.headFirstLine) {
                    lineCount++;
                    continue;
                }                
                String[] cols = line.split(this.sep);
                lExamples.add(cols);                
                for (int j=0; j < cols.length; j++) {
                    cols[j] = cols[j].trim();
                    if (Tools.isNumeric(cols[j])) {
                        
                    } else {
                        if (!typeCat.containsKey(j)) {
                            typeCat.put(j, new HashSet<String>());
                        }
                        typeCat.get(j).add(cols[j]);
                    }
                }
            }
            DenseData dd = new DenseData("Dataset", lFeatures, lFeatures.size() - 1);
            return dd;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    

}
