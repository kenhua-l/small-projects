import java.util.*;
import java.io.*;

public class build_tagger {
  public static int[][] tagMatrix = new int[47][47];
  public static String[] tags = new String[47];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static Set<String> vocabulary = new HashSet<String>();
  public static List<Map<String,Integer>> vocabularyMatrix = new ArrayList<Map<String,Integer>>();

  public static void initializeMatrix(){
    for(int i=0; i<47; i++){
      for(int j=0; j<47; j++){
        tagMatrix[i][j] = 0;
        probabilityTagMatrix[i][j] = 0;
      }
      if(i<45){
        vocabularyMatrix.add(new HashMap<String, Integer>());
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

  public static void printVocabMatrix(List<Map<String,Double>> matrix){
    for(int i=1; i<matrix.size(); i++){
      System.out.println(tags[i]);
      for(Map.Entry vocab : matrix.get(i-1).entrySet()){
        System.out.println(vocab.getKey() + "->" + vocab.getValue());
      }
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

      if(vocabularyMatrix.get(tagID.get(tag)-1).keySet().contains(word.toLowerCase())){ //tlc
        Map<String, Integer> vocabList = vocabularyMatrix.get(tagID.get(tag)-1);
        int prevFreq = vocabList.get(word.toLowerCase()); //tlc
        vocabList.put(word.toLowerCase(), prevFreq+1); //tlc
      } else {
        vocabulary.add(word.toLowerCase()); //tlc
        Map<String, Integer> vocabList = vocabularyMatrix.get(tagID.get(tag)-1);
        vocabList.put(word.toLowerCase(), 1); //tlc
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
  public static List<Map<String, Double>> duplicateVocab = new ArrayList<Map<String, Double>>();

  public static void smoothenVocabularyMatrix(){
    // cTt=raw freq of word in that tag
    // cT=total freq in that tag
    // tT=unique words in that tag
    // vT=vocab size. so zT=vT-tT
    int vT = vocabulary.size();
    for (int i=0; i<45; i++){
      double tT = vocabularyMatrix.get(i).keySet().size();
      double cT = 0;
      double zT = vT - tT;
      duplicateVocab.add(new HashMap<String, Double>());
      for(int k : vocabularyMatrix.get(i).values()){
        cT += k;
      }
      Map<String, Double> newSet = duplicateVocab.get(i);
      for(Map.Entry vocab : vocabularyMatrix.get(i).entrySet()){
        double smoothedProb = (int) vocab.getValue() / (cT + tT);
        newSet.put(String.valueOf(vocab.getKey()), smoothedProb);
      }
      double unseenProb = tT / ((cT + tT) * zT);
      newSet.put("unknownUnseenWords", unseenProb);
    }
  }

  public static void calculateProbabilityTagMatrix(){
    // <-Tag1Tag2^
    // probability of Tag2 given Tag1
    // cTt is the tag bigram count Tag1Tag2
    // cT is the frequency Tag1 appears (sum up column or row is ok)
    // tT is the number of types of tags following T
    // V is 45 so Z is V-tT
    int vT = 47;
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
      for(int i=0; i<45; i++){
        bw.write(tags[i+1]+ " " + duplicateVocab.get(i).keySet().size() + "\n");
        for (Map.Entry entry : duplicateVocab.get(i).entrySet()){
          bw.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
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
    // java build_tagger sents.train sents.devt model_file
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
      // BufferedReader brd = new BufferedReader(new FileReader(devtFileName));
      // while((line=brd.readLine()) != null){
      //   addCountToMatrix(line);
      // }

    }catch(Exception e){
        System.err.println(e + ": no file to read in main");
    }
    // printTagsMatrix();
    // System.out.println(vocabulary.size());
    // System.out.println(vocabulary.contains("unknownUnseenWords"));
    // printVocabMatrix(vocabularyMatrix);
    smoothenVocabularyMatrix();
    // printVocabMatrix(duplicateVocab);
    calculateProbabilityTagMatrix();
    // printProbabilityTagsMatrix();
    // System.out.println("JUST TO MAKE SURE: VOCABMATRIXSIZE is " + vocabularyMatrix.size());
    printModelFile(modelFileName);
  }
}
