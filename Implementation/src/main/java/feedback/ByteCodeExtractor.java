package feedback;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteCodeExtractor {

  /* Method used to extract data from mutation byte code generated from OASIs
   * 
   * returns an array with the variables in the order: 
   * Line number affected, Byte code instruction, outcome of variables.
  */ 
  public static List<String> getExtractedVariables(String FEEBACK_FILE) {
    String s = null;
    try {
      s = extractByteCode(FEEBACK_FILE);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<String> variables = extractVariables(s);
    String variablePairsMutated = variables.get(variables.size()-1);
    variables.remove(variables.size()-1);
    parseAndStoreVariables(variables,variablePairsMutated);
    return variables;
  }
  
  private static String extractByteCode(String inputFile) throws IOException {
    StringBuilder mutation = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.trim().startsWith("* Goal 1.")) { // Only want to feedback 1 mutation.
            mutation.append(line).append(System.lineSeparator());
            break;
          }
        } 
    }
    System.out.println(mutation.toString());
    return mutation.toString();
  }

  private static List<String> extractVariables(String inputString) {
    ArrayList<String> extractedVars = new ArrayList<>();
    String patternString = ":(\\d+) - (.*?) \\(([^)]+)\\)";
    Pattern pattern = Pattern.compile(patternString);

    try (Scanner scanner = new Scanner(inputString)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
          extractedVars.add(matcher.group(1)); // Adds the first group (digits after colon) to the list
          extractedVars.add(matcher.group(2)); // Adds the second group (text before parentheses) to the list
          extractedVars.add(matcher.group(3)); // Adds the third group (text inside parentheses) to the list
        }
      }
    }

    return extractedVars;
  }

  private static void parseAndStoreVariables(List<String> variables, String input) {
    String[] pairs = input.split("; ");
    for (String pair : pairs) {
      if (!pair.isEmpty()) {
        // Splitting the key-value pair
        String[] keyValue = pair.split(":");
        String key = keyValue[0];
        if (keyValue.length > 1) {
          // If there is a value associated with the key
          String[] values = keyValue[1].split(",");
          String fromValue = values[1];
          String toValue = values[0];
          variables.add(new String(key + " changes from " + fromValue + " to " + toValue));
        } else {
          // If there is no value associated with the key
          variables.add(new String(key + " negated"));
        }
      }
    }
  }

}
