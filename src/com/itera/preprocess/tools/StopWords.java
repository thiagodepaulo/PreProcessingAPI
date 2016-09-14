//*****************************************************************************
// Author: Rafael Geraldeli Rossi
// E-mail: rgr.rossi at gmail com
// Last-Modified: January 29, 2015
// Description: 
//*****************************************************************************  

package com.itera.preprocess.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashSet;

public class StopWords {
    static HashSet<String> list = new HashSet<String>();
    
    
    
    public StopWords(String language){
        
        String locationStopFile = StopWords.class.getProtectionDomain().getCodeSource().getLocation().toString();
        System.out.println(locationStopFile);
        locationStopFile = locationStopFile.replace("\\", "/");
        locationStopFile = locationStopFile.substring(5, locationStopFile.lastIndexOf("/"));
        
        //System.out.println("LOCATION STOPFILE: " + locationStopFile);
        
        if(language.equals("portuguese")){
            try{
                
                File fileStopWords = new File(locationStopFile + "/" + "stopPort.txt");
                String charset = CharsetRecognition.Recognize(fileStopWords);
                BufferedReader arqStop = new BufferedReader(new InputStreamReader(new FileInputStream(fileStopWords), charset));
                
                String line = "";
                while((line = arqStop.readLine())!=null){
                    if(line.length()>0){
                        String elem = new String(line);
                        list.add(elem);
                    }
                }
                arqStop.close();
            }catch(Exception e){
                System.err.println("Error when reading stopwords file (stopPort.txt).");
                e.printStackTrace();
                System.exit(0);
            }   
        }else{
            try{
                RandomAccessFile arqStop = new RandomAccessFile(locationStopFile + "/" + "stopIngl.txt", "r");
                String line = "";
                while((line = arqStop.readLine())!=null){
                    if(line.length()>0){
                        String elem = new String(line);
                        list.add(elem);
                    }
                }
                arqStop.close();
            }catch(Exception e){
                System.err.println("Error when reading stopwords file (stopIngl.txt)");
                e.printStackTrace();
                System.exit(0);
            }
        }
        
    }
    
    public boolean isStopWord(String str){

        if(list.contains(str)){
            return true;
        }else{
            return false;
        }
    }
    
    public String removeStopWords(String str){
        String[] terms = str.split(" ");
        String new_str = "";

        for(int i=0; i < terms.length; i++){
            String termo = terms[i];
            if(termo.startsWith("\n")){
                new_str = new_str.concat("\n");
            }
            termo = termo.trim();
            boolean quebra = false;
            if(termo.contains("\n")){
                quebra = true;
            }
            String[] terms2 = termo.split("\n");
            for(int j=0; j < terms2.length; j++){
                String termo2 = terms2[j].trim();
                if(j == terms2.length - 1){
                    quebra = false;
                }
                if(!isStopWord(termo2)){
                        if(termo2.equals(".")){
                            new_str=new_str.concat(" . ");
                            if(quebra == true){new_str=new_str.concat("\n");}
                            continue;
                        }
                        if(!(termo2.length()<=2)){
                            new_str=new_str.concat(termo2+" ");
                            if(quebra == true){new_str=new_str.concat("\n");}
                            continue;
                        }   

                }else{
                    new_str = new_str + " @ ";
                    if(quebra == true){new_str=new_str.concat("\n");}
                }    
            }
        }
        return  new_str.trim();
        
    }
}
