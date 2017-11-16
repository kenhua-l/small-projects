import java.util.*;
import java.io.*;
import java.math.*;

// Simplified NeuralNet that only has input and output - no hidden layer
class NeuralNet {
  public final double INITIAL_WEIGHT = -0.3;
  public final double LEARNING_RATE = 0.1;
  public final int HIDDEN_UNIT = 10;

  public double[][] inputWeight; // 2d matrix of output x hidden_units x input_vector length
  public double[][] hiddenWeight; // 2d matrix of output x hidden_units x input_vector length
  public int outputCount;

  public NeuralNet(int inputCount, int outputCount){
    double[] inputVector = new double[inputCount];
    this.inputWeight = new double[HIDDEN_UNIT][inputCount + 1];
    this.hiddenWeight = new double[outputCount][HIDDEN_UNIT];
    this.outputCount = outputCount;
    // this.outputVector = new double[outputCount];
    // System.out.println("inputVector : " + Arrays.toString(inputVector));
    for(int i=0; i<outputCount; i++){
      for(int j=0; j<HIDDEN_UNIT; j++){
        this.hiddenWeight[i][j] = INITIAL_WEIGHT;
      }
    }
    for(int i=0; i<HIDDEN_UNIT; i++){
      for(int j=0; j<inputCount + 1; j++){
        this.inputWeight[i][j] = INITIAL_WEIGHT;
      }
    }
  }

  public double perceptronOutput(double[] inputVector, double[] weightVector){
    // inputVector has one less element than weightVector
    // inputVector[0] should be 1.0 for w0 to be a constant
    double netSum = weightVector[0];
    for(int i=0; i<inputVector.length; i++){
      netSum += inputVector[i] * weightVector[i+1];
    }
    double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
    return sigmoid;
  }

  public double[] feedForward(double[] inputVector){
    double[] hiddenOutputVector = new double[HIDDEN_UNIT];
    double[] outputVector = new double[this.outputCount];
    // System.out.println("inputVectorsize : " + inputVector.length);
    // System.out.println("inputVectorsize : " + inputWeight[0].length);
    System.out.println("Size of inputVector " + inputVector.length + " and size of hiddenweight is " + this.inputWeight[0].length);

    for(int i=0; i<HIDDEN_UNIT; i++){
      // System.out.println("Size of inputVector " + inputVector.length + " and size of hiddenweight " + i + " is " + this.inputWeight[i].length);
      System.out.println("Input is: " + Arrays.toString(inputVector));
      System.out.println("Size of inputVector " + inputVector.length + " and size of hiddenweight is " + this.inputWeight[0].length);
      System.out.println("Weight is: " + Arrays.toString(this.inputWeight[i]));
      double unitOutput = perceptronOutput(inputVector, this.inputWeight[i]);

      System.out.println("hidden unit " + i + " is : " + unitOutput);
    }

    for(int i=0; i<HIDDEN_UNIT; i++){
      double netSum = 1 * this.inputWeight[i][0];
      // Sum net
      for(int j=1; j < this.inputWeight[i].length; j++){
        netSum += inputVector[j-1] * inputWeight[i][j];
      }
      // output - Sigmoid function
      double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
      // System.out.println(sigmoid);
      hiddenOutputVector[i] = sigmoid;
    }
    System.out.println("Hidden unit: " + Arrays.toString(hiddenOutputVector));
    for(int i=0; i<this.outputCount; i++){
      double netSum = 1 * this.hiddenWeight[i][0];
      // Sum net
      for(int j=1; j < this.hiddenWeight[i].length; j++){
        netSum += hiddenOutputVector[j-1] * hiddenWeight[i][j];
      }
      // output - Sigmoid function
      double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
      // System.out.println(sigmoid);
      outputVector[i] = sigmoid;
    }
    return outputVector;
  }

  public void printWeightMatrix(){
    for(int i=0; i<outputCount; i++){
      for(int j=0; j<HIDDEN_UNIT; j++){
        System.out.print(this.hiddenWeight[i][j] + " ");
      }
      System.out.println();
    }
    for(int i=0; i<HIDDEN_UNIT; i++){
      for(int j=0; j<this.inputWeight[i].length; j++){
        System.out.print(this.inputWeight[i][j] + " ");
      }
      System.out.println();
    }
  }

  public void backPropagate(double[] inputVector, double[] outputVector, double[] targetVector, boolean printdeets){
    if(printdeets){
      System.out.println("OutputVector: " + Arrays.toString(outputVector));
      System.out.println("TargetVector: " + Arrays.toString(targetVector));
    }

    // calculate error term
    double[] outputErrorTerm = new double[targetVector.length];
    for(int i=0; i<targetVector.length; i++){
      outputErrorTerm[i] = outputVector[i] * (1 - outputVector[i]) * (targetVector[i] - outputVector[i]);
      // targetVector[i] - outputVector[i];
    }

    double[] hiddenErrorTerm = new double[HIDDEN_UNIT];
    double[] hiddenOutputVector = new double[HIDDEN_UNIT];
    for(int i=0; i<HIDDEN_UNIT; i++){
      double netSum = 1 * this.inputWeight[i][0];
      // Sum net
      for(int j=1; j < this.inputWeight[i].length; j++){
        netSum += inputVector[j-1] * inputWeight[i][j];
      }
      // output - Sigmoid function
      double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
      // System.out.println(sigmoid);
      double downstream = 0;
      for(int j=0; j < targetVector.length; j++){
        downstream += this.hiddenWeight[j][i] * outputErrorTerm[j];
      }
      hiddenErrorTerm[i] = sigmoid * (1-sigmoid) * downstream;
    }

    if(printdeets)
      System.out.println("ErrorTermVec: " + Arrays.toString(outputErrorTerm));
    // System.out.println(inputWeight.length);
    // System.out.println(targetVector.length);
    // System.out.println(inputWeight[0].length);
    // System.out.println(inputVector.length + 1);

    // update weight
    // for each output unit
    for(int j=0; j<targetVector.length; j++){
      // for each weight associated with that output unit
      this.hiddenWeight[j][0] += LEARNING_RATE * outputErrorTerm[j] * 1;
      for(int i=1; i<HIDDEN_UNIT; i++){
        double weightChange = LEARNING_RATE * outputErrorTerm[j] * hiddenErrorTerm[i-1];
        this.hiddenWeight[j][i] += weightChange;
      }
    }
    for(int j=0; j<HIDDEN_UNIT; j++){
      // for each weight associated with that output unit
      this.inputWeight[j][0] += LEARNING_RATE * hiddenErrorTerm[j] * 1;
      for(int i=1; i<inputVector.length + 1; i++){
        double weightChange = LEARNING_RATE * hiddenErrorTerm[j] * inputVector[i-1];
        this.inputWeight[j][i] += weightChange;
      }
    }
  }

}

public class tc_train {
  public static final int WORD_FREQUENCY_THRESHOLD = 1;
  public static final double CHI2_THRESHOLD = 0.0;
  public static final int FREQUENCY_NORMALIZATION_DENOMINATOR = 500;

  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  public static Set<String> vocabulary = new HashSet<String>();   // Global vocab list
  public static Map<String, Integer> classTextFrequency = new HashMap<String, Integer>(); // Number of text for class
  public static Map<String, Map<String, Integer>> classWordTextNumber = new HashMap<String, Map<String, Integer>>();  // Number of text for word and class
  public static Vector<String> classNames;
  public static Vector<String> featureVector; // input

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
      br.close();
    }catch(Exception e1){
        System.err.println(e1 + ": no file to read in readTrainClassList");
    }

    classNames = new Vector<String>((Collection<String>) classTextFrequency.keySet());
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
    // System.out.println("N00 = " + N00);
    // System.out.println("N01 = " + N01);
    // System.out.println("N10 = " + N10);
    // System.out.println("N11 = " + N11);

    double chi2;
    // Don't add to feature if word does not occur frequent or occur in all train text
    if((N11 + N10) <= WORD_FREQUENCY_THRESHOLD || (N01 + N00) == 0){
      chi2 = 0.0;
    } else {
      chi2 = ((N11 + N10 + N01 + N00) * Math.pow((N11 * N00) - (N10 * N01), 2))
              / ((N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00));
    }
    return chi2;
  }

  public static void neuralNetLearning(String fileName){
    NeuralNet nn = new NeuralNet(featureVector.size(), classTextFrequency.keySet().size());
    // System.out.println("I am here 1");

    for(int num=0; num<10; num++){
      System.out.println("Training iteration: " + num);
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line = null;
      while((line=br.readLine()) != null){
        String[] lineSegment = line.split(" ");
        String trainingFile = lineSegment[0];
        String trainClass = lineSegment[1];

        Map<String, Double> trainTextWordFrequency = new HashMap<String, Double>();
        long numberOfWordsInText = 0;
        double[] targetVector = new double[classNames.size()];
        // for(int i=0; i<classNames.size(); i++){
        targetVector[classNames.indexOf(trainClass)] = 1.0;
        // }
        // System.out.println(Arrays.toString(tartgetVector));

        try{
          BufferedReader tbr = new BufferedReader(new FileReader(trainingFile));
          String trainLine = null;
          while((trainLine=tbr.readLine()) != null){
            trainLine = trainLine.replaceAll("[^a-zA-Z ]", " ").trim().toLowerCase();
            if(!trainLine.equals("")){
              String[] rawWordList = trainLine.split("\\s+");
              for (String word: rawWordList){
                numberOfWordsInText++;
                // remove all stop words
                if(!stopWords.contains(word)){
                  Stemmer stem = new Stemmer();
                  stem.add(word.toCharArray(), word.length());
                  // Stem the word
                  stem.stem();
                  String stemmedWord = stem.toString();

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
        // System.out.println(trainTextWordFrequency.isEmpty());
        // for(String key:trainTextWordFrequency.keySet()){
          // System.out.println(key + " -> " + trainTextWordFrequency.get(key));
        // }

        // if(numberOfWordsInText <= 100)
          // System.out.println("numberOfWordsInText : " + numberOfWordsInText);

        // Normalize word count as input
        double[] featureFrequencyInput = new double[featureVector.size()];
        // featureFrequencyInput[0] = 1.0;
        for(int i=0; i<featureVector.size(); i++){
          if(trainTextWordFrequency.get(featureVector.get(i)) == null){
            featureFrequencyInput[i] = 0;
          } else {
            featureFrequencyInput[i] = trainTextWordFrequency.get(featureVector.get(i)) / numberOfWordsInText * FREQUENCY_NORMALIZATION_DENOMINATOR;
          }
        }

        // System.out.println("THE INPUT: "+ Arrays.toString(featureFrequencyInput));
        // System.out.println("Input size: " + featureFrequencyInput.length + " compared to " + featureVector.size());
        // nn.printWeightMatrix();
        double[] outputVector = nn.feedForward(featureFrequencyInput);

        // System.out.println(Arrays.toString(outputVector));
        nn.backPropagate(featureFrequencyInput, outputVector, targetVector, num==9);

      }
      br.close();
    }catch(Exception e3){
      System.err.println(e3 + ": no file to read in neuralNetLearning outer loop");
    }

    }

    try{
      FileOutputStream fos = new FileOutputStream("testWeight.out");
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      BufferedWriter bw = new BufferedWriter(osw);
      ////////
      for(int i=0; i<nn.inputWeight.length; i++){
        for(int j=0; j<nn.inputWeight[i].length; j++){
          bw.write(nn.inputWeight[i][j] + " ");
        }
        bw.write("\n");
      }

      for(int i=0; i<nn.hiddenWeight.length; i++){
        for(int j=0; j<nn.hiddenWeight[i].length; j++){
          bw.write(nn.hiddenWeight[i][j] + " ");
        }
        bw.write("\n");
      }
      //////
      bw.close();
    } catch(Exception e){
      System.err.println(e + ": cannot write in writeModel");
    }
    // nn.printWeightMatrix();
    System.out.println(classNames.toString());
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

  public static void writeModel(String fileName){
    Set<String> classes = classTextFrequency.keySet();
    BufferedWriter bw = null;
    try {
      FileOutputStream fos = new FileOutputStream(fileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      bw = new BufferedWriter(osw);
      ////////
      for(String className : classes){
        for(String word : featureVector){
          double weight = getChiSquareValue(word, className);
          bw.write(className + " : " + word + " -> " + weight + "\n");
        }
      }
      //////
      bw.close();
    } catch(Exception e){
      System.err.println(e + ": cannot write in writeModel");
    }
  }

  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWordsFile = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];
    // System.out.println(stopWordsFile + " " + trainClass + " " + modelFileName);
    // First read to set up parameters - chi2 value
    setStopWordList(stopWordsFile);
    readTrainClassList(trainClass);
    // remove words occurring less than k times and remove words occurring in all train file
    selectFeature();
    System.out.println(vocabulary.size() + " " + featureVector.size());

    // Second read to set up backpropagation
    neuralNetLearning(trainClass);
    writeModel(modelFileName);
  }
}
