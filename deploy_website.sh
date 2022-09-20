#!/bin/bash

set -ex


# Copy mkdocs sources
cp -R ./docs/ ./build/docs/

# Generate the API docs
./gradlew dokkaGfmMultiModule

# Add readme as index
cat README.md > docs/index.md

# Remove redundant link
sed -i '/Documentation at/d' ./build/docs/index.md
sed -i 's~](docs/~](~g' ./build/docs/index.md
sed -i 's~.md)~)~g' ./build/docs/index.md
