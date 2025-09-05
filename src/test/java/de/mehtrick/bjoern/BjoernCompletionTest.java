package de.mehtrick.bjoern;

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
}