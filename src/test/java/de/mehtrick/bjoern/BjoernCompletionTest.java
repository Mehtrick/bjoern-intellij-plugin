package de.mehtrick.bjoern;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BjoernCompletionTest extends BasePlatformTestCase {
    public void testKeywordCompletionWithTypedPrefix() {
        myFixture.configureByText("checkout.zgr", "Feature: Checkout\nSc<caret>");
        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "Scenario:", "Scenarios:");
    }

    public void testDeprecatedValueCompletion() {
        myFixture.configureByText("checkout.zgr", """
                Feature: Checkout
                Scenarios:
                  - Scenario: Buy a drink
                    Deprecated: <caret>
                """);
        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(), "true", "false");
    }

    public void testStatementsFromAnotherFileWithoutTrailingNewline() {
        myFixture.addFileToProject("other.zgr", """
                Feature: Inventory
                Scenarios:
                  - Scenario: Stock
                    Given:
                      - There are "2" bottles of wine
                      - A customer "Ada \\"Lovelace\\"" exists # note
                      - There are "3" bottles of beer""");
        myFixture.configureByText("checkout.zgr", """
                Feature: Checkout
                Scenarios:
                  - Scenario: Buy
                    Given:
                      - <caret>
                """);
        myFixture.completeBasic();

        assertNotNull(myFixture.getLookupElementStrings());
        assertContainsElements(myFixture.getLookupElementStrings(),
                "There are \"\" bottles of wine", "A customer \"\" exists", "There are \"\" bottles of beer");
    }
}
