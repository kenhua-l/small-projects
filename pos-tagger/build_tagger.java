import java.util.*;
import java.io.*;

public class build_tagger {
  //List of all 45 Penn tags, <s>, and </s>
  public static final List<String> penn_Tags = Arrays.asList("<s>", "CC", "CD", "DT", "EX",
  "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP", "NNPS",
  "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH",
  "VB", "VBD", "VBG", "VBN", "VBP", "VBZ", "WDT",  "WP", "WP$", "WRB", "$",
  "#", "``", "''", "-LRB-", "-RRB-", ",", ".", ":", "</s>");

  public static double[][] tagMatrix = new double[47][47];
  public static Set<String> vocabulary = new HashSet<String>();
  public static List<Map<String, Double>> vocabularyMatrix = new ArrayList<Map<String, Double>>();

  public static void initializeMatrix(){
    for(int i=0; i<45; i++){
      // Decided to just initialize 45 because <s> and </s> does not need that much space
      vocabularyMatrix.add(new HashMap<String, Double>());
    }
  }

  //For debugging - print the tag and emitter probability matrices.
  public static void printTagsMatrix(double[][] tagMatrix){
    System.out.print("-,\t");
    for(int i=0; i<tagMatrix.length; i++){
      System.out.print(penn_Tags.get(i) + ",\t");
    }
    System.out.println();
    for(int i=0; i<tagMatrix.length; i++){
      System.out.print(penn_Tags.get(i)+ ",\t");
      for(int j=0; j<tagMatrix[i].length; j++){
        System.out.print(tagMatrix[i][j] + ",\t");
      }
      System.out.println();
    }
  }

  public static void printVocabMatrix(List<Map<String,Double>> matrix){
    for(int i=1; i<matrix.size(); i++){
      System.out.println(penn_Tags.get(i));
      for(Map.Entry<String, Double> vocab : matrix.get(i-1).entrySet()){
        System.out.println(vocab.getKey() + "->" + vocab.getValue());
      }
    }
  }

  //add raw counts to the tagmatrix and the vocabularyMatrix
  public static void processLine(String line){
    String[] segmented = line.split(" ");
    String tag = null;
    String prevTag = "<s>";
    for(int i=0; i<segmented.length; i++){
      String[] wordTag = segmented[i].split("/");
      String word = wordTag[0]; //tlc
      if(wordTag.length > 2){
        for(int j=1; j<wordTag.length-1; j++){
          word += "/" + wordTag[j]; //tlc
        }
        tag = wordTag[wordTag.length-1];
      }else{
        tag = wordTag[1];
      }

      //PennTags and tagMatrix has 47 rows, vocabularyMatrixonly has 45
      int tagIndex = penn_Tags.indexOf(tag);
      double wordCount = 0;
      if(vocabularyMatrix.get(tagIndex-1).keySet().contains(word)){
        wordCount = vocabularyMatrix.get(tagIndex-1).get(word);
      } else {
        vocabulary.add(word);
      }
      vocabularyMatrix.get(tagIndex-1).put(word, wordCount+1.0);

      tagMatrix[penn_Tags.indexOf(prevTag)][tagIndex]++;
      prevTag = tag;
    }
    tagMatrix[penn_Tags.indexOf(tag)][penn_Tags.indexOf("</s>")]++;
  }

  //For runTagger
  //Smoothing
  public static void smoothProbabilityTagMatrix(){
    // Tag1Tag2
    // p(t2|t1) probability of Tag2 given Tag1 (what we are computing)
    // cTt is the tag bigram count for Tag1Tag2 in that order
    // cT is the frequency Tag1 appears (sum up column or row is ok)
    // tT is the number of types of tags following T
    // V is 47 tags so Z is V-tT
    // WittenBell smoothing -
    // p = cTt / (cT + tT) if cTt > 0
    // p = tT / (cT + tT)*zT if cTt = 0
    int vT = penn_Tags.size();
    // </s> row not evaluated, all 0 because </s> does not preceed any tag.
    for(int i=0; i<penn_Tags.size()-1; i++){
      double cT = 0;
      double tT = 0;
      // <s> col not evaluated, all 0 because <s> does not succeed any tag.
      for(int j=1; j<penn_Tags.size(); j++){
        if(tagMatrix[i][j] > 0) {
          tT++;
          cT += tagMatrix[i][j];
        }
      }
      double zT = vT - tT;
      for(int j=1; j<penn_Tags.size(); j++){
        double cTt = tagMatrix[i][j];
        if(tagMatrix[i][j] > 0){
          tagMatrix[i][j] = cTt / (cT + tT);
        }else{
          tagMatrix[i][j] = tT / ((cT + tT) * zT);
        }
      }
    }
  }

  public static void smoothenVocabularyMatrix(){
    // cTt=raw freq of word in that tag
    // cT=total freq in that tag
    // tT=unique words in that tag
    // vT=vocab size. so zT=vT-tT
    int vT = vocabulary.size();
    for (int i=0; i<45; i++){
      double tT = vocabularyMatrix.get(i).keySet().size();
      double cT = 0;
      double zT = vT - tT;
      for(double k : vocabularyMatrix.get(i).values()){
        cT += k;
      }
      for(Map.Entry<String, Double> vocab : vocabularyMatrix.get(i).entrySet()){
        double smoothedProb = vocab.getValue() / (cT + tT);
        vocab.setValue(smoothedProb);
      }
      double unseenProb = tT / ((cT + tT) * zT);
      vocabularyMatrix.get(i).put("unknownUnseenWords", unseenProb);
    }
  }

  //

  public static void printModelFile(String modelFileName){
    BufferedWriter bw = null;
    try {
      FileOutputStream fos = new FileOutputStream(modelFileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      bw = new BufferedWriter(osw);
      ////////
      for(int i=1; i<=45; i++){
        bw.write(penn_Tags.get(i) + ", ");
      }
      bw.write(penn_Tags.get(46) + "\n");
      for(int i=0; i<=45; i++){
        for(int j=1; j<=45; j++){
          bw.write(tagMatrix[i][j] + ", ");
        }
        bw.write(tagMatrix[i][46] + "\n");
      }
      ////
      // bw.write(vocabulary.size() + "\n");
      ////
      for(int i=0; i<45; i++){
        bw.write(penn_Tags.get(i+1)+ " " + vocabularyMatrix.get(i).keySet().size() + "\n");
        for (Map.Entry<String, Double> entry : vocabularyMatrix.get(i).entrySet()){
          bw.write(entry.getKey() + " " + entry.getValue() + "\n");
        }
      }

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

  public static void main(String[] args){
    // command is java build_tagger sents.train sents.devt model_file
    String trainFileName = args[0];
    String devtFileName = args[1];
    String modelFileName = args[2];
    String line = null;
    // System.out.println(Arrays.toString(penn_Tags.toArray()));
    initializeMatrix();
    try{
      BufferedReader br = new BufferedReader(new FileReader(trainFileName));
      while((line=br.readLine()) != null){
        processLine(line);
      }
      // BufferedReader brd = new BufferedReader(new FileReader(devtFileName));
      // while((line=brd.readLine()) != null){
      //   processLine(line);
      // }

    }catch(Exception e){
        System.err.println(e + ": no file to read in main");
    }
    smoothenVocabularyMatrix();
    // printVocabMatrix(vocabularyMatrix);
    smoothProbabilityTagMatrix();
    // System.out.println(vocabulary.size());
    // printTagsMatrix(tagMatrix);
    printModelFile(modelFileName);
  }
}
