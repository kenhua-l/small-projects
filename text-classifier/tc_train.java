import java.util.*;
import java.io.*;
import java.math.*;

// Simplified NeuralNet that only has input and output - no hidden layer
class NeuralNet {
  public double[][] inputWeight; // 2d matrix of output x vector length
  public int outputCount;
  // public double[] outputVector;

  public NeuralNet(int inputCount, int outputCount){
    double[] inputVector = new double[inputCount];
    this.inputWeight = new double[outputCount][inputCount + 1];
    this.outputCount = outputCount;
    // this.outputVector = new double[outputCount];
    // System.out.println("inputVector : " + Arrays.toString(inputVector));
    for(int i=0; i<outputCount; i++){
      for(int j=0; j<inputCount + 1; j++){
        this.inputWeight[i][j] = 0.1;
      }
      // System.out.println("Vector " + i + " : " +Arrays.toString(this.inputWeight[i]));
    }

    // System.out.println(Arrays.toString(this.inputWeight));
    // System.out.println(Arrays.toString(this.outputVector));
  }

  public double[] feedForward(double[] inputVector){
    double[] outputVector = new double[this.outputCount];
    System.out.println("inputVectorsize : " + inputVector.length);
    System.out.println("inputVectorsize : " + inputWeight[0].length);
    for(int i=0; i<this.outputCount; i++){
      double netSum = this.inputWeight[i][0];
      // Sum net
      for(int j=1; j < this.inputWeight[i].length; j++){
        netSum += inputVector[j-1] * inputWeight[i][j];
      }
      // output - Sigmoid function
      double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
      // System.out.println(sigmoid);
      outputVector[i] = sigmoid;
    }
    System.out.println(Arrays.toString(outputVector));
    return outputVector;
  }

  // public backPropagate

}

public class tc_train {
  public static final int WORD_FREQUENCY_THRESHOLD = 2;
  public static final double CHI2_THRESHOLD = 10.0;

  public static Set<String> stopWords = new HashSet<String>();    // Stop words given
  public static Set<String> vocabulary = new HashSet<String>();   // Global vocab list
  public static Map<String, Integer> classTextFrequency = new HashMap<String, Integer>(); // Number of text for class
  public static Map<String, Map<String, Integer>> classWordTextNumber = new HashMap<String, Map<String, Integer>>();  // Number of text for word and class
  public static Set<String> featureVector = new HashSet<String>(); // input

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
    double[] input = new double[featureVector.size()];
    for(int i=0; i < featureVector.size(); i++){
      input[i] = 1.0;
    }
    nn.feedForward(input);

    // for()

  }

  public static void selectFeature(){
    Set<String> classes = classTextFrequency.keySet();
    for(String className : classes){
      for(String word : vocabulary){
        double weight = getChiSquareValue(word, className);
        if(weight > CHI2_THRESHOLD){
          featureVector.add(word);
        }
      }
    }
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
