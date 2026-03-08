package de.mehtrick.bjoern;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Registers all Bjoern syntax-highlight colours in
 * <em>Settings → Editor → Color Scheme → Bjoern</em>.
 */
public class BjoernColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("BDD keyword//Feature, Background, Scenarios…", BjoernSyntaxHighlighter.BJOERN_KEYWORD),
            new AttributesDescriptor("BDD keyword//Invalid keyword", BjoernSyntaxHighlighter.BJOERN_INVALID_KEYWORD),
            new AttributesDescriptor("Keys//Scalar key", BjoernSyntaxHighlighter.BJOERN_SCALAR_KEY),
            new AttributesDescriptor("Values//Scalar value", BjoernSyntaxHighlighter.BJOERN_SCALAR_VALUE),
            new AttributesDescriptor("Values//Variable (double-quoted string)", BjoernSyntaxHighlighter.BJOERN_VARIABLE),
            new AttributesDescriptor("Comment", BjoernSyntaxHighlighter.BJOERN_COMMENT),
            new AttributesDescriptor("Reference//Valid hyperlink", BjoernSyntaxHighlighter.BJOERN_VALID_LINK),
    };

    @NotNull
    @Override
    public String getDisplayName() {
        return "Bjoern";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return BjoernFileType.INSTANCE.getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new BjoernSyntaxHighlighter();
    }

    @NonNls
    @NotNull
    @Override
    public String getDemoText() {
        return """
                # This is a comment
                Feature: Demo feature
                Version: "1.0.0"
                Reference: "[TICKET-1](https://example.com)"
                Changelog: ""
                Background:
                  Given:
                    - A running system
                Scenarios:
                  - Scenario: Happy path
                    Given:
                      - A user with username "alice"
                    When:
                      - User logs in with password "secret"
                    Then:
                      - Dashboard is shown
                """;
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
}


