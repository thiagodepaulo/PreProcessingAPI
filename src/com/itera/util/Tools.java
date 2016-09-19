/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.util;

import com.itera.preprocess.tools.CharsetRecognition;
import com.itera.structures.TermFreq;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author root
 */
public class Tools {

    public static <T, P> HashMap<P, T> invertHashMap(HashMap<T, P> map) {
        HashMap<P, T> invMap = new HashMap<>();
        for (Map.Entry<T, P> entry : map.entrySet()) {
            invMap.put(entry.getValue(), entry.getKey());
        }
        return invMap;
    }

    public static String readFile(File file) {
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

}
