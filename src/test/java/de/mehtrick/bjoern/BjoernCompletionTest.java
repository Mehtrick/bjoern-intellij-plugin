package de.mehtrick.bjoern;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class BjoernCompletionTest {

    public void testBDDKeywordCompletion() {
        // Test that BDD keywords are offered for completion
        BjoernCompletionContributor contributor = new BjoernCompletionContributor();
        if (contributor == null) {
            throw new AssertionError("Completion contributor should not be null");
        }
        
        // This is a basic test to ensure the contributor is instantiated correctly
        // More comprehensive testing would require setting up a mock completion environment
    }

    public void testFormattingModelBuilder() {
        // Test that the formatting model builder is properly instantiated
        BjoernFormattingModelBuilder builder = new BjoernFormattingModelBuilder();
        if (builder == null) {
            throw new AssertionError("Formatting model builder should not be null");
        }
    }
    
    public void testStatementExtractionPattern() {
        // Test the regex pattern used for extracting statements
        String testText = "Given:\n  - there are \"2\" bottles of wine\n  - there are \"0\" bottles of beer\nWhen:\n  - user does something";
        
        Pattern sectionPattern = Pattern.compile(
            "Given" + ":\\s*\n((?:\\s*-\\s*[^\n]+\n)*)", 
            Pattern.MULTILINE
        );
        
        java.util.regex.Matcher sectionMatcher = sectionPattern.matcher(testText);
        
        if (!sectionMatcher.find()) {
            throw new AssertionError("Should find Given section in test text");
        }
        
        String section = sectionMatcher.group(1);
        if (section == null || section.trim().isEmpty()) {
            throw new AssertionError("Should extract Given section content");
        }
        
        // Check that it contains expected statements
        if (!section.contains("bottles of wine") || !section.contains("bottles of beer")) {
            throw new AssertionError("Should extract bottle statements from Given section");
        }
    }
    
    public void testVariableReplacement() {
        // Test that variables in quotes are properly replaced with empty placeholders
        Pattern variablePattern = Pattern.compile("\"[^\"]*\"");
        String testStatement = "there are \"2\" bottles of wine";
        String templateStatement = variablePattern.matcher(testStatement).replaceAll("\"\"");
        
        String expected = "there are \"\" bottles of wine";
        if (!expected.equals(templateStatement)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + templateStatement + "'");
        }
    }

    @Test
    public void testIsInsideParameter() {
        // Cursor between quotes -> inside parameter
        String text = "- there are \"\" bottles";
        int insideOffset = text.indexOf("\"\"") + 1; // between the two quotes
        if (!BjoernTabHandler.isInsideParameter(text, insideOffset)) {
            throw new AssertionError("Cursor between empty quotes should be considered inside a parameter");
        }

        // Cursor before the opening quote -> not inside
        int beforeOffset = text.indexOf("\"\"");
        if (BjoernTabHandler.isInsideParameter(text, beforeOffset)) {
            throw new AssertionError("Cursor on opening quote should not be considered inside a parameter");
        }

        // Text with no parameters
        if (BjoernTabHandler.isInsideParameter("no params here", 5)) {
            throw new AssertionError("Text without parameters should return false");
        }

        // Cursor inside a filled parameter
        String filledText = "- there are \"2\" bottles";
        int insideFilled = filledText.indexOf("\"2\"") + 1; // on the '2'
        if (!BjoernTabHandler.isInsideParameter(filledText, insideFilled)) {
            throw new AssertionError("Cursor inside filled parameter should be detected");
        }
    }

    @Test
    public void testFindNextParameter() {
        // Two empty parameters – Tab from first should return second
        String text = "- step with \"\" and \"\" values";
        int firstParamOffset = text.indexOf("\"\"") + 1;
        int[] next = BjoernTabHandler.findNextParameter(text, firstParamOffset);
        if (next == null) {
            throw new AssertionError("Should find a next parameter");
        }
        int secondParamStart = text.indexOf("\"\"", text.indexOf("\"\"") + 2);
        if (next[0] != secondParamStart) {
            throw new AssertionError("Next parameter should be the second \"\" occurrence");
        }

        // Wrap-around: Tab from the last parameter should return the first
        int secondParamOffset = secondParamStart + 1;
        int[] wrapped = BjoernTabHandler.findNextParameter(text, secondParamOffset);
        if (wrapped == null || wrapped[0] != text.indexOf("\"\"")) {
            throw new AssertionError("Tab from last parameter should wrap to first");
        }

        // Cursor not inside any parameter -> null
        int[] noParam = BjoernTabHandler.findNextParameter(text, 0);
        if (noParam != null) {
            throw new AssertionError("Cursor outside parameters should return null");
        }

        // No parameters in text -> null
        int[] noParamText = BjoernTabHandler.findNextParameter("no params", 3);
        if (noParamText != null) {
            throw new AssertionError("Text without parameters should return null");
        }

        // Single parameter – Tab should wrap back to itself
        String single = "- step with \"\" value";
        int[] self = BjoernTabHandler.findNextParameter(single, single.indexOf("\"\"") + 1);
        if (self == null || self[0] != single.indexOf("\"\"")) {
            throw new AssertionError("Single parameter should wrap to itself");
        }

        // Filled parameter – next should still be found
        String filled = "- step with \"foo\" and \"\" values";
        int insideFoo = filled.indexOf("\"foo\"") + 1;
        int[] afterFoo = BjoernTabHandler.findNextParameter(filled, insideFoo);
        if (afterFoo == null || afterFoo[0] != filled.indexOf("\"\"")) {
            throw new AssertionError("Tab from filled parameter should navigate to next parameter");
        }
    }
}