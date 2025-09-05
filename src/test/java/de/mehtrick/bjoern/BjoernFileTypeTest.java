package de.mehtrick.bjoern;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BjoernFileTypeTest extends BasePlatformTestCase {

    public void testBjoernFileType() {
        // Test that .zgr files are recognized as Bjoern files
        assertNotNull("BjoernFileType should not be null", BjoernFileType.INSTANCE);
        assertEquals("Extension should be 'zgr'", "zgr", BjoernFileType.INSTANCE.getDefaultExtension());
        assertEquals("Name should be 'Bjoern File'", "Bjoern File", BjoernFileType.INSTANCE.getName());
        assertEquals("Description should mention Bjoern", "Bjoern BDD specification file", BjoernFileType.INSTANCE.getDescription());
    }

    public void testBjoernLanguage() {
        // Test that the language is properly defined
        assertNotNull("BjoernLanguage should not be null", BjoernLanguage.INSTANCE);
        assertEquals("Language ID should be 'Bjoern'", "Bjoern", BjoernLanguage.INSTANCE.getID());
    }
}