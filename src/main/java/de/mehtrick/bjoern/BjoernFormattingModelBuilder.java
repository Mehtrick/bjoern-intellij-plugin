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
        // Create a custom formatting model that wraps YAML but adds Bjoern-specific formatting
        FormattingModel yamlModel = yamlBuilder.createModel(formattingContext);
        
        // Return a wrapper that applies Bjoern BDD formatting rules
        return new BjoernFormattingModelWrapper(yamlModel, formattingContext);
    }
    
    private static class BjoernFormattingModelWrapper implements FormattingModel {
        private final FormattingModel yamlModel;
        private final FormattingContext context;
        
        public BjoernFormattingModelWrapper(FormattingModel yamlModel, FormattingContext context) {
            this.yamlModel = yamlModel;
            this.context = context;
        }
        
        @NotNull
        @Override
        public Block getRootBlock() {
            return new BjoernFormattingBlock(yamlModel.getRootBlock(), context);
        }
        
        @NotNull
        @Override
        public FormattingDocumentModel getDocumentModel() {
            return yamlModel.getDocumentModel();
        }
        
        @NotNull
        @Override
        public TextRange replaceWhiteSpace(@NotNull TextRange textRange, @NotNull String whiteSpace) {
            // Apply Bjoern-specific formatting rules
            String bjoernFormattedWhiteSpace = applyBjoernFormatting(textRange, whiteSpace);
            return yamlModel.replaceWhiteSpace(textRange, bjoernFormattedWhiteSpace);
        }
        
        @NotNull
        @Override
        public TextRange shiftIndentInsideRange(@NotNull ASTNode node, @NotNull TextRange range, int indent) {
            return yamlModel.shiftIndentInsideRange(node, range, indent);
        }
        
        @Override
        public void commitChanges() {
            yamlModel.commitChanges();
        }
        
        private String applyBjoernFormatting(TextRange textRange, String whiteSpace) {
            // Get the document text to understand context
            FormattingDocumentModel documentModel = getDocumentModel();
            String documentText = documentModel.getText(new TextRange(0, documentModel.getTextLength())).toString();
            
            // Find the line that contains this text range
            int lineStart = documentText.lastIndexOf('\n', textRange.getStartOffset()) + 1;
            int lineEnd = documentText.indexOf('\n', textRange.getEndOffset());
            if (lineEnd == -1) lineEnd = documentText.length();
            
            if (lineStart < lineEnd) {
                String lineText = documentText.substring(lineStart, lineEnd).trim();
                
                // Apply Bjoern BDD indentation rules
                String bjoernIndent = calculateBjoernIndentation(documentText, lineStart, lineText);
                
                // If we calculated a specific indentation, use it
                if (bjoernIndent != null) {
                    // Keep any newlines from the original whitespace
                    boolean hasNewlines = whiteSpace.contains("\n");
                    if (hasNewlines) {
                        return "\n" + bjoernIndent;
                    } else if (!whiteSpace.isEmpty()) {
                        return bjoernIndent;
                    }
                }
            }
            
            return whiteSpace;
        }
        
        private String calculateBjoernIndentation(String documentText, int lineStart, String lineText) {
            if (lineText.isEmpty()) return null;
            
            // Feature, Background, Scenarios - no indentation
            if (lineText.startsWith("Feature:") || 
                lineText.startsWith("Background:") || 
                lineText.startsWith("Scenarios:")) {
                return "";
            }
            
            // Analyze context to determine proper indentation
            BjoernContext context = analyzeContext(documentText, lineStart);
            
            // Scenario items under Scenarios - 2 spaces  
            if (lineText.startsWith("- Scenario:")) {
                return "  ";
            }
            
            // Given/When/Then keywords
            if (lineText.startsWith("Given:") || lineText.startsWith("When:") || lineText.startsWith("Then:")) {
                return context.inScenario ? "    " : "  ";
            }
            
            // List items (but not Scenario items)
            if (lineText.startsWith("- ") && !lineText.startsWith("- Scenario:")) {
                return context.inScenario ? "      " : "    ";
            }
            
            return null; // Use default formatting
        }
        
        private BjoernContext analyzeContext(String documentText, int currentLineStart) {
            BjoernContext context = new BjoernContext();
            String[] lines = documentText.substring(0, currentLineStart).split("\n");
            
            boolean inScenarios = false;
            boolean inScenario = false;
            
            for (String line : lines) {
                String trimmed = line.trim();
                
                if (trimmed.startsWith("Background:")) {
                    inScenarios = false;
                    inScenario = false;
                } else if (trimmed.startsWith("Scenarios:")) {
                    inScenarios = true;
                    inScenario = false;
                } else if (trimmed.startsWith("- Scenario:")) {
                    inScenario = true;
                }
            }
            
            context.inScenarios = inScenarios;
            context.inScenario = inScenario;
            
            return context;
        }
    }
    
    private static class BjoernFormattingBlock implements Block {
        private final Block yamlBlock;
        private final FormattingContext context;
        
        public BjoernFormattingBlock(Block yamlBlock, FormattingContext context) {
            this.yamlBlock = yamlBlock;
            this.context = context;
        }
        
        @Override
        public Indent getIndent() {
            return yamlBlock.getIndent();
        }
        
        @Override
        public Alignment getAlignment() {
            return yamlBlock.getAlignment();
        }
        
        @Override
        public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
            return yamlBlock.getSpacing(child1, child2);
        }
        
        @Override
        public ChildAttributes getChildAttributes(int newChildIndex) {
            return yamlBlock.getChildAttributes(newChildIndex);
        }
        
        @Override
        public boolean isIncomplete() {
            return yamlBlock.isIncomplete();
        }
        
        @Override
        public boolean isLeaf() {
            return yamlBlock.isLeaf();
        }
        
        @NotNull
        @Override
        public TextRange getTextRange() {
            return yamlBlock.getTextRange();
        }
        
        @NotNull
        @Override
        public java.util.List<Block> getSubBlocks() {
            return yamlBlock.getSubBlocks();
        }
        
        @Override
        public Wrap getWrap() {
            return yamlBlock.getWrap();
        }
    }
    
    private static class BjoernContext {
        boolean inScenarios = false;
        boolean inScenario = false;
    }
}