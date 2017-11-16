import java.util.*;
import java.io.*;
import java.math.*;

class NeuralNet {
  private final double INITIAL_WEIGHT = -0.5;
  private final double LEARNING_RATE = 0.1;

  private double[][] inputHiddenWeight; // 2d matrix of hidden_units x input_vector
  private double[][] hiddenOutputWeight; // 2d matrix of output x hidden_units
  private int inputCount;
  private int outputCount;
  private int hiddenUnitCount;

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

  public void setInputHiddenWeight(double[][] weight){
    if(weight.length == this.hiddenUnitCount && weight[0].length == this.inputCount){
      this.inputHiddenWeight = weight;
    }
  }

  public void setHiddenOutputWeight(double[][] weight){
    if(weight.length == this.outputCount && weight[0].length == this.hiddenUnitCount){
      this.hiddenOutputWeight = weight;
    }
  }

  public int getHiddenUnitCount(){
    return this.hiddenUnitCount;
  }

  public double[][] getInputHiddenWeight(){
    return this.inputHiddenWeight;
  }

  public double[][] getHiddenOutputWeight(){
    return this.hiddenOutputWeight;
  }

  private double perceptronOutput(double[] input, double[] weight){
    // inputVector has one less element than weightVector
    // inputVector[0] should be 1.0 for w0 to be a constant
    double netSum = 0;
    for(int i=0; i<input.length; i++){
      netSum += input[i] * weight[i];
    }
    double binary = netSum > 0 ? 1 : -1;
    double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
    return binary;
  }

  public double[] justFeedForward(double[] inputVector){
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
    return output;
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
      // outputError[j] = output[j] * (1 - output[j]) * (targetVector[j] - output[j]);
      outputError[j] = (targetVector[j] - output[j]);
    }
    // System.out.println("OutputError term: " + Arrays.toString(outputError));

    double[] hiddenError = new double[this.hiddenUnitCount];
    for(int h=0; h<this.hiddenUnitCount; h++){
      double downstreamError = 0;
      for(int j=0; j<this.outputCount; j++){
        downstreamError += hiddenOutputWeight[j][h] * outputError[j];
      }
      // hiddenError[h] = hiddenOutput[h] * (1 - hiddenOutput[h]) * downstreamError;
      hiddenError[h] = downstreamError;
    }
    // System.out.println("HiddenError term: " + Arrays.toString(hiddenError));

    //reweight
    for(int h=1; h<this.hiddenUnitCount; h++){
      for(int i=0; i<this.inputCount; i++){
        double change = LEARNING_RATE * hiddenError[h] * inputVector[i];
        this.inputHiddenWeight[h][i] += change;
        // System.out.println(change);
      }
    }

    for(int j=0; j<this.outputCount; j++){
      for(int h=0; h<this.hiddenUnitCount; h++){
        double change = LEARNING_RATE * outputError[j] * hiddenOutput[h];
        this.hiddenOutputWeight[j][h] += change;
        // System.out.println(change);
      }
    }
    // printNeuralNetFriendlyVersion();
    return justFeedForward(inputVector);
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
    for(int i=0; i<classNames.size(); i++){
      targetVector[i] = -1.0;
    }
    targetVector[classNames.indexOf(className)] = 1.0;
    return targetVector;
  }

  public static void neuralNetworkTraining(String fileName, String className){
    double[] inputVector = getInputVector(fileName);
    double[] targetVector = getTargetVector(className);
    double[] outputVector = textClassifier.feedForwardBackLearn(inputVector, targetVector);

    System.out.println("Output: "+ Arrays.toString(outputVector));
    System.out.println("Target: "+ Arrays.toString(targetVector));
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
    // on basis of iteration
    for(int i=0; i<5; i++){
      System.out.println("Training iteration "+i);
      readTrainClassList(fileName, true);
    }
    // readTrainClassList(fileName, true);
  }

  public static void writeModel(String fileName){
    int inputCount = featureVector.size();
    int outputCount = classNames.size();
    int hiddenCount = textClassifier.getHiddenUnitCount();
    try{
      FileOutputStream fos = new FileOutputStream(fileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      BufferedWriter bw = new BufferedWriter(osw);
      ////////
      // write input feature
      bw.write(inputCount + "\n");
      for(int i=0; i<inputCount; i++){
        bw.write(featureVector.get(i) + "\n");
      }

      // write output class
      bw.write(outputCount + "\n");
      for(int i=0; i<outputCount; i++){
        bw.write(classNames.get(i) + "\n");
      }

      // write NeuralNet details (hidden layer)
      bw.write("num-perceptron-hidden-unit " + hiddenCount + "\n");

      // write input-hidden weight
      bw.write(hiddenCount + " " + (inputCount+1) + "\n");
      double[][] inWeight = textClassifier.getInputHiddenWeight();
      for(int h=0; h<hiddenCount; h++){
        for(int i=0; i<inputCount+1; i++){
          bw.write(inWeight[h][i] + " ");
        }
        bw.write("\n");
      }

      // write hidden-output weight
      bw.write(outputCount + " " + hiddenCount + "\n");
      double[][] hiddenWeight = textClassifier.getHiddenOutputWeight();
      for(int j=0; j<outputCount; j++){
        for(int h=0; h<hiddenCount; h++){
          bw.write(hiddenWeight[j][h] + " ");
        }
        bw.write("\n");
      }

      //////
      bw.close();
    }catch(Exception e5){
      System.err.println(e5 + ": cannot write in writeModel");
    }

  }

  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWordsFile = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];

    // First read to set up parameters - chi2 value
    setStopWordList(stopWordsFile);
    readTrainClassList(trainClass, false);
    // remove words occurring less than k times and remove words occurring in all train file
    selectFeature();

    // Second read to set up backpropagation
    textClassifier = new NeuralNet(featureVector.size(), classNames.size());
    neuralNetLearning(trainClass);

    // Write model file
    writeModel(modelFileName);
  }
}
