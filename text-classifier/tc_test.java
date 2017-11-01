import java.util.*;
import java.io.*;

public class tc_test {
  public static void main(String[] args){
    // command is java tc-test stopword-list model test-list test-class-list
    String stopWords = args[0];
    String modelFileName = args[1];
    String testList = args[2];
    String testClassList = args[3];
    System.out.println(stopWords + " " + modelFileName + " " + testList + " " + testClassList);
  }
}
