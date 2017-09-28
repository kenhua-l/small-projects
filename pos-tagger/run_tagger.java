import java.util.*;
import java.io.*;

public class run_tagger{
  public static List<String> tags = new ArrayList<String>();
  public static double[][] tagMatrix = new double[47][47];
  public static List<Map<String, Double>> vocabularyMatrix = new ArrayList<Map<String, Double>>();
  public static int vocabSize = 0;
  public static Set<String> vocabulary = new HashSet<String>();
  public static Stack<String> backtrace = new Stack<String>();

  //For debugging - print the tag and emitter probability matrices.
  public static void printTagsMatrix(double[][] tagMatrix){
    System.out.print("-,\t");
    for(int i=0; i<tagMatrix.length; i++){
      System.out.print(tags.get(i) + ",\t");
    }
    System.out.println();
    for(int i=0; i<tagMatrix.length; i++){
      System.out.print(tags.get(i)+ ",\t");
      for(int j=0; j<tagMatrix[i].length; j++){
        System.out.print(tagMatrix[i][j] + ",\t");
      }
      System.out.println();
    }
  }

  public static void printVocabMatrix(List<Map<String,Double>> matrix){
    for(int i=1; i<matrix.size(); i++){
      System.out.println(tags.get(i));
      for(Map.Entry<String, Double> vocab : matrix.get(i-1).entrySet()){
        System.out.println(vocab.getKey() + "->" + vocab.getValue());
      }
    }
  }

  public static void printViterbi(double[][] mat, String[][] back, int obs){
    for(int i=0; i<45; i++){
      for(int j=0; j<obs; j++){
        System.out.print(mat[i][j] + ", " + back[i][j] + ", ");
      }
      System.out.print("\n");
    }
  }

  public static void readModel(String fileName){
    try{
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      //
      String[] tagsRead = br.readLine().split(", ");
      tags.add("<s>");
      for(int i=0; i<tagsRead.length; i++){
        tags.add(tagsRead[i]);
      }
      //
      for(int i=0; i<tags.size()-1; i++){
        String[] strProbs = br.readLine().split(", ");
        for(int j=0; j<strProbs.length; j++){
          tagMatrix[i][j+1] = Double.parseDouble(strProbs[j]);
        }
      }

      vocabSize = Integer.parseInt(br.readLine());

      for(int i=0; i<tags.size()-2; i++){
        String[] tagCount = br.readLine().split(" ");
        vocabularyMatrix.add(new HashMap<String, Double>());
        for(int j=0; j<Integer.parseInt(tagCount[1]); j++){
          String[] line = br.readLine().split(" ");
          vocabulary.add(line[0]);
          vocabularyMatrix.get(i).put(line[0], Double.parseDouble(line[1]));
        }
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read");
    }
  }

  public static double handleUnknownWord(String word, int tagIndex){
      System.out.println(word + " is unknown to " + tags.get(tagIndex));
      return vocabularyMatrix.get(tagIndex).get("unknownUnseenWords");
  }

  public static void viterbi(String line){
    String[] tokens = line.split(" ");
    double[][] viterbiMat = new double[45][tokens.length];
    String[][] viterbiBack = new String[45][tokens.length];
    String word = tokens[0]; //tlc
    // printViterbi(viterbiMat, viterbiBack, tokens.length);
    for(int i=0; i<45; i++){
      if(vocabularyMatrix.get(i).containsKey(tokens[0])){
        viterbiMat[i][0] = tagMatrix[0][i+1]*vocabularyMatrix.get(i).get(word); //tlc
      }else{
        viterbiMat[i][0] = tagMatrix[0][i+1]*vocabularyMatrix.get(i).get("unknownUnseenWords");
      }
      viterbiBack[i][0] = "<s>";
    }
    // printViterbi(viterbiMat, viterbiBack, tokens.length);

    for(int i=1; i<tokens.length; i++){
      for(int j=0; j<tags.size()-2; j++){
        word = tokens[i]; //tlc
        double max = viterbiMat[0][i-1] * tagMatrix[1][j+1];
        String argMax = tags.get(1);
        //unknown word handling
        double probOfWordGivenTag = 1.0;
        if(!vocabularyMatrix.get(j).containsKey(word)){ //tlc
          if(!vocabulary.contains(word)){
            probOfWordGivenTag *= handleUnknownWord(word, j);
          }
          word = "unknownUnseenWords";
        }
        probOfWordGivenTag *= vocabularyMatrix.get(j).get(word);
        double viterbiMax = max * probOfWordGivenTag; //tlc
        for(int k=1; k<tags.size()-2; k++){
          double maxE = viterbiMat[k][i-1] * tagMatrix[k+1][j+1];
          String argMaxE = tags.get(k+1);
          double viterbiMaxE = maxE * probOfWordGivenTag; //tlc
          if(maxE > max){
            max = maxE;
            argMax = argMaxE;
          }
          if(viterbiMaxE > viterbiMax){
            viterbiMax = viterbiMaxE;
          }
        }
        viterbiMat[j][i] = viterbiMax;
        viterbiBack[j][i] = argMax;
      }
    }

    double maxF = viterbiMat[0][tokens.length-1] * tagMatrix[1][46];
    String argMaxF = tags.get(1);
    for (int i=1; i<45; i++){
      double maxFE = viterbiMat[i][tokens.length-1] * tagMatrix[i+1][46];
      String argMaxFE = tags.get(i+1);
      if(maxFE > maxF){
        maxF = maxFE;
        argMaxF = argMaxFE;
      }
    }

    //
    backtrace.push(tokens[tokens.length-1] + "/" + argMaxF);
    String arg=argMaxF;
    for(int i=tokens.length-1; i>0; i--){
      backtrace.push(tokens[i-1] + "/" + viterbiBack[tags.indexOf(arg)][i]);
      arg = viterbiBack[tags.indexOf(arg)][i];
    }
    // printViterbi(viterbiMat, viterbiBack, tokens.length);
  }

  public static void evaluateTestFile(String fileName, String outFileName){
    String line = null;
    BufferedWriter bw = null;
    try{
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      FileOutputStream fos = new FileOutputStream(outFileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      bw = new BufferedWriter(osw);
      while((line=br.readLine())!=null){
        viterbi(line);
        // System.out.println();
        while(!backtrace.isEmpty()){
          bw.write(backtrace.pop() + " ");
        }
        bw.write("\n");
      }
    }catch(Exception e){
        System.err.println(e + ": no file to read or write");
    }finally {
      try{
        bw.close();
      }catch(Exception e){
        System.err.println(e + ": cannot write");
      }
    }
  }

  public static void main(String[] args) {
    //java run_tagger sents.test model_file sents.out
    String testFileName = args[0];
    String modelFileName = args[1];
    String outFileName = args[2];
    readModel(modelFileName);
    evaluateTestFile(testFileName, outFileName);
    // printProbabilityTagsMatrix();
    // printVocabMatrix(vocabularyMatrix);
    // System.out.println(testFileName + " " + modelFileName + " " + outFileName);
  }
}
