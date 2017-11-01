import java.util.*;
import java.io.*;

public class tc_train {
  public static void main(String[] args){
    // command is java tc_train stopword-list train-class-list model
    String stopWords = args[0];
    String trainClass = args[1];
    String modelFileName = args[2];
    System.out.println(stopWords + " " + trainClass + " " + modelFileName);
  }
}
