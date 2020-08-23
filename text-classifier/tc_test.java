import java.util.*;
import java.io.*;

public class tc_test {
  public static final int FREQUENCY_NORMALIZATION_DENOMINATOR = 100;

  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  public static Vector<String> featureVector = new Vector<String>(); // input feature
  public static Vector<String> classNames = new Vector<String>(); // output class
  public static NeuralNet textClassifier;

  public static void setStopWordList(String fileName){
    try{
      String word = null;
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      while((word=br.readLine()) != null){
        word = word.trim();
        stopWords.add(word);
      }
      br.close();
    }catch(Exception e0){
        System.err.println(e0 + ": no file to read in readStopWords");
    }
    // System.out.println(stopWords.size()); // Check
  }

  public static void readModelFile(String fileName){
    try{
      String line = null;
      BufferedReader br = new BufferedReader(new FileReader(fileName));

      // read feature vector
      int numberOfFeatures = Integer.parseInt(br.readLine().trim());
      for(int i=0; i<numberOfFeatures; i++){
        featureVector.add(br.readLine().trim());
      }

      // read output vector
      int numberOfClasses = Integer.parseInt(br.readLine().trim());
      for(int i=0; i<numberOfClasses; i++){
        classNames.add(br.readLine().trim());
      }

      // setup NeuralNet
      textClassifier = new NeuralNet(numberOfFeatures, numberOfClasses);
      String[] inputLine = br.readLine().trim().split(" ");
      int oCount = Integer.parseInt(inputLine[0]);
      int iCount = Integer.parseInt(inputLine[1]);
      double[][] inputOutputWeight = new double[oCount][iCount];
      for(int o=0; o<oCount; o++){
        String[] longLine = br.readLine().trim().split(" ");
        for(int i=0; i<iCount; i++){
          inputOutputWeight[o][i] = Double.parseDouble(longLine[i]);
        }
      }
      textClassifier.setInputOutputWeight(inputOutputWeight);

      // int hiddenUnit = Integer.parseInt(br.readLine().trim().split(" ")[1]);
      // String[] inputLine = br.readLine().trim().split(" ");
      // int hCount = Integer.parseInt(inputLine[0]);
      // int iCount = Integer.parseInt(inputLine[1]);
      // double[][] inputHiddenWeight = new double[hCount][iCount];
      // for(int h=0; h<hCount; h++){
      //   String[] longLine = br.readLine().trim().split(" ");
      //   for(int i=0; i<iCount; i++){
      //     inputHiddenWeight[h][i] = Double.parseDouble(longLine[i]);
      //   }
      // }
      // textClassifier.setInputHiddenWeight(inputHiddenWeight);

      // inputLine = br.readLine().trim().split(" ");
      // int jCount = Integer.parseInt(inputLine[0]);
      // hCount = Integer.parseInt(inputLine[1]);
      // double[][] hiddenOutputWeight = new double[jCount][hCount];
      // for(int j=0; j<jCount; j++){
      //   String[] longLine = br.readLine().trim().split(" ");
      //   for(int h=0; h<hCount; h++){
      //     hiddenOutputWeight[j][h] = Double.parseDouble(longLine[h]);
      //   }
      // }
      // textClassifier.setHiddenOutputWeight(hiddenOutputWeight);
      // textClassifier.printNeuralNetFriendlyVersion();
      br.close();
    }catch(Exception e1){
        System.err.println(e1 + ": no file to read in readModelFile");
    }
  }

  public static double[] getInputVector(String fileName){
    Map<String, Double> trainTextWordFrequency = new HashMap<String, Double>();
    int numberOfWordsInText = 0;
    try{
      BufferedReader tbr = new BufferedReader(new FileReader(fileName));
      String trainLine = null;
      while((trainLine=tbr.readLine()) != null){
        trainLine = trainLine.replaceAll("[^a-zA-Z ]", " ").trim().toLowerCase();
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
              numberOfWordsInText++;
              if(featureVector.contains(stemmedWord)){
                if(trainTextWordFrequency.get(stemmedWord) == null){
                  trainTextWordFrequency.put(stemmedWord, 1.0);
                }else{
                  trainTextWordFrequency.put(stemmedWord, trainTextWordFrequency.get(stemmedWord) + 1.0);
                }
              }
            }
          }
        }
      }
      tbr.close();
    }catch(Exception e4){
      System.err.println(e4 + ": no file to read in neuralNetLearning inner loop");
    }

    // System.out.println("Number of words in text: " + numberOfWordsInText);
    // for(String key:trainTextWordFrequency.keySet()){
    //   System.out.println(key +" -> " + trainTextWordFrequency.get(key));
    // }
    // System.out.println();

    // Normalize word count as input
    double[] featureFrequencyInput = new double[featureVector.size() + 1];
    featureFrequencyInput[0] = 1.0;
    for(int i=0; i<featureVector.size(); i++){
      if(trainTextWordFrequency.get(featureVector.get(i)) == null){
        featureFrequencyInput[i+1] = 0;
      } else {
        featureFrequencyInput[i+1] = trainTextWordFrequency.get(featureVector.get(i)) / numberOfWordsInText * FREQUENCY_NORMALIZATION_DENOMINATOR;
      }
    }
    return featureFrequencyInput;
  }

  public static String getClass(double[] predictClass){
    int max_index = predictClass.length - 1;
    double max_value = -100;
    for(int i=predictClass.length - 1; i>=0; i--){
      if(predictClass[i]>max_value){
        max_index = i;
        max_value = predictClass[i];
      }
    }
    return classNames.get(max_index);
  }

  public static void classifyUsingNet(String testList, String outputFile){
    try{
      String line = null;
      BufferedReader br = new BufferedReader(new FileReader(testList));

      FileOutputStream fos = new FileOutputStream(outputFile);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      BufferedWriter bw = new BufferedWriter(osw);

      while((line=br.readLine()) != null){
        double[] fileInput = getInputVector(line.trim());
        double[] predictClass = textClassifier.justFeedForward(fileInput);
        // System.out.println(Arrays.toString(predictClass));
        String fileClass = getClass(predictClass);
        bw.write(line+" "+fileClass+"\n");
        // System.out.println(line +" "+ fileClass);
      }
      br.close();
      bw.close();
    }catch(Exception e3){
        System.err.println(e3 + ": no file to read in classifyUsingNet");
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
    classifyUsingNet(testList, testClassList);
  }
}
