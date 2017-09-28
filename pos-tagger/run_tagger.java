import java.util.*;
import java.io.*;

public class run_tagger{
  public static String[] tagsArr = new String[46];
  public static Map<String, Integer> tagID = new HashMap<String, Integer>();
  public static double[][] probabilityTagMatrix = new double[47][47];
  public static List<Map<String, Double>> duplicateVocab = new ArrayList<Map<String, Double>>();
  public static int vocabSize = 0;
  public static Stack<String> backtrace = new Stack<String>();

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

  public static void printVocabMatrix(List<Map<String,Double>> matrix){
    for(int i=1; i<matrix.size(); i++){
      System.out.println("tag" + i);
      for(Map.Entry vocab : matrix.get(i-1).entrySet()){
        System.out.println(vocab.getKey() + "->" + vocab.getValue());
      }
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
        tagsArr[i] = tags[i];
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
      for(int i=0; i<45; i++){
        String[] tagCount = br.readLine().split(" ");
        duplicateVocab.add(new HashMap<String, Double>());
        for(int j=0; j<Integer.parseInt(tagCount[1]); j++){
          String[] line = br.readLine().split(" ");
          duplicateVocab.get(i).put(line[0], Double.parseDouble(line[1]));
        }
      }
      // String line = null;
      // int size = 0;
      // for(int i=0; i<vocabSize; i++){
      //   String[] test = br.readLine().split("-->");
      //   test[1] = test[1].substring(1, test[1].length()-1);
      //   String[] probVals = test[1].split(", ");
      //   ArrayList<Double> arr = new ArrayList<Double>();
      //   for(int j=0; j<probVals.length; j++){
      //     arr.add(Double.valueOf(probVals[j]));
      //   }
      //   duplicateVocab.put(test[0], arr);
      // }
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
    // System.out.println("Sentence length is " + tokens.length);
    double[][] viterbiMat = new double[45][tokens.length];
    String[][] viterbiBack = new String[45][tokens.length];
    // printViterbi(viterbiMat, viterbiBack, tokens.length);
    for(int i=0; i<45; i++){
      if(duplicateVocab.get(i).containsKey(tokens[0])){
        viterbiMat[i][0] = probabilityTagMatrix[0][i+1]*duplicateVocab.get(i).get(tokens[0].toLowerCase()); //tlc
      }else{
        viterbiMat[i][0] = probabilityTagMatrix[0][i+1]*duplicateVocab.get(i).get("unknownUnseenWords");
      }
      viterbiBack[i][0] = "<s>";
    }
    // printViterbi(viterbiMat, viterbiBack, tokens.length);

    for(int i=1; i<tokens.length; i++){
      for(int j=0; j<45; j++){
        // double max = viterbiMat[0][i-1] * probabilityTagMatrix[1][j+1];
        // String argMax = tagsArr[0];
        // double viterbiMax = max * duplicateVocab.get(j).get(tokens[i]);

        // System.out.println(argMax + " " + max +" "+ tagsArr[0]+tagsArr[j]);
        if(duplicateVocab.get(j).containsKey(tokens[i].toLowerCase())){ //tlc
          double max = viterbiMat[0][i-1] * probabilityTagMatrix[1][j+1];
          String argMax = tagsArr[0];
          double viterbiMax = max * duplicateVocab.get(j).get(tokens[i].toLowerCase()); //tlc
          for(int k=1; k<45; k++){
            double maxE = viterbiMat[k][i-1] * probabilityTagMatrix[k+1][j+1];
            String argMaxE = tagsArr[k];
            double viterbiMaxE = maxE * duplicateVocab.get(j).get(tokens[i].toLowerCase()); //tlc
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
        }else{
          double max = viterbiMat[0][i-1] * probabilityTagMatrix[1][j+1];
          String argMax = tagsArr[0];
          double viterbiMax = max * duplicateVocab.get(j).get("unknownUnseenWords");
          for(int k=1; k<45; k++){
            double maxE = viterbiMat[k][i-1] * probabilityTagMatrix[k+1][j+1];
            String argMaxE = tagsArr[k];
            double viterbiMaxE = maxE * duplicateVocab.get(j).get("unknownUnseenWords");
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
    }

    double maxF = viterbiMat[0][tokens.length-1] * probabilityTagMatrix[1][46];
    String argMaxF = tagsArr[0];
    for (int i=1; i<45; i++){
      double maxFE = viterbiMat[i][tokens.length-1] * probabilityTagMatrix[i+1][46];
      String argMaxFE = tagsArr[i];
      if(maxFE > maxF){
        maxF = maxFE;
        argMaxF = argMaxFE;
      }
    }

    //
    // Stack<String> backtrace = new Stack<String>();
    // backtrace.push("</s>");
    backtrace.push(tokens[tokens.length-1] + "/" + argMaxF);
    String arg=argMaxF;
    for(int i=tokens.length-1; i>0; i--){
      backtrace.push(tokens[i-1] + "/" + viterbiBack[tagID.get(arg)][i]);
      arg = viterbiBack[tagID.get(arg)][i];
    }
    // backtrace.push("<s>");
    // while(!backtrace.isEmpty()){
    //   System.out.print(backtrace.pop() + " ");
    // }

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

  public static void writeResult(String outFileName){
    BufferedWriter bw = null;
    try {
      FileOutputStream fos = new FileOutputStream(outFileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      bw = new BufferedWriter(osw);
      ////////
      while(!backtrace.isEmpty()){
        bw.write(backtrace.pop() + " ");
      }
      bw.write("\n");

    } catch(Exception e){
      System.err.println(e + ": cannot write");
    } finally {
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
    // writeResult(outFileName);
    // System.out.println();
    // printProbabilityTagsMatrix();

    // printVocabMatrix(duplicateVocab);
    // System.out.print(vocabSize + "\n");
    //checksum to 1
    // for(int i=0; i<duplicateVocab.size(); i++){
    //   System.out.print("tag"+i);
    //   double sum = duplicateVocab.get(i).get("unknownUnseenWords") * (vocabSize - duplicateVocab.get(i).keySet().size() - 1);
    //   for(double val: duplicateVocab.get(i).values()){
    //     sum += val;
    //   }
    //   System.out.println(": "+sum);
    // }

    // System.out.println(testFileName + " " + modelFileName + " " + outFileName);
  }
}
