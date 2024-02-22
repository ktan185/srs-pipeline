package gpt;

import java.util.List;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Get the prompt engineering string for setting up system.
   *
   * @return the generated system prompt string
   */
  public static String systemPrompt() {
    return "You are a developer tasked with improving oracle assertions within " +
    "methods in java. A perfect assertion Oracle is defined as one without Oracle " +
    "deficiencies. Oracle deficiencies can be categorised into two categories:\n\n" +
    "1. False negative: a reachable program state where the given assertion is True, " +
    "although such state does not belong to the set of expected states according to " +
    "the intended behaviour.\n\n" +
    "2. False positive: a reachable program state where the given assertion is False, " +
    "although such state does belong to the set of expected states according to the " +
    "intended program behaviour.\n\n" +
    "I will update you if there are any oracle deficiencies. You must propose an " +
    "improved assertion.";
  }

  public static String falsePositiveFeedback(String testcases) {
    return "The assertion has a false positive. Here is a test case which leads to a false positive:\n" 
      + testcases 
      + "A corrected assertion would be:";  
  }

  public static String falseNegativeFeedback(List<String> mutationData) {
    String lineNumber = mutationData.get(0);
    String operation = mutationData.get(1);
    StringBuilder resultBuilder = new StringBuilder();

    resultBuilder.append("The assertion has a false negative, \n")
                 .append("if we change bytecode at line ").append(lineNumber)
                 .append(" with ").append(operation).append(" we obtain an incorrect program state, the value of: \n");

    // Check if there are more elements for variables
    for (int i = 2; i < mutationData.size(); i++) {
        resultBuilder.append(mutationData.get(i));
        if (i < mutationData.size() - 1) {
            resultBuilder.append("\n"); // Add newline if there are more variables to append
        }
    }

    resultBuilder.append("\nthe assertion passes but should fail. \n")
                 .append("A corrected assertion would be:");
    return resultBuilder.toString();
  }
}
