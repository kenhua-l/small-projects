import java.util.*;
import java.io.*;

public class build_tagger {
  public static int[][] tagMatrix = new int[47][47];
  public static String[] tags = new String[47];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static Set<String> vocabulary = new HashSet<String>();
  public static Map<String, ArrayList<Integer>> vocabularyMatrix = new HashMap<String, ArrayList<Integer>>();

  public static void initializeMatrix(){
    for(int i=0; i<47; i++){
      for(int j=0; j<47; j++){
        tagMatrix[i][j] = 0;
        probabilityTagMatrix[i][j] = 0;
      }
    }
  }

  public static void readPenn(){
    String line = null;
    int index = 0;
    try{
      BufferedReader br = new BufferedReader(new FileReader("penn_tags.txt"));
      while((line=br.readLine()) != null){
        String[] segment = line.split(" ");
        tagID.put(segment[1], Integer.valueOf(segment[0]));
        tags[index++] = segment[1];
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
  }

  public static void printTagsMatrix(){
    System.out.print("-, ");
    for(int i=0; i<=46; i++){
      System.out.print(tags[i] + ", ");
    }
    System.out.println();
    for(int i=0; i<=46; i++){
      System.out.print(tags[i]+ ", ");
      for(int j=0; j<=46; j++){
        System.out.print(tagMatrix[i][j] + ", ");
      }
      System.out.println();
    }
  }

  public static void printVocabMatrix(Map<String, ArrayList<Double>> matrix){
    System.out.print("- ");
    for(int i=1; i<=45; i++){
      System.out.print(tags[i] + " ");
    }
    System.out.println();
    for (Map.Entry entry : matrix.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }

  public static void addCountToMatrix(String line){
    String[] segmented = line.split(" ");
    String word = null;
    String tag = null;
    String prevTag = null;
    for(int i=0; i<segmented.length; i++){
      String[] wordTag = segmented[i].split("/");
      word = wordTag[0];
      if(wordTag.length > 2){
        for(int j=1; j<wordTag.length-1; j++){
          word += "/" + wordTag[j];
        }
        tag = wordTag[wordTag.length-1];
      }else{
        tag = wordTag[1];
      }

      if(vocabulary.contains(word)){
        ArrayList<Integer> arr = vocabularyMatrix.get(word);
        arr.set(tagID.get(tag)-1, arr.get(tagID.get(tag)-1)+1);
        vocabularyMatrix.put(word, arr);
      } else {
        vocabulary.add(word);
        ArrayList<Integer> arrOfOccurrence = new ArrayList<Integer>(Collections.nCopies(45, 0));
        arrOfOccurrence.set(tagID.get(tag)-1, 1);
        vocabularyMatrix.put(word, arrOfOccurrence);
      }

      if(i==0){
        tagMatrix[tagID.get("<s>")][tagID.get(tag)]++;
      } else {
        tagMatrix[tagID.get(prevTag)][tagID.get(tag)]++;
      }
      prevTag = tag;
    }
    tagMatrix[tagID.get(tag)][tagID.get("</s>")]++;
  }

  //For runTagger
  //Smoothing
  public static double[][] probabilityTagMatrix = new double[47][47];
  public static Map<String, ArrayList<Double>> duplicateVocab = new HashMap<String, ArrayList<Double>>();

  public static void smoothenVocabularyMatrix(){
    int vT = 45;
    for (Map.Entry<String, ArrayList<Integer>> vocab : vocabularyMatrix.entrySet()){
      String word = vocab.getKey();
      ArrayList<Double> freq = new ArrayList<Double>();
      for(Integer num : vocab.getValue()){
          freq.add(Double.valueOf(num));
      }
      double cT = 0;
      double tT = 0;
      for(int i=0; i<45; i++){
        if(freq.get(i) > 0){
          tT++;
          cT += freq.get(i);
        }
      }
      double zT = vT - tT;
      for(int i=0; i<45; i++){
        double cTt = freq.get(i);
        if(freq.get(i) > 0){
          freq.set(i, cTt / (cT + tT));
        }else{
          freq.set(i, tT / ((cT + tT) * zT));
        }
        duplicateVocab.put(word, freq);
      }
      //vocabularyMatrix = duplicateVocab;
      // System.out.println(vocab.getKey() + "/" + vocab.getValue());
    }
  }

  public static void calculateProbabilityTagMatrix(){
    // <-Tag1Tag2^
    // probability of Tag2 given Tag1
    // cTt is the tag bigram count Tag1Tag2
    // cT is the frequency Tag1 appears (sum up column or row is ok)
    // tT is the number of types of tags following T
    // V is 45 so Z is V-tT
    int vT = 45;
    for(int i=0; i<46; i++){
      double cT = 0;
      double tT = 0;
      for(int j=1; j<47; j++){
        if(tagMatrix[i][j] > 0) {
          tT++;
          cT += tagMatrix[i][j];
        }
      }
      double zT = vT - tT;
      for(int j=1; j<47; j++){
        double cTt = tagMatrix[i][j];
        if(tagMatrix[i][j] > 0){
          probabilityTagMatrix[i][j] = cTt / (cT + tT);
        }else{
          probabilityTagMatrix[i][j] = tT / ((cT + tT) * zT);
        }
      }
    }
  }

  public static void printProbabilityTagsMatrix(){
    System.out.print("-, ");
    for(int i=0; i<=46; i++){
      System.out.print(tags[i] + ", ");
    }
    System.out.println();
    for(int i=0; i<=46; i++){
      System.out.print(tags[i]+ ", ");
      for(int j=0; j<=46; j++){
        System.out.print(probabilityTagMatrix[i][j] + ", ");
      }
      System.out.println();
    }
  }
  //

  public static void printModelFile(String modelFileName){
    BufferedWriter bw = null;
    try {
      FileOutputStream fos = new FileOutputStream(modelFileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      bw = new BufferedWriter(osw);
      ////////
      for(int i=1; i<=45; i++){
        bw.write(tags[i] + ", ");
      }
      bw.write(tags[46] + "\n");
      for(int i=0; i<=45; i++){
        for(int j=1; j<=45; j++){
          bw.write(probabilityTagMatrix[i][j] + ", ");
        }
        bw.write(probabilityTagMatrix[i][46] + "\n");
      }
      ////
      bw.write(vocabulary.size() + "\n");
      ////
      for (Map.Entry entry : duplicateVocab.entrySet()) {
          bw.write(entry.getKey() + "-->" + entry.getValue() + "\n");
      }

    } catch(Exception e){
      System.err.println(e + ": cannot write");
    } finally {
      try{
        bw.close();
      }catch(Exception e){
        System.err.println(e + ": cannot write");
      }
    }
  }

  public static void main(String[] args){
    String trainFileName = args[0];
    String devtFileName = args[1];
    String modelFileName = args[2];
    String line = null;
    initializeMatrix();
    readPenn();
    try{
      BufferedReader br = new BufferedReader(new FileReader(trainFileName));
      while((line=br.readLine()) != null){
        addCountToMatrix(line);
      }
      // devt = new FileInputStream(devtFileName);
      // FileOutputStream out = new FileOutputStream(modelFileName);
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
    // printTagsMatrix();
    System.out.println(vocabulary.size());
    // printVocabMatrix(vocabularyMatrix);
    smoothenVocabularyMatrix();
    // printVocabMatrix(duplicateVocab);
    calculateProbabilityTagMatrix();
    // printProbabilityTagsMatrix();
    printModelFile(modelFileName);
  }
}
