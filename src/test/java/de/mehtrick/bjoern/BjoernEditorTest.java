package de.mehtrick.bjoern;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLSequence;

import java.util.List;
import java.util.Objects;

public class BjoernEditorTest extends BasePlatformTestCase {
    private static final String VALID_SPEC = """
            Feature: Checkout
            Scenarios:
              - Scenario: Buy a drink
                Deprecated: false
                Given:
                  - A customer named "Ada"
                When:
                  - The customer buys a drink
                Then:
                  - The order is confirmed
            """;

    public void testSpecificationParsesIntoYamlScenarios() {
        myFixture.configureByText("checkout.zgr", VALID_SPEC);

        assertInstanceOf(myFixture.getFile(), BjoernFile.class);
        assertEmpty(PsiTreeUtil.findChildrenOfType(myFixture.getFile(), PsiErrorElement.class));
        YAMLFile file = (YAMLFile) myFixture.getFile();
        YAMLMapping feature = assertInstanceOf(file.getDocuments().getFirst().getTopLevelValue(), YAMLMapping.class);
        assertEquals("Checkout", feature.getKeyValueByKey("Feature").getValueText());
        YAMLSequence scenarios = assertInstanceOf(feature.getKeyValueByKey("Scenarios").getValue(), YAMLSequence.class);
        assertSize(1, scenarios.getItems());
    }

    public void testHighlightingAcceptsValidScenarioAndDeprecatedField() {
        myFixture.enableInspections(new BjoernStructureInspection());
        myFixture.configureByText("checkout.zgr", VALID_SPEC);

        assertEmpty(problemDescriptions());
    }

    public void testDeprecatedAtTopLevelIsReported() {
        myFixture.enableInspections(new BjoernStructureInspection());
        myFixture.configureByText("checkout.zgr", "Deprecated: true\n" + VALID_SPEC);

        assertContainsElements(problemDescriptions(), "'Deprecated:' is only allowed inside a scenario");
    }

    public void testDuplicateScenariosAreReported() {
        myFixture.enableInspections(new BjoernStructureInspection());
        myFixture.configureByText("checkout.zgr", VALID_SPEC + "  - Scenario: Buy a drink\n");

        assertContainsElements(problemDescriptions(), "Duplicate scenario name: 'Buy a drink'");
    }

    public void testInvalidReferenceIsReportedInEditor() {
        myFixture.enableInspections(new BjoernReferenceInspection());
        myFixture.configureByText("checkout.zgr", VALID_SPEC.replace("Feature: Checkout", "Feature: Checkout\nReference: ftp://example.com/ticket"));

        assertFalse("The editor should report an invalid Reference", problemDescriptions().isEmpty());
    }

    private List<String> problemDescriptions() {
        return myFixture.doHighlighting().stream()
                .map(HighlightInfo::getDescription)
                .filter(Objects::nonNull)
                .toList();
    }
}
