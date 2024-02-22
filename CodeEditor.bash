#!/bin/bash

set -e

# File containing the Java class
FILE="Implementation/src/main/java/MethodImprovement.java"

# Line number to edit (Change per method)
LINE_NUMBER=11

# Check if improvedAssertion.txt was created and is not empty
if [ -s textfiles/improvedAssertion.txt ]; then
    # Read the first line from the file
    NEW_CONTENT=$(head -n 1 textfiles/improvedAssertion.txt)

    # Escape special characters in NEW_CONTENT
    ESCAPED_NEW_CONTENT=$(echo "$NEW_CONTENT" | sed 's/[&/|]/\\&/g')

    # Replace line 16 with ESCAPED_NEW_CONTENT using sed
    # -i '' for macOS, for Linux it might just be -i
    sed -i '' "${LINE_NUMBER}s|.*|${ESCAPED_NEW_CONTENT}|" Implementation/src/main/java/MethodImprovement.java
else
    echo "No improved assertion was provided."
    exit 1
fi

# Copy the edited file into the Improvement folder.
# Assuming Improvement folder is at the root of OASIS
echo "Copying file from $FILE to Improvement/src/"
cp "$FILE" Improvement/src/
echo "File copied."

# Run OASIs on new assertion: (update this line: ONLY CHANGE METHOD NAME)
./run.sh MethodImprovement /Improvement/src/ copySignInt
