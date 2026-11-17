package de.mehtrick.bjoern;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BjoernTabHandlerTest extends BasePlatformTestCase {
    public void testTabSelectsNextParameterAndWraps() {
        myFixture.configureByText("checkout.zgr", """
                Feature: Checkout
                Scenarios:
                  - Scenario: Buy
                    Given:
                      - A customer "<caret>Ada" with "premium" access
                """);
        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_TAB);
        assertEquals("premium", myFixture.getEditor().getSelectionModel().getSelectedText());

        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_TAB);
        assertEquals("Ada", myFixture.getEditor().getSelectionModel().getSelectedText());
    }
}
