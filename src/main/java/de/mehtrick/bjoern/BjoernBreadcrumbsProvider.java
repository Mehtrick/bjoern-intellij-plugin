package de.mehtrick.bjoern;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.jetbrains.yaml.psi.YAMLSequenceItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides breadcrumb navigation for Bjoern BDD (.zgr) files.
 * Displays a path like: "Feature: Name > Scenario: Name > Given > step text"
 */
public class BjoernBreadcrumbsProvider implements BreadcrumbsProvider {

    private static final Language[] LANGUAGES = new Language[]{YAMLLanguage.INSTANCE};

    private static final Set<String> STRUCTURAL_KEYS = new HashSet<>(Arrays.asList(
            "Feature", "Background", "Scenarios", "Scenario", "Given", "When", "Then"
    ));

    private static final Set<String> STEP_SECTION_KEYS = new HashSet<>(Arrays.asList(
            "Given", "When", "Then"
    ));

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement e) {
        if (!(e.getContainingFile() instanceof BjoernFile)) {
            return false;
        }
        if (e instanceof YAMLKeyValue) {
            String key = ((YAMLKeyValue) e).getKeyText().trim();
            return STRUCTURAL_KEYS.contains(key);
        }
        if (e instanceof YAMLSequenceItem) {
            // Only accept step items directly under a Given/When/Then sequence
            PsiElement sequence = e.getParent();
            if (sequence != null) {
                PsiElement sectionKV = sequence.getParent();
                if (sectionKV instanceof YAMLKeyValue) {
                    String key = ((YAMLKeyValue) sectionKV).getKeyText().trim();
                    return STEP_SECTION_KEYS.contains(key);
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public String getElementInfo(@NotNull PsiElement e) {
        if (e instanceof YAMLKeyValue) {
            YAMLKeyValue kv = (YAMLKeyValue) e;
            String key = kv.getKeyText().trim();
            if ("Feature".equals(key) || "Scenario".equals(key)) {
                PsiElement value = kv.getValue();
                if (value instanceof YAMLScalar) {
                    String textValue = ((YAMLScalar) value).getTextValue().trim();
                    if (!textValue.isEmpty()) {
                        return key + ": " + textValue;
                    }
                }
            }
            return key;
        }
        if (e instanceof YAMLSequenceItem) {
            PsiElement value = ((YAMLSequenceItem) e).getValue();
            if (value instanceof YAMLScalar) {
                return ((YAMLScalar) value).getTextValue().trim();
            }
            String text = e.getText().trim();
            if (text.startsWith("- ")) {
                return text.substring(2).trim();
            }
            return text;
        }
        return e.getText().trim();
    }

    @Nullable
    @Override
    public String getElementTooltip(@NotNull PsiElement e) {
        return getElementInfo(e);
    }
}
