/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itera.test;

import com.itera.structures.Conversor;
import com.itera.structures.TextData;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author root
 */
public class ArffTeste {
    
    public static void main(String[] args) throws FileNotFoundException {
        String arffArqName = "/home/thiagodepaulo/teste_jurídico.arff";
        TextData data = Conversor.arffToData(arffArqName);
        String str = Conversor.textDataToStrArff(data);
        PrintWriter pw = new PrintWriter("/home/thiagodepaulo/teste.arff");
        pw.write(str);
        pw.close();
    }
    
}
