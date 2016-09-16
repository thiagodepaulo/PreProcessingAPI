//*****************************************************************************
// Author: Rafael Geraldeli Rossi
// E-mail: rgr.rossi at gmail com
// Last-Modified: January 29, 2015
// Description: 
//*****************************************************************************
package com.itera.io;

import java.io.File;
import java.util.ArrayList;

public class ListFiles {

    public static void List(File dirIn, ArrayList<File> filesIn) {
        File[] files = dirIn.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                filesIn.add(files[i]);
            } else {
                List(files[i], filesIn);
            }
        }
    }

    public static boolean List(File dirIn, File dirOut, ArrayList<File> filesIn, ArrayList<File> filesOut, File dirBase) {
        File[] files = dirIn.listFiles();
        for (int i = 0; i < files.length; i++) {
            //System.out.println("File: " + files[i]);
            if (files[i].isDirectory()) {
                File dirNameOut = new File(dirOut.toString() + files[i].toString().substring(dirBase.toString().length(), files[i].toString().length()));
                if (!dirNameOut.exists()) {
                    boolean criou = dirNameOut.mkdir();
                    if (criou == false) {
                        return false;
                    }
                }
                List(files[i], dirOut, filesIn, filesOut, dirBase);
            }
            if (!files[i].getName().endsWith("txt")) {
                continue;
            }
            String fileName = files[i].toString();
            String fileOut = dirOut.toString() + fileName.substring(dirBase.toString().length(), fileName.length());
            filesIn.add(new File(fileName));
            filesOut.add(new File(fileOut));
        }
        return true;
    }
}
