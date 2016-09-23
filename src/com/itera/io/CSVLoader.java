/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.io;

import com.itera.structures.InputPattern;
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

/**
 *
 * @author root
 */
public class CSVLoader implements Loader {

    private String fileName;
    private String sep;

    public CSVLoader(String fileName) {
        this(fileName, ",");
    }

    public CSVLoader(String fileName, String sep) {
        this.fileName = fileName;
        this.sep = sep;
    }

    @Override
    public List<InputPattern> load() {
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

}
