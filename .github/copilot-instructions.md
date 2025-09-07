# Bjoern IntelliJ Plugin

**ALWAYS follow these instructions first and only fallback to additional search and context gathering if the information provided here is incomplete or found to be in error.**

Bjoern IntelliJ Plugin is an IntelliJ IDEA plugin that provides language support for Bjoern BDD specification files (.zgr files). The plugin includes syntax highlighting, auto-completion, smart indentation, file type recognition, and YAML-based structure validation.

## Critical Build Limitations

**NETWORKING CONSTRAINTS**: In many environments, JetBrains repositories (cache-redirector.jetbrains.com, www.jetbrains.com) are not accessible due to networking restrictions. This prevents downloading IntelliJ Platform dependencies and building the plugin.

**BUILD FAILURE EXPECTATION**: 
- `./gradlew build` -- FAILS due to inability to download IntelliJ Platform dependencies  
- `./gradlew test` -- FAILS for the same reason
- `./gradlew runIde` -- FAILS for the same reason

**DO NOT** attempt to build/test in environments with restricted internet access. Instead, focus on:
- Code analysis and understanding
- File structure validation  
- Documentation improvements
- Static code review
- Example file validation

## Working Effectively

### Bootstrap and Environment Setup
```bash
# Check Java version (requires Java 17+)
java -version

# The project requires Java 17+ but build.gradle may specify Java 21
# Adjust build.gradle if needed:
# Change sourceCompatibility/targetCompatibility from "21" to "17"

# Grant execute permission
chmod +x gradlew

# Check Gradle wrapper version
./gradlew --version  # Should show Gradle 8.10.2
```

### When Networking Allows (Full Build Process)
**NEVER CANCEL: Build operations take 60+ minutes when dependencies need downloading**
```bash
# Clean project - takes 10-15 seconds
./gradlew clean

# Build plugin - takes 45-90 minutes on first run (downloads IntelliJ Platform)
# NEVER CANCEL - Set timeout to 120+ minutes
./gradlew build

# Run tests - takes 10-20 minutes  
# NEVER CANCEL - Set timeout to 30+ minutes
./gradlew test

# Launch test IntelliJ instance with plugin
# NEVER CANCEL - Can take 30+ minutes to start
./gradlew runIde

# Build distributable plugin JAR
./gradlew buildPlugin
```

### Alternative Validation (When Build Fails)
When networking prevents full builds, validate the plugin by:

1. **File Structure Analysis**:
   ```bash
   # Verify plugin structure
   find src -name "*.java" | wc -l     # Should show 14 files
   find src/test -name "*.java" | wc -l  # Should show 4 test files
   find examples -name "*.zgr" | wc -l   # Should show 8 example files
   ```

2. **Code Analysis**:
   ```bash
   # Review key plugin components
   ls src/main/java/de/mehtrick/bjoern/
   # Should include: BjoernLanguage.java, BjoernFileType.java, 
   # BjoernSyntaxHighlighter*.java, BjoernCompletion*.java, etc.
   ```

3. **Example File Validation**:
   ```bash
   # Test with example files
   cat examples/userLogin.zgr      # User authentication example
   cat examples/kassenAutomat.zgr  # German vending machine example  
   cat examples/completionDemo.zgr # Auto-completion demo
   ```

## Key Build Files and Configuration

### Essential Files
- **build.gradle**: Main build configuration (Gradle + IntelliJ plugin)
- **plugin.xml**: Plugin configuration and extension points
- **gradle/wrapper/**: Gradle wrapper (version 8.10.2)
- **.github/workflows/**: CI/CD pipelines (ci.yml, publish.yml)

### Build Configuration Summary
```gradle
// From build.gradle
plugins {
    id 'java'
    id 'org.jetbrains.intellij' version '1.17.4'
}

intellij {
    version = '2023.3.8'
    type = 'IC' // IntelliJ IDEA Community Edition  
    plugins = ['yaml']
}
```

## Project Structure

### Source Code (14 Java files)
```
src/main/java/de/mehtrick/bjoern/
├── BjoernLanguage.java              # Language definition
├── BjoernFileType.java              # .zgr file type registration  
├── BjoernSyntaxHighlighter*.java    # Syntax highlighting rules
├── BjoernCompletion*.java           # Auto-completion logic
├── BjoernFormatting*.java           # Indentation and formatting
├── BjoernParser*.java               # YAML-based parsing
├── Bjoern*Lexer.java               # Tokenization (keyword validation, quoted strings)
└── BjoernTokenTypes.java           # Token definitions
```

### Tests (4 files)
```
src/test/java/de/mehtrick/bjoern/
├── BjoernTokenizationTest.java     # Lexer and tokenization tests
├── BjoernFileTypeTest.java         # File type recognition tests  
├── BjoernCompletionTest.java       # Auto-completion tests
└── BjoernSyntaxHighlighterTest.java # Syntax highlighting tests
```

### Example Files (8 .zgr files)
```
examples/
├── userLogin.zgr           # User authentication scenarios
├── kassenAutomat.zgr      # German vending machine test  
├── completionDemo.zgr     # Auto-completion examples
├── variableHighlighting.zgr # Variable highlighting demo
├── enhancedFeatures.zgr   # Advanced feature showcase
├── formattingTest.zgr     # Indentation and formatting tests
├── properFormatting.zgr   # Proper structure examples
└── testFormatting.zgr     # Additional formatting tests
```

## Manual Validation Scenarios

**ALWAYS manually validate plugin functionality using these scenarios:**

### 1. File Recognition Test
Open any .zgr file in examples/ directory and verify:
- File shows teal gem icon (if IDE available)
- File is recognized as "Bjoern File" type
- Content displays properly

### 2. Syntax Highlighting Validation  
Check examples/userLogin.zgr and examples/variableHighlighting.zgr:
- Keywords should be highlighted: `Feature:`, `Background:`, `Given:`, `When:`, `Then:`, `Scenario:`, `Scenarios:`
- Variables in quotes should be highlighted: `"john.doe"`, `"securePassword123"`, `"testUser"`
- Invalid keywords should be marked as errors (see examples/enhancedFeatures.zgr)

### 3. Smart Indentation Test
Compare examples/properFormatting.zgr vs examples/testFormatting.zgr:
- **Proper indentation** (properFormatting.zgr):
  - Feature, Background, Scenarios: No indentation (column 0)
  - Given/When/Then under Background: 2 spaces
  - Scenario items under Scenarios: 2 spaces  
  - Given/When/Then under Scenario: 4 spaces
  - List items: +2 additional spaces from parent
- **Improper indentation** (testFormatting.zgr): Shows incorrect spacing that should be auto-corrected

### 4. Auto-completion Validation
In examples/completionDemo.zgr:
- Typing under Given: should suggest existing patterns like `- there are "" bottles of wine`
- Variables should be replaced with empty quotes `""`
- BDD keywords should auto-complete

### 5. Variable Highlighting Test
Check examples/variableHighlighting.zgr:
- All quoted strings should be highlighted: `"john.doe"`, `"securePassword123"`, `"3600"`, `"3540"`
- Mixed content lines should highlight only the quoted parts
- File paths and non-quoted text should not be highlighted

### 6. Multi-language Support Test
Check examples/kassenAutomat.zgr:
- German text should be properly handled: `"KassenAutomaten"`, German scenario names
- Non-English BDD scenarios should work correctly
- Unicode characters should be supported

## CI/CD Pipeline

### GitHub Actions Workflows
- **ci.yml**: Runs on push/PR to main/develop branches
  - Uses Java 21 with Temurin distribution
  - Runs `./gradlew test` and `./gradlew buildPlugin`
  - Build takes 45+ minutes, tests take 15+ minutes
  - **NEVER CANCEL** - Builds require long timeouts

- **publish.yml**: Publishes plugin on GitHub releases
  - Extracts version from git tag
  - Runs plugin verification steps
  - Publishes to JetBrains Plugin Repository
  - Requires `PUBLISH_TOKEN` secret

### Common CI Tasks
```bash
# Verify plugin project configuration
./gradlew verifyPluginProjectConfiguration

# Verify plugin structure  
./gradlew verifyPluginStructure

# Run plugin verifier (compatibility checks)
./gradlew verifyPlugin
```

## Development Workflow

### Code Changes
When modifying the plugin:

1. **Syntax Highlighting Changes**: Update `BjoernSyntaxHighlighter*.java`
   - Uses unique TextAttributesKey prefixes (`BJOERN_*`) to avoid conflicts with YAML plugin
   - Color scheme: Keywords (blue/cyan), Variables in quotes (green/yellow), Normal text (theme-adaptive)

2. **New Keywords**: Modify `BjoernTokenTypes.java` and lexer classes
   - `BjoernKeywordValidationLexer.java` - validates BDD keywords
   - `BjoernDoubleQuotedStringLexer.java` - handles quoted strings
   - `BjoernLayeredLexer.java` - combines multiple lexers

3. **Auto-completion**: Update `BjoernCompletion*.java`  
   - Registered for both "Bjoern" and "yaml" languages for compatibility
   - Uses dynamic pattern extraction from existing file content
   - Replaces variables with empty quotes for easy editing

4. **File Type Changes**: Modify `BjoernFileType.java`
   - File extends `YAMLFileImpl` (not `PsiFileBase`) for YAML breadcrumbs compatibility

5. **Indentation Rules**: Update `BjoernFormatting*.java` and `BjoernLineIndentProvider.java`
   - Smart line indentation when pressing Enter
   - Document formatting (Ctrl+Alt+L) with BDD-specific rules

### Architecture Details
- **BjoernLanguage**: Registered as "Bjoern" with proper MIME type associations
- **BjoernFile**: Extends `YAMLFileImpl` to ensure compatibility with IntelliJ's YAML breadcrumbs provider  
- **Lexer Architecture**: Uses layered approach with specialized lexers for keywords and quoted strings
- **Parser**: `BjoernParserDefinition` provides YAML-based parsing with BDD enhancements

### Common Issues to Avoid
1. **ClassCastException with Breadcrumbs**: Always extend `YAMLFileImpl`, not `PsiFileBase`
2. **TextAttributesKey Conflicts**: Use unique prefixes like `BJOERN_*` instead of `YAML_*`
3. **Registry Access Errors**: Handle unmatched quotes gracefully in lexers
4. **StringIndexOutOfBoundsException**: Add proper bounds checking in completion logic

### Testing Strategy
```bash
# Unit tests (when build works)
./gradlew test

# Manual testing with examples
# 1. Check all .zgr files in examples/ directory
# 2. Verify syntax highlighting works correctly
# 3. Test auto-completion functionality  
# 4. Validate indentation behavior
# 5. Ensure file type recognition
```

### Plugin Installation (Local)
```bash
# Build distributable JAR
./gradlew buildPlugin

# JAR location: build/distributions/bjoern-intellij-plugin-*.zip
# Install in IntelliJ: File → Settings → Plugins → Install Plugin from Disk
```

## Bjoern Language Syntax

### BDD Keywords (Must be highlighted)
- `Feature:` - Test feature definition
- `Background:` - Shared setup steps  
- `Given:` - Preconditions
- `When:` - Actions/events
- `Then:` - Expected outcomes
- `Scenario:` - Individual test case
- `Scenarios:` - Multiple test cases

### Variable Syntax
- Variables in double quotes: `"john.doe"`, `"123"`, `"securePassword123"`
- Should be highlighted differently from regular text
- Auto-completion should replace with empty quotes: `""`

### Structure Rules
```yaml
Feature: Test name
Background:
  Given:
    - Setup step
Scenarios:
  - Scenario: Test case
    Given:
      - Precondition with "variable"
    When:
      - Action with "parameter"  
    Then:
      - Expected result
```

## Troubleshooting

### Build Issues
- **"No IntelliJ Platform dependency found"**: Networking restrictions prevent dependency download
- **"cache-redirector.jetbrains.com"**: JetBrains repositories not accessible
- **Java version mismatch**: Adjust sourceCompatibility in build.gradle

### Development Issues  
- **Changes not visible**: Restart test IntelliJ instance (`./gradlew runIde`)
- **Syntax highlighting broken**: Check `BjoernSyntaxHighlighter*.java` registration
- **Auto-completion not working**: Verify `BjoernCompletion*.java` is properly configured

### Network-Restricted Environments
- Focus on static code analysis
- Use example files for validation
- Review documentation and structure
- Test file processing logic without full builds

## Important Reminders

- **NEVER CANCEL** long-running Gradle commands - they may take 60+ minutes
- **ALWAYS** set appropriate timeouts (120+ minutes for builds, 30+ minutes for tests)
- **NETWORKING FAILURES ARE EXPECTED** in restricted environments
- **USE EXAMPLE FILES** for manual validation when builds fail
- **VALIDATE ALL 8 EXAMPLE .ZGR FILES** when testing syntax highlighting
- **JAVA 17+ REQUIRED** - adjust build.gradle if using different Java version

## Repository Statistics

- **Language**: Java
- **Build System**: Gradle 8.10.2
- **Source Files**: 14 Java files, 4 test files
- **Example Files**: 8 .zgr demonstration files
- **Plugin Type**: IntelliJ IDEA language support plugin
- **Dependencies**: IntelliJ Platform 2023.3.8, YAML plugin
- **Target IDEs**: IntelliJ IDEA 2023.3+ (Community/Ultimate)