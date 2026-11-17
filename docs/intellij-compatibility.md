# IntelliJ IDEA compatibility assessment and migration

Checked on **17 November 2026** (Europe/Berlin). This note records the starting incompatibility, the resulting 2026 support policy, and local verification evidence. The IntelliJ plugin is updated in this repository; the separate Bjoern Gradle framework is unchanged.

## Supported releases and tooling

JetBrains’ release feed identifies **IntelliJ IDEA 2026.3**, build **263.7307.200**, released **5 November 2026**, as the latest stable release. The latest maintenance releases for the other supported generations are 2026.1.5.1 (261.27258.136) and 2026.2.5 (262.12573.28). Preview builds are outside this support claim. [Latest stable feed](https://data.services.jetbrains.com/products/releases?code=IIU&latest=true&type=release), [full stable release feed](https://data.services.jetbrains.com/products/releases?code=IIU&latest=false&type=release).

The new plugin targets **2026.1–2026.3**, with `sinceBuild = "261"` and `untilBuild = "263.*"`. Its release build uses the oldest supported generation, **2026.1.5.1**, and emits Java 21 bytecode. CI and Plugin Verifier use explicit targets for all three generations. Compiling against the lowest supported SDK protects backwards compatibility; a metadata range alone does not establish it. [Build ranges and Java versions](https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html).

| Setting | Before | After |
| --- | --- | --- |
| Development SDK | IC 2025.2.2 | Unified IDEA 2026.1.5.1 (`IU`), overridable with `-PplatformVersion` |
| Declared IDE range | 251 onwards, unbounded | 261–263.* |
| Gradle wrapper | 8.14.4 | 9.9.1, with distribution checksum |
| IntelliJ Platform Gradle plugin | 2.11.0 | 2.20.1 |
| Kotlin JVM plugin | 2.2.21, despite Java-only sources | Removed |
| Java bytecode | 21 | 21 |
| SDK-specific toolchain | 21 | 21 for 2026.1; 25 for 2026.2/2026.3 |
| Test discovery | 0 tests | IntelliJ fixtures with JUnit and failure on no discovered tests |
| Verifier targets | Dynamic `recommended()` selection | Explicit 2026.1.5.1, 2026.2.5, 2026.3 |

The Gradle stable feed reports 9.9.1, and the IntelliJ Gradle plugin release is 2.20.1. That plugin requires Gradle 9 or newer. Java 25 toolchains and running Gradle on Java 25 are supported from Gradle 9.1. Removing the unused Kotlin JVM plugin avoids an additional compiler/Gradle compatibility constraint; Kotlin build scripts still work through Gradle’s embedded Kotlin. [Gradle release feed](https://services.gradle.org/versions/current), [IntelliJ Gradle plugin release](https://github.com/JetBrains/intellij-platform-gradle-plugin/releases/tag/2.20.1), [plugin requirements](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html), [Gradle Java matrix](https://docs.gradle.org/current/userguide/compatibility.html#java_runtime), [Kotlin Gradle compatibility](https://kotlinlang.org/docs/gradle-configure-project.html).

## Reproduced failure: removed YAML lexer

The source at release tag **1.0.3**, as well as the starting checkout, constructs `org.jetbrains.yaml.lexer.YAMLFlexLexer` in `BjoernValidatingLexer`; the syntax highlighter instantiates that lexer. The starting checkout also contained an unused `BjoernLayeredLexer` with the same dependency.

A Plugin Verifier run of the starting artifact against **IU-263.7307.200** failed with:

```text
Package 'org.jetbrains.yaml.lexer' is not found
Class org.jetbrains.yaml.lexer.YAMLFlexLexer is referenced in
  BjoernLayeredLexer.<init>()
  BjoernValidatingLexer.<init>()
```

A compilation attempt against the actual 2026.3 SDK independently failed on those imports. Inspection of the downloaded YAML JARs confirmed that the old class is absent. This is a concrete incompatible dependency in the 1.0.3 source, although colleagues’ exact runtime logs were not available to establish whether they encountered additional failures.

**Correction:** the validating lexer obtains the classic IntelliJ `Lexer` through `new YAMLParserDefinition().createLexer(null)`. This method exists in both supported parsing generations and lets YAML adapt its new syntax lexer. The unused duplicate lexer was removed. Keyword, parameter and comment highlighting are exercised through the real lexer in tests.

## Parser integration and editor regressions

The 2026.3 YAML implementation moved parsing to a syntax-aware file element. Directly calling its legacy `createParser(Project)` throws. Simply inheriting YAML’s file element is also insufficient: fixture tests reproduced `Language: yaml != Language: Bjoern` because a Bjoern PSI file must retain its own language root.

**Correction:** `BjoernParserDefinition` retains a Bjoern `IFileElementType` and delegates `parseContents` to YAML’s own file element. YAML chooses the classic or syntax parsing pipeline. Redundant lexer/parser/element overrides were removed. `BjoernFile` continues to extend `YAMLFileImpl`; parsed scenario mappings and editor highlighting are verified on actual `.zgr` files.

Activating the tests also reproduced and corrected these issues:

- A valid scenario-level `Deprecated:` declaration was incorrectly reported as outside a scenario. Its PSI parent traversal now follows the scenario mapping directly to its sequence item.
- Keyword completion disappeared after a prefix such as `Sc` had been typed. Prefix detection now permits a partially typed keyword.
- A statement at the end of another project file was omitted without a trailing newline. Completion now traverses YAML sequences instead of matching sections with a newline-dependent regex; quoted values become placeholders.
- An old breadcrumb test expected the display name `YAML` as the language ID. The actual ID is `yaml`; the test now checks the language instance and a real deprecated scenario label.

Copied-regex tests, private-field reflection, and completion constructor checks were replaced with editor-fixture behavior tests. Tab navigation is tested through the registered editor action.

## Other platform changes

Since 2025.3, IDEA uses a unified **IU** distribution. Community artifacts for newer versions cannot be resolved through `IC`/`intellijIdeaCommunity()`. The build now uses `intellijIdea(...)`. The descriptor does not require `com.intellij.modules.ultimate`; the plugin itself has no deliberate paid-feature dependency. [JetBrains migration guidance](https://blog.jetbrains.com/platform/2025/11/intellij-platform-2025-3-what-plugin-developers-should-know/).

The SDK table identifies **Java 25 for 2026.2/2026.3**, while 2026.1 uses Java 21. The downloaded 2026.3 `product-info.json` independently confirms `minRequiredJavaVersion: 25`. The selected SDK determines the build/test toolchain, while bytecode stays at 21 for the baseline. [Platform Java versions](https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html), [2026 API requirements](https://plugins.jetbrains.com/docs/intellij/api-changes-list-2026.html).

The YAML plugin ID remains `org.jetbrains.plugins.yaml` in the inspected 2026.3 distribution. Bjoern still depends on that plugin and uses YAML PSI implementation classes, so future platform changes require both binary and editor checks. General API-change lists do not cover all internal/unstable changes. [Plugin dependencies](https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html), [2026 incompatible changes](https://plugins.jetbrains.com/docs/intellij/api-changes-list-2026.html).

`colorSettingsPage` is deprecated in 2026.3 in favor of `colorSettings`, but JetBrains explicitly says the old registration still works. Keeping it is supported while earlier IDEs remain in scope and avoids the dynamic-load restriction introduced by dual registration on older IDEs. This migration therefore retains the cross-version declaration. [Color settings registration](https://plugins.jetbrains.com/docs/intellij/syntax-highlighting-and-error-highlighting.html#registering-a-color-settings-page).

## Verification workflow

```bash
# Release baseline; JDK 21
./gradlew test buildPlugin verifyPluginProjectConfiguration verifyPluginStructure

# Editor fixtures against newer SDKs; installed JDK 25
./gradlew test -PplatformVersion=2026.2.5
./gradlew test -PplatformVersion=2026.3

# Verify the baseline artifact against all three explicit IDE targets
./gradlew verifyPlugin
```

The CI matrix runs those editor/build checks for each generation and uploads reports. The compatibility job and release workflow verify the baseline artifact. The release workflow runs tests before publishing and takes the version from a validated tag via `-PpluginVersion`.

## Local results

| Target | Editor fixtures | Baseline artifact binary verification |
| --- | --- | --- |
| 2026.1.5.1 / IU-261.27258.136 | 51 passed, 0 failed, 0 skipped | Compatible |
| 2026.2.5 / IU-262.12573.28 | 51 passed, 0 failed, 0 skipped | Compatible |
| 2026.3 / IU-263.7307.200 | 51 passed, 0 failed, 0 skipped | Compatible; 5 deprecated API usages |

The combined baseline test, build, configuration, structure and verifier command completed successfully. The packaged archive is `build/distributions/bjoern-intellij-plugin-1.0.4.zip`. Its inspected descriptor contains version 1.0.4, `since-build="261"`, `until-build="263.*"`; its Java classes use bytecode version 65 (Java 21), contain no `YAMLFlexLexer` dependency, and omit the removed unused lexer.

Reports are in `build/reports/tests/test/` and `build/reports/pluginVerifier/`; local per-generation test-result copies and the combined log are in `build/validation/` (generated, ignored files).

Two non-blocking tooling notices remain:

- Configuration verification recommends removing `until-build` for forward installation. The explicit `263.*` bound is intentional for the chosen, verified 2026 support policy; a future platform is checked before expanding it.
- IntelliJ’s code-instrumentation task uses Gradle’s deprecated Ant API, scheduled for removal in Gradle 10. This is emitted from the upstream build plugin; Gradle 9.9.1 is supported and the task succeeds.

The five deprecated API usages are in the cross-version YAML parser/lexer adapter. These are supported by 2026.3, and the binary checks report no compatibility problems. This is future migration work rather than a suppressed current failure.

Binary checks and headless fixtures do not replace interactive checks in the exact IDE installation where a user sees an issue; no manual GUI session has been performed. The CI workflow definitions were checked locally, but have not been executed on GitHub during this task. No release was published.
