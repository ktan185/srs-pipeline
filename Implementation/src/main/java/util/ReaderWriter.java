package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class ReaderWriter {

  private static final String COUNTER_FILE = "counter.txt";
    // Method to save all assertions generated by GPT to a textFile.
  public static void writeAssertionsToFile(Set<String> improvedAssertions, String filePath) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
      for (String item : improvedAssertions) {
          writer.write(item);
          writer.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } 
  } 

  public static int readCounter() throws IOException {
    File file = new File(COUNTER_FILE);
    if (!file.exists()) {
      return 0;
    }
    BufferedReader br = new BufferedReader(new FileReader(file));
    String line = br.readLine();
    br.close();
    return Integer.parseInt(line);
  }

  public static void writeCounter(int count) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(COUNTER_FILE));
    bw.write(Integer.toString(count));
    bw.close();
  }
}
