package feedback;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class TestCaseExtractor {

  public static void extractTestCases(String javaFilePath, String outputFilePath) throws IOException {
    Path path = Paths.get(javaFilePath);
    StringBuilder contentBuilder = new StringBuilder();

    try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
      stream.forEach(s -> contentBuilder.append(s).append("\n"));
    }

    String content = contentBuilder.toString();
    String[] lines = content.split("\n");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
      boolean inTest = false;
      for (String line : lines) {
        if (line.trim().startsWith("@Test")) {
          inTest = true;
        }

        if (inTest) {
          writer.write(line);
          writer.newLine();

          if (line.trim().endsWith("}")) {
            inTest = false;
          }
        }
      }
    }
    System.out.println("Test cases extracted to " + outputFilePath);
  }

}
