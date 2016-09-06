/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.io;

import com.itera.preprocess.tools.CharsetRecognition;
import com.itera.structures.InputPattern;
import com.itera.structures.TermFreq;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class DirectoryLoader implements Loader {

    private final String rootDir;
    public static final String EXT = ".txt";

    public DirectoryLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public List<InputPattern> load() {
        try {
            return loadCorpus(this.rootDir);
        } catch (IOException ex) {
            Logger.getLogger(DirectoryLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<InputPattern> loadCorpus(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        List<InputPattern> corpus = new ArrayList<>();
        int docId = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                String name = file.getName();
                String className = name.replace(" ", "-");
                File subdir = new File(directoryPath + File.separator + name);
                String[] subfiles = subdir.list();
                for (String subfile : subfiles) {
                    String content = readFile(new File(subdir.getAbsolutePath() + File.separator + subfile));
                    InputPattern doc = new InputPattern(docId++, content, className);
                    corpus.add(doc);
                }
            }
        }
        return corpus;
    }

    public String readFile(File file) {
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

        } catch (Exception e) {
            System.err.println("Error when reading the file " + file.getAbsolutePath() + ".");
            e.printStackTrace();
            //System.exit(0);
        }

        return txt.toString();
    }

    public static void main(String args[]) {
        Loader loader = new DirectoryLoader("/home/thiagodepaulo/Modelo/");
        List<InputPattern> corpus = loader.load();
        System.out.println(corpus.size());
        System.out.println(corpus);
    }

}
