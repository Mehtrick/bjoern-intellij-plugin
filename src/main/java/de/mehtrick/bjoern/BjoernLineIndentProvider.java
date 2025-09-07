package de.mehtrick.bjoern;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.lineIndent.LineIndentProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BjoernLineIndentProvider implements LineIndentProvider {

    @Override
    public boolean isSuitableFor(@Nullable Language language) {
        return language != null && language.isKindOf(BjoernLanguage.INSTANCE);
    }

    @Nullable
    @Override
    public String getLineIndent(@NotNull Project project, @NotNull Editor editor, @Nullable Language language, int offset) {
        if (language != null && language.isKindOf(BjoernLanguage.INSTANCE)) {
            return getBjoernLineIndent(project, editor, offset);
        }
        return null;
    }

    private String getBjoernLineIndent(@NotNull Project project, @NotNull Editor editor, int offset) {
        PsiFile file = com.intellij.psi.PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (file == null) {
            return "";
        }

        String documentText = editor.getDocument().getText();
        int lineStartOffset = documentText.lastIndexOf('\n', offset - 1) + 1;
        
        // Get content after cursor position on current line
        String currentLine = "";
        if (lineStartOffset < offset) {
            currentLine = documentText.substring(lineStartOffset, offset).trim();
        }
        
        // Get context from previous lines
        BjoernContext context = analyzeContext(documentText, lineStartOffset);
        
        // Calculate indentation based on Bjoern BDD structure and context
        return calculateBjoernIndent(currentLine, context);
    }

    private BjoernContext analyzeContext(String documentText, int currentLineStart) {
        BjoernContext context = new BjoernContext();
        String[] lines = documentText.substring(0, currentLineStart).split("\n");
        
        // Analyze the document structure to understand current context
        int indentLevel = 0;
        boolean inBackground = false;
        boolean inScenarios = false;
        boolean inScenario = false;
        String lastSection = "";
        
        for (String line : lines) {
            String trimmed = line.trim();
            int currentIndent = getLeadingWhitespaceCount(line);
            
            if (trimmed.startsWith("Feature:")) {
                context.hasFeature = true;
                indentLevel = 0;
            } else if (trimmed.startsWith("Background:")) {
                inBackground = true;
                inScenarios = false;
                inScenario = false;
                lastSection = "Background";
                indentLevel = 0;
            } else if (trimmed.startsWith("Scenarios:")) {
                inBackground = false;
                inScenarios = true;
                inScenario = false;
                lastSection = "Scenarios";
                indentLevel = 0;
            } else if (trimmed.startsWith("- Scenario:")) {
                inScenario = true;
                inBackground = false;
                context.inScenario = true;
                indentLevel = 2;
            } else if (trimmed.startsWith("Given:") || trimmed.startsWith("When:") || trimmed.startsWith("Then:")) {
                lastSection = trimmed.substring(0, trimmed.indexOf(":"));
                if (inScenario) {
                    indentLevel = 4;
                } else {
                    indentLevel = 2;
                }
            }
        }
        
        context.inBackground = inBackground;
        context.inScenarios = inScenarios;
        context.inScenario = inScenario;
        context.lastSection = lastSection;
        context.currentIndentLevel = indentLevel;
        
        return context;
    }

    private String calculateBjoernIndent(String currentLine, BjoernContext context) {
        // Feature, Background, Scenarios - no indentation
        if (currentLine.startsWith("Feature:") || 
            currentLine.startsWith("Background:") || 
            currentLine.startsWith("Scenarios:")) {
            return "";
        }
        
        // Scenario items under Scenarios - 2 spaces  
        if (currentLine.startsWith("- Scenario:")) {
            return "  "; // 2 spaces
        }
        
        // Given/When/Then keywords
        if (currentLine.startsWith("Given:") || currentLine.startsWith("When:") || currentLine.startsWith("Then:")) {
            if (context.inScenario) {
                return "    "; // 4 spaces - under Scenario
            } else {
                return "  "; // 2 spaces - under Background
            }
        }
        
        // List items  
        if (currentLine.startsWith("- ") && !currentLine.startsWith("- Scenario:")) {
            if (context.inScenario) {
                return "      "; // 6 spaces - under Given/When/Then which is under Scenario
            } else {
                return "    "; // 4 spaces - under Given/When/Then which is under Background
            }
        }
        
        // Default indentation based on context
        if (context.inScenario && context.lastSection.matches("Given|When|Then")) {
            return "      "; // 6 spaces - continuing under Given/When/Then in Scenario
        } else if (context.lastSection.matches("Given|When|Then")) {
            return "    "; // 4 spaces - continuing under Given/When/Then in Background
        } else if (context.inScenario) {
            return "    "; // 4 spaces - under Scenario
        } else if (context.inBackground) {
            return "  "; // 2 spaces - under Background
        }
        
        return "";
    }

    private static class BjoernContext {
        boolean hasFeature = false;
        boolean inBackground = false;
        boolean inScenarios = false;
        boolean inScenario = false;
        String lastSection = "";
        int currentIndentLevel = 0;
    }

    private int getLeadingWhitespaceCount(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (Character.isWhitespace(line.charAt(i))) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}