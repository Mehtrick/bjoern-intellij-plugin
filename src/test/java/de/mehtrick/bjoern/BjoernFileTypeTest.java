package de.mehtrick.bjoern;

public class BjoernFileTypeTest {

    public void testBjoernFileType() {
        // Test that .zgr files are recognized as Bjoern files
        if (BjoernFileType.INSTANCE == null) {
            throw new AssertionError("BjoernFileType should not be null");
        }
        if (!"zgr".equals(BjoernFileType.INSTANCE.getDefaultExtension())) {
            throw new AssertionError("Extension should be 'zgr'");
        }
        if (!"Bjoern File".equals(BjoernFileType.INSTANCE.getName())) {
            throw new AssertionError("Name should be 'Bjoern File'");
        }
        if (!"Bjoern BDD specification file".equals(BjoernFileType.INSTANCE.getDescription())) {
            throw new AssertionError("Description should mention Bjoern");
        }
        if (BjoernFileType.INSTANCE.getIcon() == null) {
            throw new AssertionError("Icon should not be null");
        }
    }

    public void testBjoernLanguage() {
        // Test that the language is properly defined
        if (BjoernLanguage.INSTANCE == null) {
            throw new AssertionError("BjoernLanguage should not be null");
        }
        if (!"Bjoern".equals(BjoernLanguage.INSTANCE.getID())) {
            throw new AssertionError("Language ID should be 'Bjoern'");
        }
    }
}