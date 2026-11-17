# Bjoern IntelliJ Plugin

This repository provides editor support for YAML-based `.zgr` BDD specifications. The separate Bjoern Gradle framework reads the specifications and generates JUnit test classes, step interfaces, and documentation. Language changes must remain compatible across both projects.

## Project references

- For setup, supported IDE versions and toolchains, read `README.md` and the current `build.gradle.kts`/Gradle wrapper configuration.
- For component responsibilities, language changes, tests or publishing, read `PLUGIN-DEVELOPMENT-GUIDE.md`.
- Before changing the supported IDE range, SDK, YAML dependency or parser/lexer integration, read `docs/intellij-compatibility.md`. It records the reproduced 2026.3 failure and cross-version solution.
- Extension registrations and dependency declarations are in `src/main/resources/META-INF/plugin.xml`.

## Architecture constraints

- `BjoernLanguage` derives from `YAMLLanguage.INSTANCE`. `BjoernFile` extends `YAMLFileImpl` so consumers can use YAML PSI.
- Keep a Bjoern-language root element in `BjoernParserDefinition`, and delegate its parsing to YAML’s own file element. Simply returning YAML’s file root violates the file-language assertion. Calling YAML’s legacy parser directly fails on newer syntax-based implementations.
- Obtain the highlighting lexer through `YAMLParserDefinition.createLexer(Project)`. The old `YAMLFlexLexer` implementation is absent in 2026.3.
- `BjoernValidatingLexer` adds keyword/parameter highlighting; parsing itself remains YAML’s responsibility.
- Use `BJOERN_*` text-attribute names and theme-aware defaults to avoid clashes with YAML’s attributes.
- Completion is registered for `Bjoern` and `yaml`, guarded by the `.zgr` filename. Collect statements from YAML PSI sequences in the current and other indexed project files, and preserve the Given/When/Then context.
- Quoted statement values become empty parameter placeholders. Tab navigation must delegate unrelated actions to the original editor handler.
- Keep formatting and Enter indentation consistent with each other and the framework’s validation rules.

## Language changes

1. Confirm syntax, scope and semantics in the framework.
2. Update `BjoernKeywordValidationLexer` and completion keywords. Ordinary keywords reuse the existing keyword token category.
3. Update relevant inspections, formatting/indentation, breadcrumbs, the file template and examples.
4. Cover the observable editing behavior with fixture tests. For field placement, exercise real parsed PSI rather than testing only a string-validation helper.

## Tests and validation

Tests extend IntelliJ’s `BasePlatformTestCase`; public `test…` methods are discovered through Gradle’s explicitly configured JUnit runner. The test task fails when no tests are discovered. Keep assertions on editor/PSI behavior; avoid private-field reflection, copied production algorithms, and constructor-only assertions as evidence of feature coverage.

```bash
./gradlew test
./gradlew test -PplatformVersion=2026.2.5
./gradlew test -PplatformVersion=2026.3
./gradlew buildPlugin verifyPluginProjectConfiguration verifyPluginStructure
./gradlew verifyPlugin
```

The default SDK uses JDK 21; the two newer test SDKs need installed JDK 25. Bytecode remains Java 21. CI runs a platform matrix and verifies the baseline artifact against explicit IDE targets. Inspect executed counts and reports before claiming compatibility; an IDE-range declaration or successful compilation alone is insufficient.

Use `./gradlew runIde -PplatformVersion=2026.3` for interactive smoke checks. The current build is Java-only; the Kotlin JVM plugin is unnecessary for Kotlin Gradle scripts.
