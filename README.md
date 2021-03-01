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

This project contains the [ANTLR example grammars](https://github.com/antlr/grammars-v4) as a git subtree under [third_party/antlr_grammars_v4](third_party/antlr_grammars_v4). Please see the individual grammar files for license and author information.

They can be updated with the following command:

```bash
git subtree pull --squash --prefix=third_party/antlr_grammars_v4 git@github.com:antlr/grammars-v4.git <branch>
```

All demo code and the included third party repositories are not bundled in the release jars.