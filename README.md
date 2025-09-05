# Bjoern IntelliJ Plugin

An IntelliJ IDEA plugin for [Bjoern](https://github.com/Mehtrick/bjoern) BDD specification language support.

## Features

- **File Type Recognition**: Recognizes `.zgr` files as Bjoern specification files
- **Syntax Highlighting**: Highlights BDD keywords in Bjoern files:
  - `Feature:`
  - `Background:`
  - `Given:`
  - `When:`
  - `Then:`
  - `Scenario:`
  - `Scenarios:`
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
      - Precondition step
    When:
      - Action step
    Then:
      - Verification step
```

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

## Development

### Building

```bash
./gradlew build
```

### Testing

Create `.zgr` files and verify that:
1. They are recognized as Bjoern files
2. BDD keywords are highlighted properly
3. YAML structure is validated

## Requirements

- IntelliJ IDEA 2022.3 or later
- Java 11 or later

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Links

- [Bjoern Project](https://github.com/Mehtrick/bjoern)
- [Bjoern VS Code Extension](https://marketplace.visualstudio.com/items?itemName=mehtrick.bjoern)