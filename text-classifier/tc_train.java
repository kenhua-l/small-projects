import java.util.*;
import java.io.*;
import java.math.*;

class NeuralNet {
  public final double INITIAL_WEIGHT = 0.5;
  public final double LEARNING_RATE = 0.1;

  public double[][] inputHiddenWeight; // 2d matrix of hidden_units x input_vector
  public double[][] hiddenOutputWeight; // 2d matrix of output x hidden_units
  public int inputCount;
  public int outputCount;
  public int hiddenUnitCount;

  public NeuralNet(int inputCount, int outputCount){
    this.inputCount = inputCount + 1;
    this.outputCount = outputCount;
    this.hiddenUnitCount = (inputCount / outputCount) + 1;
    this.inputHiddenWeight = new double[this.hiddenUnitCount][this.inputCount];
    this.hiddenOutputWeight = new double[this.outputCount][this.hiddenUnitCount];

    for(int h=0; h<this.hiddenUnitCount; h++){
      for(int i=0; i<this.inputCount; i++){
        this.inputHiddenWeight[h][i] = INITIAL_WEIGHT;
      }
    }

    for(int j=0; j<this.outputCount; j++){
      for(int h=0; h<this.hiddenUnitCount; h++){
        this.hiddenOutputWeight[j][h] = INITIAL_WEIGHT;
      }
    }

    // printNeuralNetFriendlyVersion();
  }

  public double perceptronOutput(double[] input, double[] weight){
    // inputVector has one less element than weightVector
    // inputVector[0] should be 1.0 for w0 to be a constant
    double netSum = 0;
    for(int i=0; i<input.length; i++){
      netSum += input[i] * weight[i];
    }
    double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
    return sigmoid;
  }

  public double[] feedForwardBackLearn(double[] inputVector, double[] targetVector){
    // feed forward
    double[] hiddenOutput = new double[this.hiddenUnitCount];
    hiddenOutput[0] = 1.0;
    for(int h=1; h<this.hiddenUnitCount; h++){
      hiddenOutput[h] = perceptronOutput(inputVector, this.inputHiddenWeight[h]);
    }
    double[] output = new double[this.outputCount];
    for(int j=0; j<this.outputCount; j++){
      output[j] = perceptronOutput(hiddenOutput, this.hiddenOutputWeight[j]);
    }

    // error propagate
    double[] outputError = new double[this.outputCount];
    for(int j=0; j<this.outputCount; j++){
      outputError[j] = output[j] * (1 - output[j]) * (targetVector[j] - output[j]);
    }
    // System.out.println("OutputError term: " + Arrays.toString(outputError));

    double[] hiddenError = new double[this.hiddenUnitCount];
    for(int h=0; h<this.hiddenUnitCount; h++){
      double downstreamError = 0;
      for(int j=0; j<this.outputCount; j++){
        downstreamError += hiddenOutputWeight[j][h] * outputError[j];
      }
      hiddenError[h] = hiddenOutput[h] * (1 - hiddenOutput[h]) * downstreamError;
    }
    // System.out.println("HiddenError term: " + Arrays.toString(hiddenError));

    //reweight
    this.inputHiddenWeight[0]
    for(int h=0; h<this.hiddenUnitCount; h++){
      for(int i=0; i<this.inputCount; i++){
        this.inputHiddenWeight[h][i] += LEARNING_RATE * hiddenError[h]
      }
    }

    for(int j=0; j<this.outputCount; j++){
      for(int h=0; h<this.hiddenUnitCount; h++){
        this.hiddenOutputWeight[j][h] = INITIAL_WEIGHT;
      }
    }


    printNeuralNetFriendlyVersion();

    return output;
  }

  public void printNeuralNetFriendlyVersion(){
    System.out.println("InputCount = " + this.inputCount + ", HiddenCount = " + this.hiddenUnitCount + ", OutputCount = " + this.outputCount);
    System.out.println("Input-hidden: ");
    for(int h=0; h<this.hiddenUnitCount; h++){
      for(int i=0; i<this.inputCount; i++){
        System.out.println(i + "-" + h +" : " + this.inputHiddenWeight[h][i]);
      }
    }
    System.out.println("hidden-output: ");
    for(int j=0; j<this.outputCount; j++){
      for(int h=0; h<this.hiddenUnitCount; h++){
        System.out.println(h + "-" + j +" : " + this.hiddenOutputWeight[j][h]);
      }
    }

  }
}

public class tc_train{
  public static final int WORD_FREQUENCY_THRESHOLD = 1;
  public static final double CHI2_THRESHOLD = 0.0;
  public static final int FREQUENCY_NORMALIZATION_DENOMINATOR = 500;

  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  public static Set<String> vocabulary = new HashSet<String>();   // Global vocab list
  public static Map<String, Integer> classTextFrequency = new HashMap<String, Integer>(); // Number of text for class
  public static Map<String, Map<String, Integer>> classWordTextNumber = new HashMap<String, Map<String, Integer>>();  // Number of text for word and class
  public static Vector<String> classNames;
  public static Vector<String> featureVector; // input
  public static NeuralNet textClassifier;

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

  public static double[] getTargetVector(String className){
    double[] targetVector = new double[classNames.size()];
    targetVector[classNames.indexOf(className)] = 1.0;
    return targetVector;
  }

  public static void neuralNetworkTraining(String fileName, String className){
    double[] inputVector = getInputVector(fileName);
    double[] targetVector = getTargetVector(className);
    double[] outputVector = textClassifier.feedForwardBackLearn(inputVector, targetVector);
    System.out.println(Arrays.toString(outputVector));

    // System.out.println(Arrays.toString(targetVector));
    //
    // System.out.println(inputVector.length);
    // System.out.println(1 +", "+inputVector[0]);
    // for(int i=0; i<featureVector.size(); i++){
    //   System.out.println(featureVector.get(i) +", "+inputVector[i+1]);
    // }
  }

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
    // System.out.println("stop words has " + stopWords.size()); // Check
  }

  public static void readTrainingFileToGetNValues(String fileName, String trainClass){
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
              // again check stop words
              if(!stopWords.contains(stemmedWord)){
                vocabulary.add(stemmedWord);  // add to global vocab list
                vocabInText.add(stemmedWord); // add to local file vocab list
              }
            }
          }
        }
      }
      tbr.close();
    }catch(Exception e2){
      System.err.println(e2 + ": no file to read in within readTrainClass");
    }

    // Setting up the potential stemmed word features we can use, also to find X2 value, N
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

  public static void readTrainClassList(String fileName, boolean trainNN){
    try{
      String line = null;
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      // Read training file name from list
      while((line=br.readLine()) != null){
        String[] lineSegment = line.split(" ");
        String trainingFile = lineSegment[0];
        String trainClass = lineSegment[1];

        if(!trainNN){
          if(classTextFrequency.get(trainClass) == null){
            classTextFrequency.put(trainClass, 1);
          }else{
            classTextFrequency.put(trainClass, classTextFrequency.get(trainClass) + 1);
          }
          readTrainingFileToGetNValues(trainingFile, trainClass);
        }else{
          neuralNetworkTraining(trainingFile, trainClass);
        }
      }
      br.close();
    }catch(Exception e1){
        System.err.println(e1 + ": no file to read in readTrainClassList");
    }
    if(!trainNN){
      classNames = new Vector<String>((Collection<String>) classTextFrequency.keySet());
    }
  }

  public static double getChiSquareValue(String w, String c){
    int N00, N01, N10, N11;
    N00 = N01 = N10 = N11 = 0;
    // N00 - no w and not in c
    // N01 - no w but in c
    // N10 - has w but not in c
    // N11 - has w and in c
    for(Map.Entry<String, Map<String, Integer>> vocab : classWordTextNumber.entrySet()){ // Go thru all classes
      if(!vocab.getKey().equals(c)){
        int hasWnotC = vocab.getValue().get(w) == null ? 0 : vocab.getValue().get(w);
        N00 += classTextFrequency.get(c) - hasWnotC;
        N10 += hasWnotC;
      }else{
        N11 = vocab.getValue().get(w) == null ? 0 : vocab.getValue().get(w);
        N01 += classTextFrequency.get(c) - N11;
      }
    }

    double chi2;
    // Don't add to feature if word does not occur frequent or occur in all train text (not unique)
    if((N11 + N10) <= WORD_FREQUENCY_THRESHOLD || (N01 + N00) == 0){
      chi2 = 0.0;
    } else {
      chi2 = ((N11 + N10 + N01 + N00) * Math.pow((N11 * N00) - (N10 * N01), 2))
              / ((N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00));
    }
    return chi2;
  }

  public static void selectFeature(){
    Set<String> classes = classTextFrequency.keySet();
    Set<String> feactureSelected = new HashSet<String>();
    for(String className : classes){
      for(String word : vocabulary){
        double weight = getChiSquareValue(word, className);
        // System.out.println(word + " " + weight);
        if(weight > CHI2_THRESHOLD){
          feactureSelected.add(word);
        }
      }
    }
    featureVector = new Vector<String>((Collection<String>) feactureSelected);
  }

  public static void neuralNetLearning(String fileName){
    readTrainClassList(fileName, true);
  }

  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWordsFile = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];
    // System.out.println(stopWordsFile + " " + trainClass + " " + modelFileName);

    // First read to set up parameters - chi2 value
    setStopWordList(stopWordsFile);
    readTrainClassList(trainClass, false);
    // remove words occurring less than k times and remove words occurring in all train file
    selectFeature();
    System.out.println(vocabulary.size() + " " + featureVector.size());
    // for(int i=0; i< featureVector.size(); i++){
    //   double occurr1 = classWordTextNumber.get("c1").get(featureVector.get(i)) != null ? classWordTextNumber.get("c1").get(featureVector.get(i)) : 0;
    //   double occurr2 = classWordTextNumber.get("c2").get(featureVector.get(i)) != null ?  classWordTextNumber.get("c2").get(featureVector.get(i)) : 0;
    //   double occurr3 = classWordTextNumber.get("c3").get(featureVector.get(i)) != null ?  classWordTextNumber.get("c3").get(featureVector.get(i)) : 0;
    //   double occurr4 = classWordTextNumber.get("c4").get(featureVector.get(i)) != null ?  classWordTextNumber.get("c4").get(featureVector.get(i)) : 0;
    //   double occurr5 = classWordTextNumber.get("c5").get(featureVector.get(i)) != null ?  classWordTextNumber.get("c5").get(featureVector.get(i)) : 0;
    //   double occurr = occurr1 + occurr2 + occurr3 + occurr4 + occurr5;
    //   if(occurr == 0){
    //     System.out.println(classWordTextNumber.get("c1").get(featureVector.get(i)));
    //     System.out.println(classWordTextNumber.get("c2").get(featureVector.get(i)));
    //     System.out.println(classWordTextNumber.get("c3").get(featureVector.get(i)));
    //     System.out.println(classWordTextNumber.get("c4").get(featureVector.get(i)));
    //     System.out.println(classWordTextNumber.get("c5").get(featureVector.get(i)));
    //   }
    //   System.out.println(featureVector.get(i) + " -> " + occurr);
    // }

    // Second read to set up backpropagation
    textClassifier = new NeuralNet(featureVector.size(), classNames.size());
    neuralNetLearning(trainClass);
    // writeModel(modelFileName);
  }
}
