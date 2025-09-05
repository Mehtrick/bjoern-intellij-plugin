package de.mehtrick.bjoern;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BjoernCompletionTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    public void testBDDKeywordCompletion() {
        // Test that BDD keywords are offered for completion
        BjoernCompletionContributor contributor = new BjoernCompletionContributor();
        assertNotNull("Completion contributor should not be null", contributor);
        
        // This is a basic test to ensure the contributor is instantiated correctly
        // More comprehensive testing would require setting up a mock completion environment
    }

    public void testFormattingModelBuilder() {
        // Test that the formatting model builder is properly instantiated
        BjoernFormattingModelBuilder builder = new BjoernFormattingModelBuilder();
        assertNotNull("Formatting model builder should not be null", builder);
    }
}