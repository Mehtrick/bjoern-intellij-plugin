package de.mehtrick.bjoern;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.*;

import java.util.*;
import java.util.regex.Pattern;

public class BjoernCompletionContributor extends CompletionContributor {
    private static final List<String> BDD_KEYWORDS = List.of(
            "Feature:", "Version:", "Reference:", "Changelog:",
            "Background:", "Given:", "When:", "Then:", "Scenario:", "Scenarios:",
            "Deprecated:"
    );

    private static final List<String> BOOLEAN_VALUES = List.of("true", "false");
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"");

    public BjoernCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement()
                        .inFile(PlatformPatterns.psiFile().withName(StandardPatterns.string().endsWith(".zgr"))),
                new BjoernCompletionProvider());
    }
    
    private static class BjoernCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet result) {
            PsiFile file = parameters.getOriginalFile();
            if (!file.getName().endsWith(".zgr")) {
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

            // Provide boolean completion for Deprecated: values
            if (isDeprecatedValueContext(element)) {
                for (String value : BOOLEAN_VALUES) {
                    result.addElement(LookupElementBuilder.create(value)
                            .withBoldness(true)
                            .withTypeText("boolean"));
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
        int lineStart = text.lastIndexOf('\n', offset - 1) + 1;
        String prefix = text.substring(lineStart, offset).trim();
        if (prefix.startsWith("- ")) {
            return "Scenario".startsWith(prefix.substring(2));
        }
        return prefix.chars().allMatch(Character::isLetter);
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

    private static boolean isDeprecatedValueContext(PsiElement element) {
        YAMLKeyValue currentKeyValue = PsiTreeUtil.getParentOfType(element, YAMLKeyValue.class);
        if (currentKeyValue == null || !"Deprecated".equals(currentKeyValue.getKeyText())) {
            return false;
        }

        // Ensure the caret is inside the Deprecated value, not inside the key itself.
        YAMLValue value = currentKeyValue.getValue();
        if (value == null) {
            return false;
        }

        PsiElement current = element;
        while (current != null && current != currentKeyValue) {
            if (current == value) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
    
    private static Set<String> extractStatementsFromFile(PsiFile file, String contextType) {
        Set<String> statements = new LinkedHashSet<>();

        // Collect statements from the current file first
        collectStatements(file, contextType, statements);

        // Then scan all other .zgr files in the project
        Project project = file.getProject();
        Collection<VirtualFile> allZgrFiles = FileTypeIndex.getFiles(
                BjoernFileType.INSTANCE,
                GlobalSearchScope.projectScope(project));

        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile vf : allZgrFiles) {
            if (vf.equals(file.getVirtualFile())) continue; // already done above
            PsiFile otherFile = psiManager.findFile(vf);
            if (otherFile != null) {
                collectStatements(otherFile, contextType, statements);
            }
        }

        return statements;
    }

    private static void collectStatements(PsiFile file, String contextType, Set<String> statements) {
        for (YAMLKeyValue key : PsiTreeUtil.findChildrenOfType(file, YAMLKeyValue.class)) {
            if (!contextType.equals(key.getKeyText()) || !(key.getValue() instanceof YAMLSequence sequence)) {
                continue;
            }
            for (YAMLSequenceItem item : sequence.getItems()) {
                if (item.getValue() instanceof YAMLScalar scalar) {
                    String statement = scalar.getTextValue().trim();
                    if (!statement.isEmpty()) {
                        statements.add(VARIABLE_PATTERN.matcher(statement).replaceAll("\"\""));
                    }
                }
            }
        }
    }
}
