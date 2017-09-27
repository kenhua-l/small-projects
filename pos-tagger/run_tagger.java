import java.util.*;
import java.io.*;

public class run_tagger{
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static double[][] probabilityTagMatrix = new double[47][47];
  public static Map<String, ArrayList<Double>> duplicateVocab = new HashMap<String, ArrayList<Double>>();
  public static int vocabSize = 0;

  public static void printProbabilityTagsMatrix(){
    System.out.print("-, ");
    for(int i=0; i<=46; i++){
      System.out.print("tag" + i + ", ");
    }
    System.out.println();
    for(int i=0; i<=46; i++){
      System.out.print("tag" + i + ", ");
      for(int j=0; j<=46; j++){
        System.out.print(probabilityTagMatrix[i][j] + ", ");
      }
      System.out.println();
    }
  }

  public static void printVocabMatrix(Map<String, ArrayList<Double>> matrix){
    System.out.print("- ");
    for(int i=1; i<=45; i++){
      System.out.print("tag" + i + ", ");
    }
    System.out.println();
    for (Map.Entry entry : matrix.entrySet()) {
        System.out.println(entry.getKey() + " " + entry.getValue());
    }
  }

  public static void readModel(String fileName){
    try{
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      //
      String[] tags = br.readLine().split(", ");
      tagID.put("<s>", 0);
      for(int i=0; i<tags.length; i++){
        tagID.put(tags[i], i+1);
      }
      //
      for(int i=0; i<46; i++){
        String[] strProbs = br.readLine().split(", ");
        for(int j=0; j<strProbs.length; j++){
          probabilityTagMatrix[i][j+1] = Double.parseDouble(strProbs[j]);
        }
      }
      //
      vocabSize = Integer.parseInt(br.readLine());
      //
      String line = null;
      int size = 0;
      for(int i=0; i<vocabSize; i++){
        String[] test = br.readLine().split("-->");
        test[1] = test[1].substring(1, test[1].length()-1);
        String[] probVals = test[1].split(", ");
        ArrayList<Double> arr = new ArrayList<Double>();
        for(int j=0; j<probVals.length; j++){
          arr.add(Double.valueOf(probVals[j]));
        }
        duplicateVocab.put(test[0], arr);
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
  }

  public static void printViterbi(double[][] mat, String[][] back, int obs){
    for(int i=0; i<45; i++){
      for(int j=0; j<obs; j++){
        System.out.print(mat[i][j] + "->" + back[i][j] + " ");
      }
      System.out.print("\n");
    }
  }

  public static void viterbi(String line){
    String[] tokens = line.split(" ");
    double[][] viterbiMat = new double[45][tokens.length];
    String[][] viterbiBack = new String[45][tokens.length];
    // printViterbi(viterbiMat, viterbiBack, tokens.length);
    // for(int i=0; i<45; i++){
    //   viterbiMat[i][0] = probabilityTagMatrix[0][i+1];
    // }
    // printViterbi(viterbiMat, viterbiBack, tokens.length);

    for(int i=0; i<tokens.length; i++){
      if(duplicateVocab.get(tokens[i]) == null){
        System.out.print(tokens[i] + "\n");

      }
    }
  }

  public static void evaluateTestFile(String fileName){
    String line = null;
    try{
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      while((line=br.readLine())!=null){
        viterbi(line);
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
  }

  public static void main(String[] args) {
    //java run_tagger sents.test model_file sents.out
    String testFileName = args[0];
    String modelFileName = args[1];
    String outFileName = args[2];
    readModel(modelFileName);
    evaluateTestFile(testFileName);
    // printProbabilityTagsMatrix();

    // printVocabMatrix(duplicateVocab);
    // System.out.print(duplicateVocab.size() + "\n");
    // System.out.print(vocabSize + "\n");

    // System.out.println(testFileName + " " + modelFileName + " " + outFileName);
  }
}
