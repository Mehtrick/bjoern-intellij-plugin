package de.mehtrick.bjoern;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Arrays;

public class BjoernBreadcrumbsProviderTest extends BasePlatformTestCase {
    public void testLanguagesContainYaml() {
        assertContainsElements(Arrays.asList(new BjoernBreadcrumbsProvider().getLanguages()), YAMLLanguage.INSTANCE);
    }

    public void testDeprecatedScenarioLabel() {
        myFixture.configureByText("checkout.zgr", """
                Feature: Checkout
                Scenarios:
                  - Scenario: Old checkout
                    Deprecated: true
                """);
        YAMLKeyValue scenario = PsiTreeUtil.findChildrenOfType(myFixture.getFile(), YAMLKeyValue.class).stream()
                .filter(key -> "Scenario".equals(key.getKeyText())).findFirst().orElseThrow();
        BjoernBreadcrumbsProvider provider = new BjoernBreadcrumbsProvider();

        assertTrue(provider.acceptElement(scenario));
        assertEquals("Scenario: Old checkout (deprecated)", provider.getElementInfo(scenario));
    }
}
