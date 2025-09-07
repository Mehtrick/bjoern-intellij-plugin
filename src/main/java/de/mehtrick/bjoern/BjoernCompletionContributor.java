package de.mehtrick.bjoern;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLTokenTypes;
import org.jetbrains.yaml.psi.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BjoernCompletionContributor extends CompletionContributor {
    private static final List<String> BDD_KEYWORDS = List.of(
            "Feature:", "Background:", "Given:", "When:", "Then:", "Scenario:", "Scenarios:"
    );
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\"[^\"]*\"");

    public BjoernCompletionContributor() {
        // Primary completion provider for Bjoern files - using multiple patterns for better coverage
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".zgr"))),
                new BjoernCompletionProvider());
        
        // Backup completion provider for YAML-based structures
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(YAMLTokenTypes.TEXT)
                        .inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".zgr"))),
                new BjoernCompletionProvider());
        
        // Additional pattern for YAML scalar values
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(YAMLTokenTypes.SCALAR_KEY)
                        .inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".zgr"))),
                new BjoernCompletionProvider());
    }
    
    private static class BjoernCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            // Debug: Always try to provide completions for .zgr files
            PsiFile file = parameters.getOriginalFile();
            if (file == null || !file.getName().endsWith(".zgr")) {
                return;
            }
            
            PsiElement element = parameters.getPosition();
            String currentContext = getCurrentBDDContext(element);
            String text = parameters.getEditor().getDocument().getText();
            int offset = parameters.getOffset();
            
            // Always provide BDD keyword completions
            boolean shouldShowKeywords = shouldShowKeywordCompletion(text, offset);
            if (shouldShowKeywords) {
                for (String keyword : BDD_KEYWORDS) {
                    result.addElement(LookupElementBuilder.create(keyword)
                            .withBoldness(true)
                            .withTypeText("BDD Keyword"));
                }
            }
            
            // Provide smart completion based on context
            if (currentContext != null) {
                Set<String> suggestions = extractStatementsFromFile(file, currentContext);
                
                for (String suggestion : suggestions) {
                    String lookupText = isUnderListItem(text, offset) ? suggestion : "- " + suggestion;
                    result.addElement(LookupElementBuilder.create(lookupText)
                            .withTypeText(currentContext + " suggestion")
                            .withInsertHandler((context1, item) -> {
                                // Place cursor at first variable placeholder if any
                                int caretOffset = context1.getEditor().getCaretModel().getOffset();
                                String insertedText = item.getLookupString();
                                int firstQuote = insertedText.indexOf("\"\"");
                                if (firstQuote != -1) {
                                    context1.getEditor().getCaretModel().moveToOffset(caretOffset - insertedText.length() + firstQuote + 1);
                                }
                            }));
                }
            }
        }
    }
    
    private static boolean shouldShowKeywordCompletion(String text, int offset) {
        // Show keyword completion at beginning of line or after whitespace
        if (offset == 0) return true;
        if (offset > 0 && text.charAt(offset - 1) == '\n') return true;
        
        // Check if we're after whitespace that suggests we want a keyword
        if (offset > 0 && Character.isWhitespace(text.charAt(offset - 1))) {
            // Look back to see if we're at an appropriate indentation level for keywords
            int lineStart = text.lastIndexOf('\n', offset - 1) + 1;
            String linePrefix = text.substring(lineStart, offset).trim();
            return linePrefix.isEmpty(); // Empty line prefix suggests keyword completion
        }
        
        return false;
    }
    
    private static boolean isUnderListItem(String text, int offset) {
        // Check if we're already under a list item (starts with -)
        if (offset <= 0 || offset > text.length()) {
            return false;
        }
        
        int lineStart = text.lastIndexOf('\n', offset - 1) + 1;
        // Ensure lineStart is not greater than offset
        if (lineStart > offset) {
            lineStart = 0;
        }
        
        String currentLine = text.substring(lineStart, offset);
        return currentLine.trim().startsWith("-");
    }
    
    private static String getCurrentBDDContext(PsiElement element) {
        // Walk up the PSI tree to find the current BDD context (Given, When, Then)
        YAMLKeyValue currentKeyValue = PsiTreeUtil.getParentOfType(element, YAMLKeyValue.class);
        
        while (currentKeyValue != null) {
            String keyText = currentKeyValue.getKeyText();
            if (keyText.equals("Given") || keyText.equals("When") || keyText.equals("Then")) {
                return keyText;
            }
            currentKeyValue = PsiTreeUtil.getParentOfType(currentKeyValue, YAMLKeyValue.class);
        }
        
        // Alternative approach: analyze the text context
        return getCurrentBDDContextFromText(element);
    }
    
    private static String getCurrentBDDContextFromText(PsiElement element) {
        PsiFile file = element.getContainingFile();
        String text = file.getText();
        int offset = element.getTextOffset();
        
        // Look backwards from the current position to find the nearest BDD keyword
        String[] lines = text.substring(0, offset).split("\n");
        
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith("Given:")) {
                return "Given";
            } else if (line.startsWith("When:")) {
                return "When";
            } else if (line.startsWith("Then:")) {
                return "Then";
            }
        }
        
        return null;
    }
    
    private static Set<String> extractStatementsFromFile(PsiFile file, String contextType) {
        Set<String> statements = new LinkedHashSet<>();
        String text = file.getText();
        
        // Pattern to match statements under the specified context
        Pattern sectionPattern = Pattern.compile(
            contextType + ":\\s*\n((?:\\s*-\\s*[^\n]+\n)*)", 
            Pattern.MULTILINE
        );
        
        Matcher sectionMatcher = sectionPattern.matcher(text);
        
        while (sectionMatcher.find()) {
            String section = sectionMatcher.group(1);
            
            // Extract individual list items
            Pattern itemPattern = Pattern.compile("^\\s*-\\s*(.+)$", Pattern.MULTILINE);
            Matcher itemMatcher = itemPattern.matcher(section);
            
            while (itemMatcher.find()) {
                String statement = itemMatcher.group(1).trim();
                
                // Replace quoted variables with empty placeholders
                String templateStatement = VARIABLE_PATTERN.matcher(statement).replaceAll("\"\"");
                
                statements.add(templateStatement);
            }
        }
        
        return statements;
    }
}