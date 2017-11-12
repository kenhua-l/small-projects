import java.util.*;
import java.io.*;

public class tc_train {

  public static void readStopWords(String fileName){
    String line = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      while((line=br.readLine()) != null){
        System.out.println(line);
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read in readStopWords");
    }
  }

  public static void readTrainClass(String fileName){
    String line = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      // Read training file name
      int i=1;
      while((line=br.readLine()) != null){
        System.out.println(i);
        String[] lineSegment = line.split(" ");
        String trainingFile = lineSegment[0];
        String trainClass = lineSegment[1];
        try{
          String trainLine = null;
          BufferedReader tbr = new BufferedReader(new FileReader(trainingFile));
          // Read lines for each training file - trime and split
          while((trainLine=tbr.readLine().trim()) != null){
            System.out.println(trainLine);
            if(!trainLine.equals("")){
              System.out.println("YAY");
            }
            // if(!trainLine.isEmpty()){
              // String[] trainLineSegment = trainLine.split("\\s+");
              // System.out.print(Arrays.toString(trainLineSegment));
              // System.out.print(" " + trainLineSegment.length);
              //For each word
              // for (String s : trainLineSegment){
                // Stemmer hello = new Stemmer();
                // hello.add(s.toCharArray(), s.length());
                // hello.stem();
                // System.out.println(hello.toString());
              // }
            // }
            // Stemmer hello = new Stemmer();
            // hello.add(trainLine.toCharArray(), trainLine.length());
            // hello.stem();
            // System.out.println(hello.toString());
          }
        }catch(Exception e2){
          System.out.println(trainingFile);
          System.err.println(e2 + ": no file to read in within readTrainClass");
        }
        System.out.println(trainingFile + " " + trainClass);
      }
    }catch(Exception e1){
        System.err.println(e1 + ": no file to read in readTrainClass");
    }
  }

  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWords = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];
    System.out.println(stopWords + " " + trainClass + " " + modelFileName);
    // Stemmer stem = new Stemmer();
    // readStopWords(stopWords);
    readTrainClass(trainClass);
  }
}
