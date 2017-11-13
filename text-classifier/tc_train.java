import java.util.*;
import java.io.*;

public class tc_train {
  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  public static Set<String> vocabulary = new HashSet<String>();   // Global vocab list
  public static Map<String, Integer> classTextFrequency = new HashMap<String, Integer>(); // Number of text for class
  public static Map<String, Map<String, Integer>> classWordTextNumber = new HashMap<String, Map<String, Integer>>();  // Number of text for word and class

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

  public static void readTrainingFile(String fileName, String trainClass){
    // Read file content of fileName
    Set<String> vocabInText = new HashSet<String>();
    try{
      String trainLine = null;
      BufferedReader tbr = new BufferedReader(new FileReader(fileName));
      // Read line by line
      while((trainLine=tbr.readLine()) != null){
        // remove non-alphabetic words, trim white spaces and lowercase the words
        trainLine = trainLine.replaceAll("[^a-zA-Z ]", " ").trim().toLowerCase();
        // ignore if it is blank - empty string
        if(!trainLine.equals("")){
          String[] rawWordList = trainLine.split("\\s+");
          for (String word: rawWordList){
            // remove all stop words
            if(!stopWords.contains(word)){
              Stemmer stem = new Stemmer();
              stem.add(word.toCharArray(), word.length());
              // Stem the word
              stem.stem();
              String stemmedWord = stem.toString();
              vocabulary.add(stemmedWord);  // add to global vocab list
              vocabInText.add(stemmedWord); // add to local file vocab list
            }
          }
        }
      }
      tbr.close();
    }catch(Exception e2){
      System.err.println(e2 + ": no file to read in within readTrainClass");
    }

    // Setting up the X2 value, N
    for(String word: vocabInText){
      if(classWordTextNumber.get(trainClass) == null){
        Map<String, Integer> value = new HashMap<String, Integer>();
        value.put(word, 1);
        classWordTextNumber.put(trainClass, value);
      }else{
        if(classWordTextNumber.get(trainClass).get(word) == null){
          classWordTextNumber.get(trainClass).put(word, 1);
        }else{
          classWordTextNumber.get(trainClass).put(word, classWordTextNumber.get(trainClass).get(word) + 1);
        }
      }
    }

  }

  public static void readTrainClassList(String fileName){
    String line = null;
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      // Read training file name from list
      int numberOfTrainingText = 0;
      while((line=br.readLine()) != null){
        numberOfTrainingText++;
        String[] lineSegment = line.split(" ");
        String trainingFile = lineSegment[0];
        String trainClass = lineSegment[1];
        if(classTextFrequency.get(trainClass) == null){
          classTextFrequency.put(trainClass, 1);
        }else{
          classTextFrequency.put(trainClass, classTextFrequency.get(trainClass) + 1);
        }
        readTrainingFile(trainingFile, trainClass);
        // System.out.println(trainingFile + " " + trainClass); //Check
      }
      // System.out.println(numberOfTrainingText);
    }catch(Exception e1){
        System.err.println(e1 + ": no file to read in readTrainClassList");
    }
  }

  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWords = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];
    // System.out.println(stopWords + " " + trainClass + " " + modelFileName);
    setStopWordList(stopWords);
    readTrainClassList(trainClass);
    // for(String word : vocabulary){
      // System.out.print(word + " ");
    // }
    for(Map.Entry<String, Map<String, Integer>> vocab : classWordTextNumber.entrySet()){
      System.out.println(vocab.getKey() + "->" + vocab.getValue());
    }
    for(Map.Entry<String, Integer> freq : classTextFrequency.entrySet()){
      System.out.println(freq.getKey() + "->" + freq.getValue());
    }
  }
}
