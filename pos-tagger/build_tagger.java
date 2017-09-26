import java.util.*;
import java.io.*;


public class build_tagger {
  public static int[][] tagMatrix = new int[47][47];
  public static String[] tags = new String[47];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static Set<String> vocabulary = new HashSet<String>();
  // public static Map<String, ArrayList<Integer>> vocabularyMatrix = new HashMap<String, ArrayList<Integer>>();

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

  // public static void printVocabMatrix(){
  //   System.out.print("- ");
  //   for(int i=1; i<=45; i++){
  //     System.out.print(tags[i] + " ");
  //   }
  //   System.out.println();
  //   for (Map.Entry entry : vocabularyMatrix.entrySet()) {
  //       System.out.println(entry.getKey() + " " + entry.getValue());
  //   }
  // }

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
      vocabulary.add(word);
      // vocabularyMatrix.put(word, new ArrayList<Integer>(Collections.nCopies(45, 0)));
      // vocabularyMatrix.get(word).add(tagID.get(tag)-1, )
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
    printTagsMatrix();
    System.out.println(vocabulary.size());
    // printVocabMatrix();
    calculateProbabilityTagMatrix();
    printProbabilityTagsMatrix();
  }
}
