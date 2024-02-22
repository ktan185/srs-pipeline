## SRS GPT Pipeline

This is a prototype pipeline used for testing automatic assertion improvement leveraging GPT created for
UOA Summer Research Scholarship 2024.

## How to run

Ensure that the correct Java version is set, This line can be executed in CLI:

export JAVA_HOME=`/usr/libexec/java_home -v 1.8`

apiproxy.config file containing OpenAI api email & key should be placed in the Implementatation folder.

Because this tool is very rudamentary, it is not "Fully" automated and requires some setup before running.
The following files need to be updated before running: (Potientally fix these issues in the future)

1. MethodImprovement.java must be updated with the method containing the assertion we want to improve. Instructions on what to do for this file is written in the file itself.

2. initmsg.txt is the initial prompt we send the LLM. at the bottom (line 42 onwards) we need to paste the method from MethodImprovement.java, and manually insert the line numbers. (Ensure that the line numbers are the same as the ones in MethodImprovement.java -> This is how we keep FN feedback consistent)

3. CodeEditor.bash needs to be updated: see instructions in MethodImprovement.java

4. Main.java line 34 needs to be updated MethodImprovement\_<INSERT_METHOD_NAME>\_Test.java";

## Notes

OASIs tool created by Gunel Jahangirova is included in this repository, find the original [repository](https://github.com/guneljahan/OASIs) here.

## Related Publications

Gunel Jahangirova, David Clark, Mark Harman, and Paolo Tonella "Test oracle assessment and improvement", In Proceedings of the 25th International Symposium on Software Testing and Analysis (ISSTA 2016).
