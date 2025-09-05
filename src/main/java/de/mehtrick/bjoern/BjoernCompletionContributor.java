package de.mehtrick.bjoern;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;

import java.util.List;

public class BjoernCompletionContributor extends CompletionContributor {
    private static final List<String> BDD_KEYWORDS = List.of(
            "Feature:", "Background:", "Given:", "When:", "Then:", "Scenario:", "Scenarios:"
    );
    
    private static final List<String> GIVEN_SUGGESTIONS = List.of(
            "A user with username",
            "A system with",
            "An empty database",
            "A registered user",
            "Configuration is set to"
    );
    
    private static final List<String> WHEN_SUGGESTIONS = List.of(
            "User attempts to login",
            "User clicks on",
            "System processes",
            "Request is sent to",
            "User navigates to"
    );
    
    private static final List<String> THEN_SUGGESTIONS = List.of(
            "Login should be successful",
            "User should be redirected to",
            "System should display",
            "Response should contain",
            "Error message should appear"
    );

    public BjoernCompletionContributor() {
        // Complete BDD keywords at the beginning of lines
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        for (String keyword : BDD_KEYWORDS) {
                            result.addElement(LookupElementBuilder.create(keyword)
                                    .withBoldness(true)
                                    .withTypeText("BDD Keyword"));
                        }
                    }
                });
        
        // Also support completion in text areas and for partial text
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        // Only provide keyword completions in Bjoern files
                        if (parameters.getOriginalFile() instanceof BjoernFile) {
                            String text = parameters.getEditor().getDocument().getText();
                            int offset = parameters.getOffset();
                            
                            // Check if we're at the beginning of a line or after whitespace
                            boolean isLineStart = offset == 0 || text.charAt(offset - 1) == '\n';
                            
                            if (isLineStart || (offset > 0 && Character.isWhitespace(text.charAt(offset - 1)))) {
                                for (String keyword : BDD_KEYWORDS) {
                                    result.addElement(LookupElementBuilder.create(keyword)
                                            .withBoldness(true)
                                            .withTypeText("BDD Keyword"));
                                }
                            }
                        }
                    }
                });
        
        // Complete Given statements
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(PlatformPatterns.psiElement().withText("Given:")),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        for (String suggestion : GIVEN_SUGGESTIONS) {
                            result.addElement(LookupElementBuilder.create("- " + suggestion)
                                    .withTypeText("Given suggestion"));
                        }
                    }
                });
        
        // Complete When statements
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(PlatformPatterns.psiElement().withText("When:")),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        for (String suggestion : WHEN_SUGGESTIONS) {
                            result.addElement(LookupElementBuilder.create("- " + suggestion)
                                    .withTypeText("When suggestion"));
                        }
                    }
                });
        
        // Complete Then statements
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .afterLeaf(PlatformPatterns.psiElement().withText("Then:")),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        for (String suggestion : THEN_SUGGESTIONS) {
                            result.addElement(LookupElementBuilder.create("- " + suggestion)
                                    .withTypeText("Then suggestion"));
                        }
                    }
                });
    }
}