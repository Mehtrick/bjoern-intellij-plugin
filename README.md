# Bjoern IntelliJ Plugin

An IntelliJ IDEA plugin for [Bjoern](https://github.com/Mehtrick/bjoern) BDD specification language support.

## Features

- **File Type Recognition**: Recognizes `.zgr` files as Bjoern specification files with custom teal gem icon
- <a id="syntax-highlighting"></a>**Syntax Highlighting**: Highlights BDD keywords in Bjoern files:
  - `Feature:`
  - `Version:`
  - `Reference:`
  - `Changelog:`
  - `Deprecated:`
  - `Background:`
  - `Given:`
  - `When:`
  - `Then:`
  - `Scenario:`
  - `Scenarios:`
- **Keyword Validation**: Only valid BDD keywords are highlighted; others are marked as invalid
- <a id="reference-inspection"></a>**Reference Inspection**: Warns when the `Reference:` field contains an invalid URL scheme (only `http://` and `https://` are allowed) or a malformed markdown link `[text](url)`
- <a id="deprecated-field-support"></a>**Deprecated Field Support**: Highlights `Deprecated:` inside scenarios, offers `true`/`false` completion, and validates placement and value via structural inspection
- **Variable Highlighting**: Double-quoted strings are highlighted as variables with vibrant colors (e.g., `"john.doe"`, `"123"`)
- **Comment Support**: Hash comments are properly highlighted with standard comment colors:
  - Full-line comments: `#This is a comment`
  - Inline comments: `- A statement #with inline comment`
- **Auto-indentation**: Proper YAML-based indentation support
- **Smart Autocomplete**: Intelligent code completion with context-aware suggestions:
  - BDD keyword completion
  - Dynamic statement suggestions based on existing file content
  - Variable placeholders (variables in quotes are replaced with `""` for easy editing)
- **YAML Structure Support**: Based on YAML parsing for proper structure validation
- <a id="structural-inspection"></a>**Structural Inspection**: Checks required top-level fields, duplicate scenario names, empty step blocks, and `Deprecated:` declarations
- <a id="breadcrumbs"></a>**Breadcrumbs**: Navigate scenario and step sections; deprecated scenarios receive a label
- <a id="file-template"></a>**File Template**: Create a specification using **New → Bjoern Spec File**

## About Bjoern

Bjoern is a universal BDD test generator that creates Java test classes from BDD-style specification files. The main focus is to generate Java classes from `.zgr` files to ensure synchronization between the specification and the code.

### Bjoern Language Structure

Bjoern files use YAML syntax with specific BDD keywords:

```yaml
Feature: Test example
Background:
  Given:
    - Initial setup step
Scenarios:
  - Scenario: Test case name
    Deprecated: false
    Given:
      - A user with username "john.doe"
      - Password is "securePassword123"
    When:
      - User logs in with "john.doe"
    Then:
      - Login should display "Welcome!"
  - Scenario: Legacy login flow
    Deprecated: true
    Given:
      - A user with username "legacy.user"
    When:
      - User logs in with old API
    Then:
      - A deprecation warning is shown
```

Variables in double quotes like `"john.doe"` and `"securePassword123"` are highlighted differently to distinguish them from regular text.

### Smart Autocomplete

The plugin provides intelligent code completion:

1. **BDD Keywords**: Type keywords like `Given:`, `When:`, `Then:` for quick insertion
2. **Dynamic Statement Suggestions**: When typing under a BDD section, get suggestions from the current file and other indexed `.zgr` files in the project, grouped by Given/When/Then context
3. **Variable Placeholders**: Suggestions replace variables with empty quotes (`""`); the caret moves to the first placeholder and Tab navigates parameters

Example: In a file with `- there are "2" bottles of wine`, typing under a `Given:` section will suggest `- there are "" bottles of wine` with the cursor positioned inside the quotes.

## Installation

1. Install JDK 21 for the baseline build, then use the Gradle wrapper:
   ```bash
   ./gradlew buildPlugin
   ```

2. Install the plugin in IntelliJ IDEA:
   - Go to `File → Settings → Plugins`
   - Click `Install Plugin from Disk...`
   - Select the plugin ZIP from `build/distributions/`

The plugin supports IntelliJ IDEA **2026.1–2026.3** (builds `261` through `263.*`) and requires the bundled `org.jetbrains.plugins.yaml` plugin. The default compilation SDK is unified IDEA 2026.1.5.1; CI exercises each supported 2026 generation. See [Requirements](#requirements) and the [compatibility investigation](docs/intellij-compatibility.md).

## Examples

The `examples/` directory contains sample `.zgr` files demonstrating the Bjoern language syntax:

- `kassenAutomat.zgr` - Vending machine test example
- `userLogin.zgr` - User authentication test example  
- `variableHighlighting.zgr` - Example showing variable highlighting features
- `enhancedFeatures.zgr` - Demonstrates keyword validation and enhanced features
- `completionDemo.zgr` - Shows smart autocomplete functionality
- `commentTest.zgr` - Demonstrates hash comment support (full-line and inline)
- `complexCommentTest.zgr` - Shows comments mixed with quoted variables

## Development

For comprehensive plugin development documentation, see **[PLUGIN-DEVELOPMENT-GUIDE.md](PLUGIN-DEVELOPMENT-GUIDE.md)** - this guide explains how the plugin works and provides a foundation for understanding IntelliJ plugin development, even if you have no prior experience.

### Plugin Architecture

The Bjoern plugin extends IntelliJ IDEA's core functionality through several key components:

- **Language Support**: Registers `.zgr` files as a new language type with custom syntax
- **Syntax Highlighting**: Colors BDD keywords, variables, and validates keyword correctness  
- **Smart Completion**: Context-aware auto-completion with dynamic suggestions from file content
- **Formatting**: Auto-indentation rules that understand BDD structure hierarchy
- **File Recognition**: Custom teal gem icon and proper file type association

`BjoernLanguage` derives from IntelliJ's YAML language. `BjoernParserDefinition` delegates parsing to YAML, and `BjoernFile` extends `YAMLFileImpl`. Highlighting uses a separate validating lexer.

The IDE plugin has no direct dependency on the [Bjoern Gradle framework](https://github.com/Mehtrick/bjoern). The framework reads `.zgr` files to generate JUnit test classes, step interfaces, and documentation. This plugin supports editing those files; keyword and validation changes need to stay aligned across both projects.

### Quick Start

```bash
# Build the plugin
./gradlew build

# Run IntelliJ with the plugin for testing
./gradlew runIde

# Run tests
./gradlew test

# Check project configuration and packaged plugin structure
./gradlew verifyPluginProjectConfiguration verifyPluginStructure

# Check binary compatibility against the configured IDE set
./gradlew verifyPlugin
```

`./build.sh` runs tests, builds the installable plugin ZIP, and checks project configuration and package structure. `runIde` launches the configured development IDE in a sandbox. To test or launch another supported SDK, pass `-PplatformVersion=2026.2.5` or `-PplatformVersion=2026.3`; these targets require an installed JDK 25. Java bytecode stays at version 21 for the 2026.1 baseline.

### Testing

Test sources are in `src/test/java` and use IntelliJ's `BasePlatformTestCase` fixtures with JUnit. They exercise real `.zgr` parsing, highlighting, completion, inspections, breadcrumbs, and Tab navigation, plus lexer and Reference validation cases. Gradle is configured to fail if no tests are discovered. Reports are written to `build/reports/tests/test/`. See the [testing guide](PLUGIN-DEVELOPMENT-GUIDE.md#testing-and-compatibility-checks).

For manual checks, create `.zgr` files and verify that:

1. They are recognized as Bjoern files with teal gem icon
2. BDD keywords are highlighted properly
3. Invalid keywords are marked as errors
4. Variables in double quotes are highlighted with vibrant colors
5. Hash comments are highlighted with standard comment colors (both inline and full-line)
6. Auto-indentation works correctly
7. Smart autocomplete provides relevant suggestions
8. YAML structure is validated
9. `Deprecated:` is only accepted inside scenarios with values `true` or `false`

## Requirements

The checked-in build configuration is:

| Setting | Value |
| --- | --- |
| Development SDK | Unified IntelliJ IDEA 2026.1.5.1 (`IU`) |
| Java bytecode target | 21 |
| Build/test toolchain | JDK 21 for 2026.1; JDK 25 for 2026.2 and 2026.3 |
| Gradle wrapper | 9.9.1, with distribution SHA-256 verification |
| IntelliJ Platform Gradle Plugin | 2.20.1 |
| Declared IDE build range | `261`–`263.*` (2026.1–2026.3) |
| CI and Plugin Verifier targets | 2026.1.5.1, 2026.2.5, 2026.3 |
| Required IDE plugin | `org.jetbrains.plugins.yaml` |

These values come from `build.gradle.kts`, `gradle/wrapper/gradle-wrapper.properties`, and `src/main/resources/META-INF/plugin.xml`. Other JetBrains products require their own verification and the YAML dependency.

For the reproduced YAML compatibility issue, its correction, and Java 25 implications, read [IntelliJ compatibility investigation](docs/intellij-compatibility.md). That report distinguishes platform changes from confirmed plugin failures.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Links

- [Bjoern Project](https://github.com/Mehtrick/bjoern)
- [Bjoern VS Code Extension](https://marketplace.visualstudio.com/items?itemName=mehtrick.bjoern)
