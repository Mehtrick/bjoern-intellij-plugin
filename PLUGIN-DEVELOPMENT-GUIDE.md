# Bjoern IntelliJ Plugin Development Guide

This guide describes the checked-in implementation and development workflow. The [README](README.md#requirements) lists the configured SDK and tool versions. The dated [IntelliJ compatibility investigation](docs/intellij-compatibility.md) covers newer platform releases and verification limits.

## Relationship to the Bjoern framework

The IntelliJ plugin supports editing YAML-based `.zgr` specifications. The separate [Bjoern Gradle framework](https://github.com/Mehtrick/bjoern) validates and reads those specifications, generates abstract JUnit classes and step interfaces, and produces documentation. Developers implement the generated steps in their own test classes.

There is no direct framework dependency in this IDE plugin. The specification format is their shared contract. A language change therefore needs corresponding framework parsing/generation support and IDE editing/validation support.

## Architecture

The plugin reuses IntelliJ's bundled YAML implementation:

```text
.zgr file
  → BjoernFileType / BjoernLanguage (derived from YAML)
  → BjoernParserDefinition (delegates to YAMLParserDefinition)
  → BjoernFile (extends YAMLFileImpl)
  → YAML PSI consumed by completion, inspections, annotator and breadcrumbs

Editor highlighting
  → BjoernSyntaxHighlighter
  → BjoernValidatingLexer (layered over YAMLParserDefinition.createLexer(Project))
  → keyword validation and double-quoted parameter tokens
```

PSI is IntelliJ's structured representation of a file. The parser continues to use YAML tokens; the custom highlighting lexer does not replace the YAML parser's lexer.

`src/main/resources/META-INF/plugin.xml` registers the extension points and declares dependencies on `com.intellij.modules.platform` and `org.jetbrains.plugins.yaml`.

## Project layout

```text
bjoern-intellij-plugin/
├── build.gradle.kts                 # SDK, toolchains, verification and publishing
├── settings.gradle.kts
├── gradle.properties                # Build/configuration cache settings
├── gradle/wrapper/                  # Gradle wrapper distribution configuration
├── src/main/java/de/mehtrick/bjoern/ # All production classes, including lexers
├── src/main/resources/
│   ├── META-INF/plugin.xml
│   ├── META-INF/pluginIcon.svg
│   ├── icons/icon.svg
│   ├── fileTemplates/internal/Bjoern Spec.zgr.ft
│   └── inspectionDescriptions/
├── src/test/java/de/mehtrick/bjoern/
├── examples/
├── docs/intellij-compatibility.md
└── .github/workflows/
```

## Main components

| Component | Responsibility |
| --- | --- |
| `BjoernLanguage`, `BjoernFileType` | YAML-derived language identity, `.zgr` association, and file icon |
| `BjoernParserDefinition`, `BjoernFile` | Keep a Bjoern file root, delegate parsing to YAML’s file element type, and reuse YAML PSI |
| `BjoernSyntaxHighlighter`, `BjoernSyntaxHighlighterFactory` | Assign `BJOERN_*` color attributes to tokens |
| `BjoernValidatingLexer` | Layer keyword and parameter highlighting over YAML’s lexer adapter |
| `BjoernKeywordValidationLexer` | Classify keys using `VALID_BDD_KEYWORDS` |
| `BjoernDoubleQuotedStringLexer` | Tokenize quoted parameters inside text |
| `BjoernCompletionContributor` | Keyword and boolean suggestions; reuse statements from project `.zgr` files |
| `BjoernTabHandler` | Navigate quoted parameters, delegating other cases to the original Tab handler |
| `BjoernFormattingModelBuilder` | Wrap the YAML formatter and apply Björn indentation rules |
| `BjoernLineIndentProvider` | Calculate indentation when entering a new line |
| `BjoernStructureInspection` | Check required fields, duplicate scenario names, empty blocks, and `Deprecated:` |
| `BjoernReferenceInspection`, `BjoernAnnotator` | Validate Reference values and visually highlight valid Markdown links |
| `BjoernBreadcrumbsProvider` | Display structural sections and steps, including deprecated scenario labels |
| `BjoernCreateFileAction` | Offer **New → Bjoern Spec File** using the internal template |
| `BjoernColorSettingsPage` | Expose the plugin's editor color attributes |

The syntax highlighter instantiates `BjoernValidatingLexer`. It obtains the base lexer through `new YAMLParserDefinition().createLexer(null)`, avoiding the implementation class removed in 2026.3. The Bjoern file element delegates `parseContents` to YAML’s own file element, retaining Bjoern language identity while supporting both the classic and new syntax parsing pipelines.

Completion is registered for both `Bjoern` and `yaml`, guarded by the `.zgr` filename. It determines the Given/When/Then context from PSI with a text fallback, scans the current file and other project files, and replaces quoted values with `""`. Statement extraction traverses YAML PSI sequences; a regular expression replaces quoted parameters, including escaped quotes.

## Development setup

Install JDK 21 for the 2026.1 baseline and JDK 25 to build/test against 2026.2 or 2026.3. Import the repository as a Gradle project. The wrapper can run on either installed JDK; the Java toolchain follows the selected platform. Gradle downloads the configured IntelliJ SDK and dependencies on the first build.

```bash
./gradlew build
./gradlew runIde
```

The wrapper uses Gradle 9.9.1 with a pinned SHA-256 checksum. The build uses IntelliJ Platform Gradle Plugin 2.20.1 and unified IDEA 2026.1.5.1 by default. Production and test sources are Java; no Kotlin JVM plugin is needed. Compilation emits Java 21 bytecode for the oldest supported platform.

Select a supported SDK with a Gradle property:

```bash
./gradlew test -PplatformVersion=2026.2.5
./gradlew test -PplatformVersion=2026.3
./gradlew runIde -PplatformVersion=2026.3
```

The default plugin version is 1.0.4. A release build can override it with `-PpluginVersion=1.0.4`. Versions and the explicit verification matrix are configured in `build.gradle.kts`.

`runIde` starts a separate development IDE under `.intellijPlatform/sandbox/`. `./build.sh` delegates to the wrapper and runs tests, plugin packaging, project-configuration verification, and structure verification. Additional arguments are forwarded to Gradle.

## Editing language support

### Adding a keyword

1. Confirm the field's syntax, scope, and semantics in the framework's parser/model/validators.
2. Add its name to `VALID_BDD_KEYWORDS` in `BjoernKeywordValidationLexer` and its completion text to `BDD_KEYWORDS` in `BjoernCompletionContributor`.
3. Update applicable inspections, formatting/indentation, breadcrumbs, color-settings examples, and the file template. Ordinary keywords reuse `VALID_KEYWORD`; a new token type is only needed for a distinct token category.
4. Update README examples and verify behavior on parsed `.zgr` files.

### Colors

Edit `TextAttributesKey` definitions in `BjoernSyntaxHighlighter`. Keep the `BJOERN_*` prefix and theme-aware defaults. Verify both light and dark schemes. Reference link styling is supplied by `BjoernAnnotator`; styling alone does not implement a custom click-navigation handler.

### Indentation

Keep `BjoernFormattingModelBuilder` and `BjoernLineIndentProvider` aligned with the framework's validation rules:

```yaml
Feature: Example
Version: "1.0.0"
Reference: "[TICKET-123](https://example.com/TICKET-123)"
Changelog: "Initial specification"
Background:
  Given:
    - A shared precondition
Scenarios:
  - Scenario: Example scenario
    Deprecated: false
    Given:
      - A user named "Ada"
    When:
      - The user logs in
    Then:
      - The greeting is "Hello Ada"
```

### File extensions

File associations are registered through the `fileType` extension's `extensions` attribute in `plugin.xml`. `BjoernFileType.getDefaultExtension()` returns the single default extension `zgr`. Adding another association also requires reviewing `.zgr` filename guards in completion, inspections, and the annotator, plus the framework's accepted input extensions.

## Testing and compatibility checks

| Command | Purpose |
| --- | --- |
| `./gradlew test` | Invoke Gradle's test task; inspect the executed test count |
| `./gradlew build` | Compile, check, and assemble according to the Gradle task graph |
| `./gradlew buildPlugin` | Create the installable ZIP in `build/distributions/` |
| `./gradlew verifyPluginProjectConfiguration` | Check SDK, JVM, and descriptor configuration |
| `./gradlew verifyPluginStructure` | Validate packaged plugin structure |
| `./gradlew verifyPlugin` | Run binary compatibility checks against the configured IDE set |
| `./gradlew runIde` | Launch the development IDE for editor smoke tests |

Tests extend IntelliJ's `BasePlatformTestCase`, whose JUnit-compatible `test…` methods are discovered by Gradle's explicitly configured `useJUnit()` runner. The build includes JUnit 4 and the Platform test framework, and sets `failOnNoDiscoveredTests = true` so an empty test run cannot silently succeed.

Fixture tests use real `.zgr` PSI/editor behavior for parsing, highlighting, completion across project files, Reference/structure inspections, breadcrumbs, and Tab navigation. Lexer and Reference value tests supplement those checks. Reports are written to `build/reports/tests/test/` and `build/test-results/test/`.

Manual smoke checks should cover file recognition; valid and invalid keywords; matched and unmatched parameter quotes; comments; completion from the current and another project file; placeholder navigation; Enter and document formatting; references; breadcrumbs; file creation; and `Deprecated:` placement/value validation.

The descriptor is patched from `build.gradle.kts`: `sinceBuild = "261"` and `untilBuild = "263.*"`, covering IntelliJ IDEA 2026.1–2026.3. The lowest SDK, 2026.1.5.1, is the release compilation baseline; CI additionally recompiles and runs fixtures on 2026.2.5 and 2026.3. Plugin Verifier checks the baseline artifact against all three explicit targets, using each IDE’s bundled runtime. Binary verification must be supplemented by editor behavior tests.

When investigating a newer IDE, record its exact version/build, plugin version, dependency availability, and errors from **Help → Show Log**. Check both the declared minimum and the newest stable platform. See the [compatibility investigation](docs/intellij-compatibility.md) for distribution and runtime changes.

## CI and publishing

`.github/workflows/ci.yml` runs for pushes and pull requests to `main`. Its matrix uses JDK 21 for 2026.1.5.1 and JDK 25 for 2026.2.5/2026.3. Each entry runs tests, packages the plugin, checks configuration/structure, and uploads test reports and the archive. A separate compatibility job verifies the baseline artifact against all supported generations and uploads its reports. Gradle caching replaces the old transfer of entire build workspaces.

`.github/workflows/publish.yml` runs when a GitHub release is created. It accepts `1.0.4` or `v1.0.4` tags, validates the three-part numeric version, and passes the normalized version through `-PpluginVersion`. It runs tests, packaging and verification before publishing with `PUBLISH_TOKEN`, and uploads reports and archives even if a check fails.

Keep `pluginConfiguration.changeNotes` current before releasing. Publishing is a separate operation from a local build.

For local installation, select the generated ZIP using **Settings → Plugins → Install Plugin from Disk**. Use the complete distribution archive from `build/distributions/`.

## Troubleshooting

| Symptom | Checks |
| --- | --- |
| Plugin fails to load after an IDE update | Inspect the IDE log and exact build, YAML dependency availability, descriptor range, and Plugin Verifier output; a broader range alone cannot fix missing APIs |
| Java/toolchain error | Check the Gradle JVM, configured toolchain, SDK bytecode level, and wrapper compatibility; the checked-in baseline uses JDK 21 |
| Highlighting differs from YAML parsing | Check `BjoernValidatingLexer` and token highlighting separately from `BjoernParserDefinition` |
| Completion missing or incorrect | Check the `.zgr` guard, PSI/text context, project indexing, YAML sequence extraction, and both language registrations |
| Formatting or Enter indentation differs | Compare both indentation implementations using the same specification |
| No tests are discovered | The test task now fails; check fixture inheritance, test method names and filters, then inspect the reports |

## Primary references

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [IntelliJ Platform Gradle Plugin tasks](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-tasks.html)
- [Build number ranges and Java versions](https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html)
- [Verifying plugin compatibility](https://plugins.jetbrains.com/docs/intellij/verifying-plugin-compatibility.html)
- [Incompatible API changes](https://plugins.jetbrains.com/docs/intellij/api-changes-list.html)
