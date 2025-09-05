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

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        // Use YAML formatting as base but customize for Bjoern
        return yamlBuilder.createModel(formattingContext);
    }
}