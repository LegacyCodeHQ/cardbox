#!/usr/bin/env sh

./gradlew test cli:executable
cp ./cli/build/exec/cardbox /usr/local/bin/
chmod +x /usr/local/bin/cardbox
