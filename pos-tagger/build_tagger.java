import java.util.*;
import java.io.*;


public class build_tagger {
  public static int[][] tagMatrix = new int[47][47];
  public static String[] tags = new String[47];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static Set<String> vocabulary = new HashSet<String>();

  public static void initializeMatrix(){
    for(int i=0; i<47; i++){
      for(int j=0; j<47; j++){
        tagMatrix[i][j] = 0;
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
    System.out.print("     ");
    for(int i=0; i<=46; i++){
      System.out.print(tags[i] + " ");
    }
    System.out.println();
    for(int i=0; i<=46; i++){
      System.out.print(tags[i]+ " ");
      for(int j=0; j<=46; j++){
        System.out.print(tagMatrix[i][j] + " ");
      }
      System.out.println();
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
      vocabulary.add(word);
      if(i==0){
        tagMatrix[tagID.get("<s>")][tagID.get(tag)]++;
      } else {
        tagMatrix[tagID.get(prevTag)][tagID.get(tag)]++;
      }
      prevTag = tag;
    }
    tagMatrix[tagID.get(tag)][tagID.get("</s>")]++;
  }

  public static void main(String[] args){
    String trainFileName = args[0];
    String devtFileName = args[1];
    String modelFileName = args[2];
    String line = null;
    initializeMatrix();
    readPenn();
    // printTagsMatrix();
    try{
      BufferedReader br = new BufferedReader(new FileReader(trainFileName));
      while((line=br.readLine()) != null){
        addCountToMatrix(line);
        // System.out.println(line);
      }
      // devt = new FileInputStream(devtFileName);
      // FileOutputStream out = new FileOutputStream(modelFileName);
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
    printTagsMatrix();
    System.out.println(vocabulary.size());
    // Iterator<String> itr=vocabulary.iterator();
    // while(itr.hasNext()){
    //     System.out.println(itr.next());
    // }
  }
}
