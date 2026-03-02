package de.mehtrick.bjoern;

public class BjoernBreadcrumbsProviderTest {

    public void testLanguagesContainYaml() {
        BjoernBreadcrumbsProvider provider = new BjoernBreadcrumbsProvider();
        com.intellij.lang.Language[] languages = provider.getLanguages();
        if (languages == null || languages.length == 0) {
            throw new AssertionError("Languages should not be empty");
        }
        boolean hasYaml = false;
        for (com.intellij.lang.Language lang : languages) {
            if ("YAML".equals(lang.getID())) {
                hasYaml = true;
                break;
            }
        }
        if (!hasYaml) {
            throw new AssertionError("Provider should handle YAML language (base of Bjoern)");
        }
    }
}
