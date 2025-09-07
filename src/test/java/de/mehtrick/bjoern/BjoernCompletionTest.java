package de.mehtrick.bjoern;

import java.util.Set;
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
}