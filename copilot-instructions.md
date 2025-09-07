# Copilot Instructions for Bjoern IntelliJ Plugin

## Project Overview

This is an IntelliJ IDEA plugin that provides language support for Bjoern BDD (Behavior-Driven Development) specification files with `.zgr` extension. The plugin includes syntax highlighting, auto-completion, formatting, and validation features specifically designed for the Bjoern BDD language.

## Key Architecture Decisions

### File Type and Language Implementation

- **BjoernFile** extends `YAMLFileImpl` (not `PsiFileBase`) to ensure compatibility with IntelliJ's YAML breadcrumbs provider
- **BjoernLanguage** is registered as "Bjoern" with proper MIME type associations
- **BjoernFileType** handles `.zgr` file recognition with a teal gem icon

### Syntax Highlighting

- **BjoernSyntaxHighlighter** extends YAML highlighter for base functionality
- Uses unique TextAttributesKey prefixes (`BJOERN_*`) to avoid conflicts with YAML plugin
- **Color scheme**:
  - Keywords (Feature, Background, Scenarios, Given, When, Then): Blue/cyan
  - Variables in double quotes: Green/yellow highlighting
  - Normal text: Theme-adaptive subtle colors (white in dark, black in light)

### Auto-completion System

- **BjoernCompletionContributor** provides dynamic completion based on file content
- **Pattern matching**: Uses universal patterns that trigger on typing
- **Context awareness**: Detects if user is under Background or Scenario sections
- **Dynamic extraction**: Scans existing file content for Given/When/Then statements
- **Variable placeholders**: Replaces quoted variables with `""` for easy editing
- **Multi-language registration**: Registered for both "Bjoern" and "yaml" languages

### Formatting and Indentation

- **BjoernLineIndentProvider**: Smart line indentation when pressing Enter
- **BjoernFormattingModelBuilder**: Document formatting (Ctrl+Alt+L) with Bjoern BDD rules

#### Indentation Rules
```
Feature: Test                    # Column 0
Background:                      # Column 0
  Given:                         # 2 spaces (under Background)
    - statement                  # 4 spaces (list item under Background Given)
Scenarios:                       # Column 0
  - Scenario: Name               # 2 spaces (under Scenarios)
    Given:                       # 4 spaces (under Scenario)
      - statement                # 6 spaces (list item under Scenario Given)
    When:                        # 4 spaces (under Scenario)
      - statement                # 6 spaces (list item under Scenario When)
    Then:                        # 4 spaces (under Scenario)
      - statement                # 6 spaces (list item under Scenario Then)
```

## Build Configuration

### Modern IntelliJ Platform Setup
- **IntelliJ Platform Gradle Plugin**: 2.0.1 (stable version for compatibility)
- **Gradle**: 8.10.2 (latest)
- **Java**: 17 (with explicit toolchain configuration)
- **IntelliJ version**: 2023.3.8 (stable)
- **Version range**: 233 to 234.* (conservative for stability)

### Dependencies
```gradle
intellijPlatform {
    intellijIdeaCommunity("2023.3.8")
    bundledPlugin("org.jetbrains.plugins.yaml")
    pluginVerifier()
    zipSigner()
    instrumentationTools()
}
```

## Common Issues and Solutions

### 1. ClassCastException with Breadcrumbs
**Problem**: BjoernFile cannot be cast to YAMLFile
**Solution**: Extend YAMLFileImpl instead of PsiFileBase

### 2. TextAttributesKey Conflicts
**Problem**: IllegalStateException about YAML_SCALAR_KEY already registered
**Solution**: Use unique prefixes like `BJOERN_SCALAR_KEY` instead of `YAML_SCALAR_KEY`

### 3. Completion Not Triggering
**Problem**: BjoernCompletionContributor called but addCompletion never executed
**Solution**: 
- Use specific patterns targeting .zgr files and YAML tokens
- Register for both "Bjoern" and "yaml" languages
- Use universal patterns that work across contexts

### 4. Registry Access During IDE Initialization
**Problem**: Single double quotes cause registry access errors
**Solution**: Handle unmatched quotes gracefully in BjoernDoubleQuotedStringLexer

### 5. StringIndexOutOfBoundsException
**Problem**: Index errors in completion contributor's isUnderListItem method
**Solution**: Add proper bounds checking before substring operations

### 6. Java Version Compatibility
**Problem**: IllegalArgumentException during plugin loading
**Solution**: Use stable plugin versions and explicit Java 17 toolchain

## Testing Strategy

The plugin includes comprehensive tests:
- **BjoernFileTypeTest**: File type and language registration
- **BjoernSyntaxHighlighterTest**: Syntax highlighting verification
- **BjoernTokenizationTest**: Lexer and parser functionality
- **BjoernCompletionTest**: Auto-completion features

## File Structure

```
src/
├── main/
│   ├── java/de/mehtrick/bjoern/
│   │   ├── BjoernFile.java                    # File implementation
│   │   ├── BjoernFileType.java                # File type definition
│   │   ├── BjoernLanguage.java                # Language definition
│   │   ├── BjoernSyntaxHighlighter.java       # Syntax highlighting
│   │   ├── BjoernCompletionContributor.java   # Auto-completion
│   │   ├── BjoernLineIndentProvider.java      # Smart indentation
│   │   ├── BjoernFormattingModelBuilder.java  # Document formatting
│   │   └── lexer/                             # Lexer implementations
│   └── resources/
│       ├── META-INF/plugin.xml               # Plugin configuration
│       └── icons/                            # Teal gem icon
└── test/
    └── java/de/mehtrick/bjoern/              # Test files
```

## Plugin Registration (plugin.xml)

Key extensions registered:
- `fileType`: Associates .zgr files with BjoernFileType
- `lang.syntaxHighlighterFactory`: Provides syntax highlighting
- `completion.contributor`: Enables auto-completion
- `lang.formatter`: Document formatting support
- `codeInsight.lineIndentProvider`: Smart line indentation

## Development Guidelines

### Making Changes
1. **Minimize modifications**: Change as few lines as possible
2. **Test compatibility**: Always test with existing YAML functionality
3. **Error handling**: Add robust bounds checking and null safety
4. **Pattern specificity**: Use specific patterns for completion and formatting
5. **Theme adaptation**: Ensure colors work in both light and dark themes

### Build and Test
```bash
./gradlew test          # Run all tests
./gradlew buildPlugin   # Build plugin distribution
./gradlew runIde        # Test in development IDE
```

### Debugging Tips
- Use IntelliJ's PSI Viewer to understand file structure
- Test completion patterns in development IDE
- Verify lexer behavior with various input combinations
- Check formatting rules with real Bjoern BDD files

## Dependencies and Compatibility

- **Core dependency**: IntelliJ YAML plugin (bundled)
- **Java requirement**: 17+ (with toolchain configuration)
- **IDE compatibility**: 2023.3+ (tested with 2023.3.8)
- **Platform**: Cross-platform (inherits from IntelliJ platform)

## Future Enhancements

Potential areas for improvement:
- Advanced error detection and validation
- Better integration with BDD testing frameworks
- Enhanced variable detection and validation
- Code folding for large scenario files
- Integration with external Bjoern tools