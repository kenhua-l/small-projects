import java.util.*;
import java.io.*;

public class tc_train {
  public static Set<String> stopWords = new HashSet<String>();

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

  public static void readTrainingFile(String fileName){
    // Read file content of fileName
    try{
      String trainLine = null;
      BufferedReader tbr = new BufferedReader(new FileReader(fileName));
      // Read line by line
      while((trainLine=tbr.readLine()) != null){
        // remove non-alphabetic words, trim white spaces and lowercase the words
        trainLine = trainLine.replaceAll("[^a-zA-Z ]", " ").trim().toLowerCase();
        // ignore if it is blank - empty string
        if(!trainLine.equals("")){
          Vector<String> processedWordList = new Vector<String>();
          String[] rawWordList = trainLine.split("\\s+");
          // System.out.println(Arrays.toString(rawWordList));
          for (String word: rawWordList){
            // remove all stop words
            if(!stopWords.contains(word)){
              Stemmer stem = new Stemmer();
              stem.add(word.toCharArray(), word.length());
              stem.stem();
              System.out.println(stem.toString());
              processedWordList.add(stem.toString());
            }
          }
          for(String word: processedWordList){
            System.out.print(word + " ");
          }
          System.out.println();
        }
      }
      tbr.close();
    }catch(Exception e2){
      System.err.println(e2 + ": no file to read in within readTrainClass");
    }
  }

  public static void readTrainClassList(String fileName){
    String line = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      // Read training file name from list
      while((line=br.readLine()) != null){
        String[] lineSegment = line.split(" ");
        String trainingFile = lineSegment[0];
        String trainClass = lineSegment[1];
        readTrainingFile(trainingFile);
        System.out.println(trainingFile + " " + trainClass); //Check
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
    setStopWordList(stopWords);
    readTrainClassList(trainClass);
  }
}
