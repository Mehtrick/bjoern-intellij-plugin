package de.mehtrick.bjoern;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.formatter.YAMLFormattingModelBuilder;

public class BjoernFormattingModelBuilder implements FormattingModelBuilder {
    private final YAMLFormattingModelBuilder yamlBuilder = new YAMLFormattingModelBuilder();

    @NotNull
    @Override
    public FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        // Use YAML formatting as base - custom Bjoern BDD formatting is complex to implement
        // The LineIndentProvider handles proper indentation when users press Enter
        // Full document formatting follows YAML rules for now
        return yamlBuilder.createModel(formattingContext);
    }
}