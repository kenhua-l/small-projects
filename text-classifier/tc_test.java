import java.util.*;
import java.io.*;

public class tc_test {
  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  // public static 

  public static void setStopWordList(String fileName){
    try{
      String word = null;
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      while((word=br.readLine()) != null){
        word = word.trim();
        stopWords.add(word);
      }
    }catch(Exception e0){
        System.err.println(e0 + ": no file to read in readStopWords");
    }
    // System.out.println(stopWords.size()); // Check
  }

  public static void readModelFile(String fileName){
    try{
      String word = null;
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      while((word=br.readLine()) != null){
        System.out.println(word);
      }
    }catch(Exception e0){
        System.err.println(e0 + ": no file to read in readStopWords");
    }
  }

  public static void main(String[] args){
    // command is java tc_test stopword-list model test-list test-class-list
    String stopWordsFile = args[0];
    String modelFileName = args[1];
    String testList = args[2];
    String testClassList = args[3];
    // System.out.println(stopWordsFile + " " + modelFileName + " " + testList + " " + testClassList);
    setStopWordList(stopWordsFile);
    readModelFile(modelFileName);
    // for(String word : stopWords){
      // System.out.println(word);
    // }
  }
}
