# RSyntaxTextArea ANTLR 4 Extension

[![Maven Central](https://img.shields.io/maven-central/v/de.tisoft.rsyntaxtextarea/rsyntaxtextarea-antlr4-extension)](https://search.maven.org/artifact/de.tisoft.rsyntaxtextarea/rsyntaxtextarea-antlr4-extension)
[![GitHub](https://img.shields.io/github/license/tisoft/rsyntaxtextarea-antlr4-extension)](LICENSE)

This project contains an extension for the [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) library, that allows the usage of [ANTLR 4](https://www.antlr.org) lexers and parsers.

## Third party code

This project contains the [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) as a git subtree under [third_party/RSyntaxTextArea](third_party/RSyntaxTextArea). It is used by the [demo program](src/test/java/de/tisoft/rsyntaxtextarea/modes/AntlrTokenMakerTest.java).

It can be updated with the following command:

```bash
git subtree pull --squash --prefix=third_party/RSyntaxTextArea git@github.com:bobbylight/RSyntaxTextArea.git <tag>
```
The demo program also uses several [example grammars](src/test/antlr4/third_party). Please see the individual grammar files for license and author information.

All demo code is not bundled in the release jars.