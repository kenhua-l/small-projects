import java.util.*;
import java.io.*;

// Simplified NeuralNet that only has input and output - no hidden layer
class NeuralNet {
  public double[] inputWeight;
  public double[] outputVector;
  public NeuralNet(int inputCount, int outputCount){
    double[] inputVector = new double[inputCount];
    this.inputWeight = new double[inputCount + 1];
    this.outputVector = new double[outputCount];
    System.out.println(Arrays.toString(inputVector));
    for(int i=0; i<inputCount + 1; i++){
      this.inputWeight[i] = 0.1;
    }
    System.out.println(Arrays.toString(this.inputWeight));
    System.out.println(Arrays.toString(this.outputVector));
  }

  public void feedForward(double[] inputVector){
    double netSum = this.inputWeight[0];
    // Sum net
    for(int i=1; i < this.inputWeight.length; i++){
      netSum += inputVector[i-1] * inputWeight[i];
    }
    System.out.println(netSum);

    // Sigmoid
    double sigmoid = 1 / (1 + Math.pow(Math.E, -netSum));
    System.out.println(sigmoid);

  }

}

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
    double chi2 = ((N11 + N10 + N01 + N00) * Math.pow((N11 * N00) - (N10 * N01), 2))
                    / ((N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00));
    // System.out.println("chi2 = " + chi2);
    return chi2;
  }

  public static void neuralNetLearning(){
    NeuralNet nn = new NeuralNet(vocabulary.size(), classTextFrequency.keySet().size());
    double[] input = new double[vocabulary.size()];
    for(int i=0; i < vocabulary.size(); i++){
      input[i] = 1.0;
    }
    nn.feedForward(input);
    // NeuralNet nn = new NeuralNet(10, 5);

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
        for(String word : vocabulary){
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
    // First read to set up parameters
    setStopWordList(stopWordsFile);
    readTrainClassList(trainClass);

    // Second read to set up backpropagation
    neuralNetLearning();
    writeModel(modelFileName);
  }
}
