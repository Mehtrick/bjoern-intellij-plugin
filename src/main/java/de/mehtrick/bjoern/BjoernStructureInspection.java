package de.mehtrick.bjoern;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.*;

import java.util.*;

/**
 * Structural inspection for Bjoern BDD spec files.
 * <p>
 * Checks:
 * <ol>
 *   <li>Required top-level fields {@code Feature:} and {@code Scenarios:} must be present.</li>
 *   <li>Duplicate scenario names within the same file are flagged as warnings.</li>
 *   <li>Empty {@code Given:}, {@code When:} or {@code Then:} blocks (no list items) are warned about.</li>
 *   <li>{@code Deprecated:} is only allowed inside a scenario and must have the value {@code true} or {@code false}.</li>
 * </ol>
 */
public class BjoernStructureInspection extends LocalInspectionTool {

    private static final Set<String> REQUIRED_TOP_LEVEL = Set.of("Feature", "Scenarios");
    private static final Set<String> BDD_STEP_KEYS = Set.of("Given", "When", "Then");
    static final String DEPRECATED_KEY = "Deprecated";
    private static final Set<String> VALID_DEPRECATED_VALUES = Set.of("true", "false");

    @Override
    public ProblemDescriptor @NotNull [] checkFile(@NotNull PsiFile file,
                                                    @NotNull InspectionManager manager,
                                                    boolean isOnTheFly) {
        if (!file.getName().endsWith(".zgr")) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }
        if (!(file instanceof YAMLFile yamlFile)) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }

        List<ProblemDescriptor> problems = new ArrayList<>();

        for (YAMLDocument document : yamlFile.getDocuments()) {
            YAMLValue topValue = document.getTopLevelValue();
            if (!(topValue instanceof YAMLMapping mapping)) {
                continue;
            }

            checkRequiredTopLevelFields(mapping, manager, isOnTheFly, problems, file);
            checkDuplicateScenarioNames(mapping, manager, isOnTheFly, problems);
            checkEmptyStepBlocks(mapping, manager, isOnTheFly, problems);
            checkDeprecatedFields(mapping, manager, isOnTheFly, problems);
        }

        return problems.toArray(ProblemDescriptor.EMPTY_ARRAY);
    }

    // -------------------------------------------------------------------------
    // Check 1: required top-level fields
    // -------------------------------------------------------------------------
    private void checkRequiredTopLevelFields(YAMLMapping mapping,
                                              InspectionManager manager,
                                              boolean isOnTheFly,
                                              List<ProblemDescriptor> problems,
                                              PsiFile file) {
        Set<String> presentKeys = new HashSet<>();
        for (YAMLKeyValue kv : mapping.getKeyValues()) {
            presentKeys.add(kv.getKeyText());
        }

        for (String required : REQUIRED_TOP_LEVEL) {
            if (!presentKeys.contains(required)) {
                // Report on the whole file (first character) since the field is missing entirely
                problems.add(manager.createProblemDescriptor(
                        file,
                        TextRange.from(0, 1),
                        "Bjoern spec is missing required top-level field: '" + required + ":'",
                        ProblemHighlightType.WARNING,
                        isOnTheFly));
            }
        }
    }

    // -------------------------------------------------------------------------
    // Check 2: duplicate scenario names
    // -------------------------------------------------------------------------
    private void checkDuplicateScenarioNames(YAMLMapping mapping,
                                              InspectionManager manager,
                                              boolean isOnTheFly,
                                              List<ProblemDescriptor> problems) {
        YAMLKeyValue scenariosKV = mapping.getKeyValueByKey("Scenarios");
        if (scenariosKV == null) return;

        YAMLValue scenariosValue = scenariosKV.getValue();
        if (!(scenariosValue instanceof YAMLSequence sequence)) return;

        Map<String, YAMLKeyValue> seenNames = new LinkedHashMap<>();

        for (YAMLSequenceItem item : sequence.getItems()) {
            YAMLValue itemValue = item.getValue();
            if (!(itemValue instanceof YAMLMapping scenarioMapping)) continue;

            YAMLKeyValue scenarioKV = scenarioMapping.getKeyValueByKey("Scenario");
            if (scenarioKV == null) continue;

            String scenarioName = scenarioKV.getValueText().trim();
            if (scenarioName.isBlank()) continue;

            if (seenNames.containsKey(scenarioName)) {
                // Report on the duplicate (second occurrence)
                problems.add(manager.createProblemDescriptor(
                        scenarioKV,
                        "Duplicate scenario name: '" + scenarioName + "'",
                        isOnTheFly,
                        LocalQuickFix.EMPTY_ARRAY,
                        ProblemHighlightType.WARNING));
            } else {
                seenNames.put(scenarioName, scenarioKV);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Check 3: empty Given / When / Then blocks
    // -------------------------------------------------------------------------
    private void checkEmptyStepBlocks(YAMLMapping mapping,
                                       InspectionManager manager,
                                       boolean isOnTheFly,
                                       List<ProblemDescriptor> problems) {
        // Check Background
        YAMLKeyValue backgroundKV = mapping.getKeyValueByKey("Background");
        if (backgroundKV != null) {
            YAMLValue bgValue = backgroundKV.getValue();
            if (bgValue instanceof YAMLMapping bgMapping) {
                for (YAMLKeyValue kv : bgMapping.getKeyValues()) {
                    if (BDD_STEP_KEYS.contains(kv.getKeyText())) {
                        checkStepHasItems(kv, manager, isOnTheFly, problems, "Background");
                    }
                }
            }
        }

        // Check each Scenario
        YAMLKeyValue scenariosKV = mapping.getKeyValueByKey("Scenarios");
        if (scenariosKV == null) return;

        YAMLValue scenariosValue = scenariosKV.getValue();
        if (!(scenariosValue instanceof YAMLSequence sequence)) return;

        for (YAMLSequenceItem item : sequence.getItems()) {
            YAMLValue itemValue = item.getValue();
            if (!(itemValue instanceof YAMLMapping scenarioMapping)) continue;

            YAMLKeyValue scenarioKV = scenarioMapping.getKeyValueByKey("Scenario");
            String scenarioLabel = scenarioKV != null ? "Scenario '" + scenarioKV.getValueText().trim() + "'" : "Scenario";

            for (YAMLKeyValue kv : scenarioMapping.getKeyValues()) {
                if (BDD_STEP_KEYS.contains(kv.getKeyText())) {
                    checkStepHasItems(kv, manager, isOnTheFly, problems, scenarioLabel);
                }
            }
        }
    }

    private void checkStepHasItems(YAMLKeyValue stepKV,
                                    InspectionManager manager,
                                    boolean isOnTheFly,
                                    List<ProblemDescriptor> problems,
                                    String contextLabel) {
        YAMLValue value = stepKV.getValue();
        boolean isEmpty;

        if (value == null) {
            isEmpty = true;
        } else if (value instanceof YAMLSequence sequence) {
            isEmpty = sequence.getItems().isEmpty();
        } else {
            // scalar / null value – consider empty
            isEmpty = value.getText().isBlank();
        }

        if (isEmpty) {
            problems.add(manager.createProblemDescriptor(
                    stepKV,
                    "Empty '" + stepKV.getKeyText() + ":' block in " + contextLabel + " – add at least one step",
                    isOnTheFly,
                    LocalQuickFix.EMPTY_ARRAY,
                    ProblemHighlightType.WEAK_WARNING));
        }
    }

    // -------------------------------------------------------------------------
    // Check 4: Deprecated field placement and value
    // -------------------------------------------------------------------------
    private void checkDeprecatedFields(YAMLMapping mapping,
                                       InspectionManager manager,
                                       boolean isOnTheFly,
                                       List<ProblemDescriptor> problems) {
        collectDeprecatedKeyValues(mapping, manager, isOnTheFly, problems);
    }

    private void collectDeprecatedKeyValues(YAMLValue value,
                                            InspectionManager manager,
                                            boolean isOnTheFly,
                                            List<ProblemDescriptor> problems) {
        if (value instanceof YAMLMapping mapping) {
            for (YAMLKeyValue kv : mapping.getKeyValues()) {
                if (DEPRECATED_KEY.equals(kv.getKeyText())) {
                    validateDeprecatedKeyValue(kv, manager, isOnTheFly, problems);
                }
                YAMLValue childValue = kv.getValue();
                if (childValue instanceof YAMLMapping || childValue instanceof YAMLSequence) {
                    collectDeprecatedKeyValues(childValue, manager, isOnTheFly, problems);
                }
            }
        } else if (value instanceof YAMLSequence sequence) {
            for (YAMLSequenceItem item : sequence.getItems()) {
                collectDeprecatedKeyValues(item.getValue(), manager, isOnTheFly, problems);
            }
        }
    }

    private void validateDeprecatedKeyValue(YAMLKeyValue deprecatedKV,
                                            InspectionManager manager,
                                            boolean isOnTheFly,
                                            List<ProblemDescriptor> problems) {
        if (!isInsideScenario(deprecatedKV)) {
            problems.add(manager.createProblemDescriptor(
                    deprecatedKV,
                    "'Deprecated:' is only allowed inside a scenario",
                    isOnTheFly,
                    LocalQuickFix.EMPTY_ARRAY,
                    ProblemHighlightType.WARNING));
        }

        String valueText = deprecatedKV.getValueText().trim();
        if (!VALID_DEPRECATED_VALUES.contains(valueText)) {
            problems.add(manager.createProblemDescriptor(
                    deprecatedKV,
                    "'Deprecated:' value must be 'true' or 'false' but found '" + valueText + "'",
                    isOnTheFly,
                    LocalQuickFix.EMPTY_ARRAY,
                    ProblemHighlightType.WARNING));
        }
    }

    private boolean isInsideScenario(YAMLKeyValue deprecatedKV) {
        // Deprecated: must be a direct key of a scenario mapping.
        PsiElement parent = deprecatedKV.getParent();
        if (!(parent instanceof YAMLMapping)) {
            return false;
        }

        PsiElement scenarioKV = parent.getParent();
        if (!(scenarioKV instanceof YAMLKeyValue) || !"Scenario".equals(((YAMLKeyValue) scenarioKV).getKeyText())) {
            return false;
        }

        PsiElement sequenceItem = scenarioKV.getParent();
        if (!(sequenceItem instanceof YAMLSequenceItem)) {
            return false;
        }

        PsiElement sequence = sequenceItem.getParent();
        if (!(sequence instanceof YAMLSequence)) {
            return false;
        }

        PsiElement scenariosKV = sequence.getParent();
        return scenariosKV instanceof YAMLKeyValue && "Scenarios".equals(((YAMLKeyValue) scenariosKV).getKeyText());
    }

    /**
     * Returns {@code true} when the supplied value is a valid Deprecated field value
     * ({@code true} or {@code false}). Used by tests and can be reused by other callers.
     */
    static boolean isValidDeprecatedValue(String value) {
        return value != null && VALID_DEPRECATED_VALUES.contains(value.trim());
    }
}



