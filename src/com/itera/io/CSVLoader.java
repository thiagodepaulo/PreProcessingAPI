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
    private static final String STR_NUMERIC = "__NUMERIC__";

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

    @Override
    public DenseData loadDenseData() {
        try {
            HashMap<Integer, String[]> data = new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            String line = null;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if (this.headFirstLine && count == 0) {
                    count++;
                    continue;
                }
                String[] cols = line.split(this.sep);
                if (cols.length > 1) {
                    data.put(count++, cols);
                }
            }

            ArrayList<Feature> lFeatures = createFeatures(data);
            int classId = lFeatures.size() - 1;
            DenseData dd = new DenseData("Dataset", lFeatures, classId);

            for (int id : data.keySet()) {
                String[] cols = data.get(id);
                DenseExample ex = new DenseExample(dd);
                int col = 0;
                for (String s : cols) {
                    s = s.trim();
                    if (Tools.isNumeric(s)) {
                        ex.setValue(col, Double.parseDouble(s));
                    } else {
                        //System.out.println(col+" "+s);
                        ex.setValue(col, s);
                    }
                    col += 1;
                }
                dd.addExample(ex);
            }

            return dd;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<Feature> createFeatures(HashMap<Integer, String[]> data) {
        HashMap<Integer, HashSet<String>> colsCat = new HashMap<>();
        for (int id : data.keySet()) {
            int col = 0;
            for (String s : data.get(id)) {
                s = s.trim();
                if (!colsCat.containsKey(col)) {
                    colsCat.put(col, new HashSet<String>());
                }
                if (Tools.isNumeric(s)) {
                    colsCat.put(col, null);
                } else {
                    //System.out.println(col + " " + s);
                    colsCat.get(col).add(s);
                }
                col += 1;
            }
        }

        ArrayList<Feature> l = new ArrayList<>();
        for (int col : colsCat.keySet()) {
            Feature f = null;
            if (colsCat.get(col) != null) {
                HashSet<String> cat = colsCat.get(col);
                String[] strCat = new String[cat.size()];
                int i = 0;
                for (String s : cat) {
                    strCat[i++] = s;
                }
                f = new Feature(Feature.FeatureType.NOMINAL, "feat_" + col, strCat);
            } else {
                f = new Feature("feat_" + col);
            }
            l.add(f);
        }
        return l;
    }

    public static void main(String[] args) {
        String arq = "/home/thiagodepaulo/Downloads/bank.csv";
        CSVLoader loader = new CSVLoader(arq, ",", true);
        DenseData data = loader.loadDenseData();
        System.out.println(data.numExamples());
        System.out.println(data);
    }

}
