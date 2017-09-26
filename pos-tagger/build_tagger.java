import java.util.*;
import java.io.*;


public class build_tagger {
  public static double[][] tagMatrix = new double[47][47];

  public static void initializeMatrix(){
    for(int i=0; i<47; i++){
      for(int j=0; j<47; j++){
        tagMatrix[i][j] = 0.0;
      }
    }
  }

  public static void printTagsMatrix(){
    String line = null;
    initializeMatrix();
    Map<String, Integer> tagID = new HashMap<String, Integer>();
    try{
      BufferedReader br = new BufferedReader(new FileReader("penn_tags.txt"));
      while((line=br.readLine()) != null){
        String[] segment = line.split(" ");
        tagID.put(segment[1], Integer.valueOf(segment[0]));
        //System.out.println(line);
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
    System.out.println(tagID.entrySet().toString());
    for(int i=0; i<=46; i++){
      for(int j=0; j<=46; j++){
        System.out.print(tagMatrix[i][j]);
      }
    }
  }


  public static void main(String[] args){
    String trainFileName = args[0];
    String devtFileName = args[1];
    String modelFileName = args[2];
    String line = null;
    printTagsMatrix();
    try{
      // BufferedReader br = new BufferedReader(new FileReader(trainFileName));
      // while((line=br.readLine()) != null){

        // System.out.println(line);
      // }
      // devt = new FileInputStream(devtFileName);
      // FileOutputStream out = new FileOutputStream(modelFileName);
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
    System.out.println(trainFileName + " " + devtFileName + " " + modelFileName);
  }

  
}
