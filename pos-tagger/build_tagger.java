import java.util.*;
import java.io.*;


public class build_tagger {
  public static int[][] tagMatrix = new int[47][47];
  public static String[] tags = new String[47];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();

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
    // String word = null;
    // String tag = null;
    for(int i=0; i<segmented.length; i++){
      // String[] wordTag = segmented[i].split("/");
      // String word = wordTag[0];
      // String tag = wordTag[1];
      // System.out.print(word + "-->" + tag);
      // if(i==0){
        // tagMatrix[tagID.get("<s>")][tagID.get(tag)]++;
      // }
      System.out.print(segmented[i] + "     ");
    }
    // tagMatrix[tagID.get(tag)][tagID.get("</s>")]++;
    System.out.println();
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
  }

}
