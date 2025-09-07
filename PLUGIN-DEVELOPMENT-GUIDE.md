# IntelliJ Plugin Development Guide: Understanding the Bjoern Plugin

This guide explains how the Bjoern IntelliJ plugin works and provides a foundation for understanding IntelliJ plugin development, even if you have no prior experience.

## Table of Contents

1. [What is an IntelliJ Plugin?](#what-is-an-intellij-plugin)
2. [Plugin Architecture Overview](#plugin-architecture-overview)
3. [Project Structure](#project-structure)
4. [Key Components Explained](#key-components-explained)
5. [How the Plugin Works (Step by Step)](#how-the-plugin-works-step-by-step)
6. [Development Environment Setup](#development-environment-setup)
7. [Common Development Tasks](#common-development-tasks)
8. [Build System and Testing](#build-system-and-testing)
9. [Publishing and Distribution](#publishing-and-distribution)
10. [Troubleshooting Common Issues](#troubleshooting-common-issues)

## What is an IntelliJ Plugin?

An IntelliJ plugin is a software extension that adds new features or functionality to JetBrains IDEs (IntelliJ IDEA, PyCharm, WebStorm, etc.). Think of it like a browser extension, but for your code editor.

### What Our Plugin Does

The Bjoern plugin adds support for `.zgr` files (Bjoern BDD specification files) to IntelliJ IDEA by providing:

- **File Recognition**: IntelliJ recognizes `.zgr` files and shows them with a teal gem icon
- **Syntax Highlighting**: Keywords like `Given:`, `When:`, `Then:` are colored differently
- **Smart Completion**: Type suggestions as you write BDD scenarios
- **Auto-Formatting**: Proper indentation when you press Enter or format the document
- **Error Detection**: Invalid keywords are highlighted as errors

## Plugin Architecture Overview

IntelliJ plugins work by extending the IDE's core functionality through well-defined extension points. Here's how our plugin fits into the IDE:

```
IntelliJ IDEA Core
├── File System
│   └── Our Plugin: Recognizes .zgr files
├── Editor
│   ├── Our Plugin: Syntax highlighting
│   ├── Our Plugin: Auto-completion
│   └── Our Plugin: Formatting
└── Project Model
    └── Our Plugin: File type registration
```

### Key Concepts

1. **Extension Points**: Predefined places in IntelliJ where plugins can add functionality
2. **PSI (Program Structure Interface)**: IntelliJ's way of understanding code structure
3. **Lexer**: Breaks text into tokens (words, keywords, symbols)
4. **Parser**: Understands the structure and meaning of tokens
5. **Language**: A registered language type that IntelliJ can work with

## Project Structure

```
bjoern-intellij-plugin/
├── build.gradle              # Build configuration
├── src/main/
│   ├── java/de/mehtrick/bjoern/   # Java source code
│   │   ├── BjoernLanguage.java         # Language definition
│   │   ├── BjoernFileType.java         # File type (.zgr recognition)
│   │   ├── BjoernSyntaxHighlighter.java # Coloring rules
│   │   ├── BjoernCompletionContributor.java # Auto-completion
│   │   ├── BjoernFormattingModelBuilder.java # Document formatting
│   │   ├── BjoernLineIndentProvider.java # Smart indentation
│   │   └── ... (other components)
│   └── resources/
│       ├── META-INF/plugin.xml    # Plugin configuration
│       └── icons/             # Plugin icons
├── examples/                  # Sample .zgr files for testing
└── .github/workflows/         # CI/CD automation
```

## Key Components Explained

### 1. Plugin Configuration (`plugin.xml`)

This is the "blueprint" of your plugin. It tells IntelliJ:

```xml
<idea-plugin>
    <id>de.mehtrick.bjoern-intellij-plugin</id>
    <name>Bjoern Language Support</name>
    
    <!-- What other plugins we depend on -->
    <depends>com.intellij.modules.platform</depends>
    <depends>org.jetbrains.plugins.yaml</depends>
    
    <!-- What features we add to IntelliJ -->
    <extensions defaultExtensionNs="com.intellij">
        <fileType name="Bjoern File" implementationClass="...BjoernFileType"/>
        <lang.syntaxHighlighterFactory language="Bjoern" implementationClass="..."/>
        <!-- ... more extensions -->
    </extensions>
</idea-plugin>
```

**Think of it as**: A restaurant menu that lists all the dishes (features) your plugin offers.

### 2. Language Definition (`BjoernLanguage.java`)

```java
public class BjoernLanguage extends Language {
    public static final BjoernLanguage INSTANCE = new BjoernLanguage();
    
    private BjoernLanguage() {
        super("Bjoern");  // This is the language ID
    }
}
```

**What it does**: Registers "Bjoern" as a programming language that IntelliJ can understand.

**Think of it as**: Creating a new entry in IntelliJ's dictionary of programming languages.

### 3. File Type (`BjoernFileType.java`)

```java
public class BjoernFileType extends LanguageFileType {
    public static final BjoernFileType INSTANCE = new BjoernFileType();
    
    @Override
    public String getDefaultExtension() {
        return "zgr";  // Files ending in .zgr
    }
    
    @Override
    public Icon getIcon() {
        return BjoernIcons.FILE;  // The teal gem icon
    }
}
```

**What it does**: Tells IntelliJ that `.zgr` files are Bjoern files and should show a teal gem icon.

**Think of it as**: Teaching IntelliJ that `.zgr` files are like `.java` files, but for the Bjoern language.

### 4. Syntax Highlighting (`BjoernSyntaxHighlighter.java`)

This component colors different parts of your code:

```java
public class BjoernSyntaxHighlighter extends SyntaxHighlighterBase {
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType == BjoernTokenTypes.KEYWORD) {
            return KEYWORD_KEYS;  // Color keywords blue
        } else if (tokenType == BjoernTokenTypes.VARIABLE) {
            return VARIABLE_KEYS; // Color variables green
        }
        return EMPTY_KEYS;  // Default color for everything else
    }
}
```

**What it does**: Decides what color each word should be (like "Given:" being blue).

**Think of it as**: A painter that knows which brush to use for different parts of a picture.

### 5. Auto-completion (`BjoernCompletionContributor.java`)

```java
public class BjoernCompletionContributor extends CompletionContributor {
    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        // When user types, suggest relevant completions
        result.addElement(LookupElementBuilder.create("Given:"));
        result.addElement(LookupElementBuilder.create("When:"));
        // ... more suggestions
    }
}
```

**What it does**: Provides suggestions when you start typing (like autocomplete in your phone's keyboard).

**Think of it as**: A helpful assistant that finishes your sentences.

### 6. Formatting (`BjoernFormattingModelBuilder.java` & `BjoernLineIndentProvider.java`)

- **FormattingModelBuilder**: Handles document formatting (Ctrl+Alt+L)
- **LineIndentProvider**: Handles smart indentation when you press Enter

**What they do**: Make sure your code is properly indented and organized.

**Think of it as**: An automatic organizer that keeps your desk tidy.

## How the Plugin Works (Step by Step)

Let's trace what happens when you work with a `.zgr` file:

### 1. Opening a File

1. User opens `example.zgr`
2. IntelliJ checks file extension → finds `.zgr` 
3. Looks up file type → finds `BjoernFileType`
4. Shows file with teal gem icon
5. Associates file with Bjoern language

### 2. Syntax Highlighting

1. IntelliJ reads file content
2. Lexer breaks text into tokens (words)
3. `BjoernSyntaxHighlighter` assigns colors to tokens
4. Keywords like "Given:" become blue
5. Variables in quotes become green

### 3. Auto-completion

1. User starts typing in the editor
2. IntelliJ calls `BjoernCompletionContributor`
3. Plugin analyzes context (are we under a "Given:" section?)
4. Suggests relevant completions
5. User sees dropdown with suggestions

### 4. Formatting

1. User presses Enter or Ctrl+Alt+L
2. IntelliJ calls formatting providers
3. Plugin calculates correct indentation
4. Text is automatically indented according to BDD rules

## Development Environment Setup

### Prerequisites

- **Java 17 or later**: The programming language used to build the plugin
- **IntelliJ IDEA**: For development and testing
- **Git**: For version control

### Setup Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Mehtrick/bjoern-intellij-plugin.git
   cd bjoern-intellij-plugin
   ```

2. **Open in IntelliJ IDEA**
   - File → Open → Select the project folder
   - IntelliJ will automatically detect it's a Gradle project

3. **Build the Plugin**
   ```bash
   ./gradlew build
   ```

4. **Run/Test the Plugin**
   ```bash
   ./gradlew runIde
   ```
   This opens a new IntelliJ instance with your plugin installed.

### What Each Build File Does

- **`build.gradle`**: Main build configuration
  - Defines Java version (17)
  - Specifies IntelliJ version to build against (2023.3.8)
  - Lists dependencies (YAML plugin)
  - Configures plugin publishing

- **`settings.gradle`**: Project settings
- **`gradlew`**: Gradle wrapper script (ensures consistent Gradle version)

## Common Development Tasks

### Adding a New Keyword

1. **Add token type** in `BjoernTokenTypes.java`:
   ```java
   IElementType NEW_KEYWORD = new IElementType("NEW_KEYWORD", BjoernLanguage.INSTANCE);
   ```

2. **Update lexer** to recognize the keyword
3. **Add highlighting** in `BjoernSyntaxHighlighter.java`:
   ```java
   if (tokenType == BjoernTokenTypes.NEW_KEYWORD) {
       return KEYWORD_KEYS;
   }
   ```

4. **Add completion** in `BjoernCompletionContributor.java`:
   ```java
   result.addElement(LookupElementBuilder.create("NewKeyword:"));
   ```

### Changing Colors

Modify the TextAttributesKey definitions in `BjoernSyntaxHighlighter.java`:

```java
public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
    "BJOERN_KEYWORD", 
    DefaultLanguageHighlighterColors.KEYWORD  // Change this to different color
);
```

### Modifying Indentation Rules

Update `BjoernLineIndentProvider.java` or `BjoernFormattingModelBuilder.java`:

```java
if (isUnderBackground(document, offset)) {
    return Indent.getSpaceIndent(2);  // Change number of spaces
}
```

### Adding New File Extensions

Modify `BjoernFileType.java`:

```java
@Override
public String getDefaultExtension() {
    return "zgr;bdd;spec";  // Support multiple extensions
}
```

## Build System and Testing

### Gradle Tasks

- **`./gradlew build`**: Compile and build the plugin
- **`./gradlew test`**: Run automated tests
- **`./gradlew runIde`**: Launch IntelliJ with the plugin
- **`./gradlew buildPlugin`**: Create distributable plugin JAR
- **`./gradlew publishPlugin`**: Publish to JetBrains Plugin Repository

### Testing the Plugin

1. **Manual Testing**:
   - Run `./gradlew runIde`
   - Create test `.zgr` files
   - Verify syntax highlighting, completion, formatting

2. **Automated Testing**:
   - Tests are in `src/test/`
   - Run with `./gradlew test`

3. **Example Files**:
   - Use files in `examples/` directory
   - Create new examples for different scenarios

### CI/CD Pipeline

The project uses GitHub Actions for automation:

- **`ci.yml`**: Runs tests on every push/PR
- **`publish.yml`**: Publishes plugin when creating a GitHub release

## Publishing and Distribution

### Local Installation

1. Build the plugin: `./gradlew buildPlugin`
2. In IntelliJ: File → Settings → Plugins → Install Plugin from Disk
3. Select the JAR file from `build/distributions/`

### JetBrains Plugin Repository

1. **Prepare for publishing**:
   - Ensure `plugin.xml` has proper description
   - Update version in `build.gradle`
   - Test thoroughly

2. **Publish via GitHub Release**:
   - Create a new release on GitHub
   - Tag it with version (e.g., `v1.0.1`)
   - GitHub Actions automatically publishes to JetBrains

3. **Required Secret**:
   - `PUBLISH_TOKEN`: Get from https://plugins.jetbrains.com/author/me/tokens
   - Add to GitHub repository secrets

## Troubleshooting Common Issues

### "Plugin failed to load"

**Cause**: Usually plugin.xml configuration issues
**Solution**: 
1. Check `plugin.xml` syntax
2. Verify all class names are correct
3. Ensure dependencies are properly declared

### "Syntax highlighting not working"

**Cause**: Lexer or highlighter issues
**Solution**:
1. Check if `BjoernSyntaxHighlighterFactory` is registered in `plugin.xml`
2. Verify token types are properly defined
3. Test lexer with simple examples

### "Auto-completion not appearing"

**Cause**: Completion contributor not triggering
**Solution**:
1. Verify `BjoernCompletionContributor` is registered for correct language
2. Check completion patterns match your use case
3. Add logging to debug when contributor is called

### "Build fails with Java version error"

**Cause**: Java version mismatch
**Solution**:
1. Ensure Java 17+ is installed
2. Check `build.gradle` has correct Java version
3. Verify JAVA_HOME environment variable

### "Plugin not compatible with IntelliJ version"

**Cause**: Version range too restrictive
**Solution**:
1. Update `sinceBuild` and `untilBuild` in `plugin.xml`
2. Test with target IntelliJ versions
3. Consider using broader version range

## Key Learning Resources

- **IntelliJ Platform SDK**: https://plugins.jetbrains.com/docs/intellij/
- **Sample Plugins**: https://github.com/JetBrains/intellij-sdk-code-samples
- **Plugin Development Forum**: https://intellij-support.jetbrains.com/hc/en-us/community/topics

## Understanding the Code: Class by Class

### Core Classes

| Class | Purpose | Think of it as |
|-------|---------|----------------|
| `BjoernLanguage` | Registers the language | Birth certificate for the language |
| `BjoernFileType` | File recognition | File extension handler |
| `BjoernSyntaxHighlighter` | Colors text | Painter with color rules |
| `BjoernCompletionContributor` | Auto-complete | Helpful typing assistant |
| `BjoernFormattingModelBuilder` | Document formatting | Professional document formatter |
| `BjoernLineIndentProvider` | Smart Enter key | Smart tab organizer |

### Supporting Classes

| Class | Purpose |
|-------|---------|
| `BjoernParserDefinition` | Tells IntelliJ how to parse files |
| `BjoernFile` | Represents a .zgr file in memory |
| `BjoernTokenTypes` | Defines different types of text elements |
| `BjoernLayeredLexer` | Breaks text into meaningful pieces |
| `BjoernKeywordValidationLexer` | Validates BDD keywords |
| `BjoernDoubleQuotedStringLexer` | Handles quoted strings |

This guide provides a foundation for understanding IntelliJ plugin development through the lens of a real, working plugin. Each concept builds upon the previous ones, creating a comprehensive understanding of how modern IDE extensions work.