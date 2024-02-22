import gpt.ChatMessage;
import gpt.GptPromptEngineering;
import gpt.openai.ApiProxyException;
import gpt.openai.ChatCompletionRequest;
import gpt.openai.ChatCompletionResult;
import util.ReaderWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import feedback.ByteCodeExtractor;
import feedback.TestCaseExtractor;

public class Main {
  
  private static Set<String> improvedAssertions =  new HashSet<>();
  private static String scriptPath = "CodeEditor.bash";
  private static String falsePositive = "False Positive Detected!";
  private static String falseNegative = "False Negative Detected!";
  private static String noFalseNegative = "No False Negative Detected!";
 
  // This file needs to be edited for method: change _(METHOD NAME)_Test.java
  private static final String FEEBACK_FILE = "Improvement/src/MethodImprovement_copySignInt_Test.java";
  private static final String FALSEPOSITIVEFEEDBACK = "textfiles/testcasesToGPT.txt";

  public static void main(String[] args) throws ApiProxyException, IOException {

    Scanner scanner = new Scanner(System.in);

    int timeBudget = scanner.nextInt();

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println("Time is up! Terminating the script.");
        System.exit(0);
      }
    }, 600 * 1000); // Testing config of 10 minutes
    System.gc();
    ChatCompletionRequest chatRequest = new ChatCompletionRequest();
    int count = 0;
    String fileName = null;
    try {
      count = ReaderWriter.readCounter();
      fileName = "output_" + count + ".txt";
      count++;
      ReaderWriter.writeCounter(count);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    String filePath = "Implementation/AllImprovedAssertions/" + fileName;

    // Initialize the chat session with the system prompt
    String systemPrompt = GptPromptEngineering.systemPrompt();
    System.out.println("Initialising GPT with prompt...");
    chatRequest.addMessage("system", systemPrompt);

    // Read the first message from the file
    String firstMsg = readFileContent("textfiles/initmsg.txt");
    System.out.println(firstMsg);
    // Add the first message from the file to the chat request
    runGPT(chatRequest, firstMsg);      
    String scriptOutput = "";
    while (true) {
      System.out.println("Running OASIs...");
      scriptOutput = runBashScriptAndGetOutput(scriptPath);

      if (scriptOutput.equals(falsePositive)) { 
        TestCaseExtractor.extractTestCases(FEEBACK_FILE, FALSEPOSITIVEFEEDBACK);
        // Convert the test cases to a string
        String testcases = new String(Files.readAllBytes(Paths.get(FALSEPOSITIVEFEEDBACK)));
        String feedback = GptPromptEngineering.falsePositiveFeedback(testcases);
        System.out.println(feedback);
        if (!runGPT(chatRequest, feedback)) break; 
      }
      
      if (scriptOutput.equals(falseNegative)) {
        List<String> mutationData = ByteCodeExtractor.getExtractedVariables(FEEBACK_FILE);
        // Take the variables and create new message to send to GPT.
        String feedback = GptPromptEngineering.falseNegativeFeedback(mutationData);
        System.out.println(feedback);
        if (!runGPT(chatRequest, feedback)) break; 
      }

      if (scriptOutput.equals(noFalseNegative)) break;
    }

    ReaderWriter.writeAssertionsToFile(improvedAssertions, filePath);
    timer.cancel();
    // scanner.close();
    System.out.println("Exiting.");
    System.exit(0);
  }

  // Method to read the content of the file
  private static String readFileContent(String filePath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  // Method to send msg to GPT
  private static boolean runGPT(ChatCompletionRequest chatRequest, String input) {
    try {
      chatRequest.addMessage("user", input);
      ChatCompletionResult result = chatRequest.execute();
      ChatCompletionResult.Choice choice = result.getChoice(0);
      ChatMessage responseMessage = choice.getChatMessage();
      String response = responseMessage.getContent();
      if(!printResponse(response)) {
        return false;
      } 
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
    return true;
  }
  // Method to print out GPT's improve assertion
  private static boolean printResponse(String response) {
    String improvedAssertion = getAssertion(response);
    
    // First, check for null
    if (improvedAssertion == null) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("textfiles/improvedAssertion.txt"))) {
          writer.write("Error: GPT did not provide an assertion.");
          return false;
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    // Then, check for other conditions
    System.out.println(improvedAssertion);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("textfiles/improvedAssertion.txt"))) {
      if (!improvedAssertion.equals("Repeated Assertion!")) {
        writer.write(improvedAssertion);
        return true;
    } else {
        writer.write("Error: GPT provided a repeated assertion.");
        writer.write(improvedAssertion);
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
  // Method to obtain the assertion from GPT's response.
  private static String getAssertion(String gptResponse) {
    String[] lines = gptResponse.split("\n");
    for(String line : lines) {
      if(line.trim().startsWith("assert")) {
        String assertion = line.trim();
        // Store the assertion globally:
        if (!improvedAssertions.contains(assertion)) {
          improvedAssertions.add(assertion);
        } else {
          return "Repeated Assertion!";
        }
        // Output the line
        return assertion;
      }
    }
    return null;
  }
  // Method to run the bash script
  private static String runBashScriptAndGetOutput(String scriptPath) {
    StringBuilder output = new StringBuilder();
    String lastLine = "";
    try {
      ProcessBuilder builder = new ProcessBuilder("/bin/bash", scriptPath);
      builder.redirectErrorStream(true); // Redirect error stream to the standard output stream

      Process process = builder.start();

      // Read the output of the script
      try (InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader)) {
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println("OASIs reports: " + line);
          lastLine = line; // Store the last line
        }
      }
      // Process the last line of the script
      if (lastLine.contains(falsePositive)) {
        output.append(lastLine);
      } else if (lastLine.contains(falseNegative)) {
        output.append(lastLine);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return output.toString();
  }



}
