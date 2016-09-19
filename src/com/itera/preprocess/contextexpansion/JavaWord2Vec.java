package com.itera.preprocess.contextexpansion;

import com.itera.util.VectorOps;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

public class JavaWord2Vec {

    private HashMap<String, Integer> vocab;
    private HashMap<Integer, String> invVocab;
    private float[][] vecs;

    public int topN = 10;

    private final static String VOCAB_FILE_NAME = "vocab.txt";
    private final static String VECTORS_FILE_NAME = "vecs.dat";

    public JavaWord2Vec() {

    }

    public List<Pair<String, float[]>> mostSimilar(String... words) {
        // calculate vector mean for all word in vocabulary and words
        List<Integer> listWIds = new ArrayList<>(words.length);
        List<float[]> listWvecs = new ArrayList<>(words.length);
        int wid;
        for (String word : words) {
            if (this.vocab.containsKey(word)) {
                wid = this.vocab.get(word);
                listWvecs.add(this.vecs[wid]);
                listWIds.add(wid);
            }
        }
        // if do not contain word in vocabulary!
        if (listWvecs.isEmpty()) {
            return null;
        }
        float[] vecMean = VectorOps.mean(listWvecs);
        listWvecs = null;
        VectorOps.unitVec(vecMean);
        
        // get the most similar words
        double[] similarities = VectorOps.dot(vecMean, this.vecs);
        int[] sortedIdx = VectorOps.argsort(similarities, false);
        List<Pair<String, float[]>> l = new ArrayList<>(this.topN);
        int i = 0;
        while (l.size() < this.topN) {
            wid = sortedIdx[i++];
            if (false == listWIds.contains(wid)) {
                Pair<String, float[]> wordVec = new Pair<>(this.invVocab.get(wid), this.vecs[wid]);
                l.add(wordVec);
            }
        }
        return l;
    }

    public double similarity(String w1, String w2) {
        if (!this.vocab.containsKey(w1) || !this.vocab.containsKey(w2)) {
            return -1;
        }
        float[] v1 = this.vecs[this.vocab.get(w1)];
        float[] v2 = this.vecs[this.vocab.get(w2)];
        return VectorOps.dot(v1, v2) / (Math.sqrt(VectorOps.squaredSum(v1)) * Math.sqrt(VectorOps.squaredSum(v2)));
    }

    public double similarity(List<String> l1, List<String> l2) {
        List<float[]> lvecs1 = new ArrayList<>(l1.size());
        List<float[]> lvecs2 = new ArrayList<>(l2.size());
        for (String w1 : l1) {
            if (this.vocab.containsKey(w1)) {
                lvecs1.add(this.vecs[this.vocab.get(w1)]);
            }
        }
        for (String w2 : l2) {
            if (this.vocab.containsKey(w2)) {
                lvecs2.add(this.vecs[this.vocab.get(w2)]);
            }
        }
        float[] v1 = VectorOps.mean(lvecs1);
        float[] v2 = VectorOps.mean(lvecs2);
        return VectorOps.dot(v1, v2) / (Math.sqrt(VectorOps.squaredSum(v1)) * Math.sqrt(VectorOps.squaredSum(v2)));
    }

    public static float[][] readByteMatrix(File arq) throws IOException {
        DataInputStream din = new DataInputStream(new FileInputStream(arq));
        byte bytes[] = new byte[4];
        din.read(bytes);
        float f = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        int num_rows = (int) f;
        din.read(bytes);
        f = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        int num_cols = (int) f;
        float[][] vecs = new float[num_rows][num_cols];
        for (int i = 0; i < num_rows; i++) {
            for (int j = 0; j < num_cols; j++) {
                din.read(bytes);
                vecs[i][j] = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
            }
        }
        din.close();
        return vecs;
    }

    public static HashMap<String, Integer> readDictionary(File arq) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileInputStream(arq));
        HashMap<String, Integer> map = new HashMap<>();
        String str = null;
        int c = 0;
        while (scan.hasNextLine()) {
            str = scan.nextLine().trim();
            map.put(str, c++);
        }
        scan.close();
        return map;
    }

    public static JavaWord2Vec load(String dir) throws IOException {
        System.out.println("Carregando expansor de contexto...");
        File vocabArq = new File(dir, VOCAB_FILE_NAME);
        File vecArq = new File(dir, VECTORS_FILE_NAME);
        JavaWord2Vec model = new JavaWord2Vec();
        model.setVocab(readDictionary(vocabArq));
        model.vecs = readByteMatrix(vecArq);
        
        return model;
    }

    public HashMap<String, Integer> getVocab() {
        return vocab;
    }

    public void setVocab(HashMap<String, Integer> vocab) {
        this.vocab = vocab;
        this.invVocab = invertVocabulary(vocab);
    }

    private static HashMap<Integer, String> invertVocabulary(HashMap<String, Integer> vocab) {
        HashMap<Integer, String> invVocab = new HashMap<>();
        for (Entry<String, Integer> e : vocab.entrySet()) {
            invVocab.put(e.getValue(), e.getKey());
        }
        return invVocab;
    }

    public static void main(String[] args) throws IOException {
        JavaWord2Vec m = JavaWord2Vec.load("/media/thiagodepaulo/Dados/Thiago/wordEmbedding/wiki2vec/wiki2vec/out");
        System.out.println("calculando similarity");
        System.out.println(m.similarity("thiago", "batalha"));
        System.out.println(m.mostSimilar("carro", "foguete"));
        System.out.println(m.mostSimilar("bradesco","cartão"));

        //List<String> l1 = Arrays.asList("brasil", "thiago");
        //List<String> l2 = Arrays.asList("país", "capital");
        //System.out.println(m.similarity(l1, l2));
    }
}
