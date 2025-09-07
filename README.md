# Bjoern IntelliJ Plugin

An IntelliJ IDEA plugin for [Bjoern](https://github.com/Mehtrick/bjoern) BDD specification language support.

## Features

- **File Type Recognition**: Recognizes `.zgr` files as Bjoern specification files with custom teal gem icon
- **Syntax Highlighting**: Highlights BDD keywords in Bjoern files:
  - `Feature:`
  - `Background:`
  - `Given:`
  - `When:`
  - `Then:`
  - `Scenario:`
  - `Scenarios:`
- **Keyword Validation**: Only valid BDD keywords are highlighted; others are marked as invalid
- **Variable Highlighting**: Double-quoted strings are highlighted as variables with vibrant colors (e.g., `"john.doe"`, `"123"`)
- **Auto-indentation**: Proper YAML-based indentation support
- **Smart Autocomplete**: Intelligent code completion with context-aware suggestions:
  - BDD keyword completion
  - Dynamic statement suggestions based on existing file content
  - Variable placeholders (variables in quotes are replaced with `""` for easy editing)
- **YAML Structure Support**: Based on YAML parsing for proper structure validation
- **IntelliJ Integration**: Seamless integration with IntelliJ IDEA and other JetBrains IDEs

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
    Given:
      - A user with username "john.doe"
      - Password is "securePassword123"
    When:
      - User logs in with "john.doe"
    Then:
      - Login should display "Welcome!"
```

Variables in double quotes like `"john.doe"` and `"securePassword123"` are highlighted differently to distinguish them from regular text.

### Smart Autocomplete

The plugin provides intelligent code completion:

1. **BDD Keywords**: Type keywords like `Given:`, `When:`, `Then:` for quick insertion
2. **Dynamic Statement Suggestions**: When typing under a BDD section, get suggestions based on existing statements in the same file
3. **Variable Placeholders**: Suggestions automatically replace variables with empty quotes (`""`) for easy editing

Example: In a file with `- there are "2" bottles of wine`, typing under a `Given:` section will suggest `- there are "" bottles of wine` with the cursor positioned inside the quotes.

## Installation

1. Build the plugin using Gradle:
   ```bash
   ./gradlew buildPlugin
   ```

2. Install the plugin in IntelliJ IDEA:
   - Go to `File → Settings → Plugins`
   - Click `Install Plugin from Disk...`
   - Select the built plugin JAR file

## Examples

The `examples/` directory contains sample `.zgr` files demonstrating the Bjoern language syntax:

- `kassenAutomat.zgr` - Vending machine test example
- `userLogin.zgr` - User authentication test example  
- `variableHighlighting.zgr` - Example showing variable highlighting features
- `enhancedFeatures.zgr` - Demonstrates keyword validation and enhanced features
- `completionDemo.zgr` - Shows smart autocomplete functionality

## Development

For comprehensive plugin development documentation, see **[PLUGIN-DEVELOPMENT-GUIDE.md](PLUGIN-DEVELOPMENT-GUIDE.md)** - this guide explains how the plugin works and provides a foundation for understanding IntelliJ plugin development, even if you have no prior experience.

### Plugin Architecture

The Bjoern plugin extends IntelliJ IDEA's core functionality through several key components:

- **Language Support**: Registers `.zgr` files as a new language type with custom syntax
- **Syntax Highlighting**: Colors BDD keywords, variables, and validates keyword correctness  
- **Smart Completion**: Context-aware auto-completion with dynamic suggestions from file content
- **Formatting**: Auto-indentation rules that understand BDD structure hierarchy
- **File Recognition**: Custom teal gem icon and proper file type association

The plugin inherits from YAML parsing for structural validation while adding BDD-specific enhancements.

### Quick Start

```bash
# Build the plugin
./gradlew build

# Run IntelliJ with the plugin for testing
./gradlew runIde

# Run tests
./gradlew test
```

### Testing

Create `.zgr` files and verify that:
1. They are recognized as Bjoern files with teal gem icon
2. BDD keywords are highlighted properly
3. Invalid keywords are marked as errors
4. Variables in double quotes are highlighted with vibrant colors
5. Auto-indentation works correctly
6. Smart autocomplete provides relevant suggestions
7. YAML structure is validated

## Requirements

- IntelliJ IDEA 2022.3 or later (recommended: 2023.3.7 or later)
- Java 17 or later
- Gradle 8.10.2 (included via wrapper)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Links

- [Bjoern Project](https://github.com/Mehtrick/bjoern)
- [Bjoern VS Code Extension](https://marketplace.visualstudio.com/items?itemName=mehtrick.bjoern)