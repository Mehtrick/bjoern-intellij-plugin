package de.mehtrick.bjoern;

public class BjoernStructureInspectionTest {

    public void testInspectionCanBeInstantiated() {
        BjoernStructureInspection inspection = new BjoernStructureInspection();
        if (inspection == null) {
            throw new AssertionError("Structure inspection should not be null");
        }
    }

    public void testValidDeprecatedValues() {
        if (!BjoernStructureInspection.isValidDeprecatedValue("true")) {
            throw new AssertionError("'true' should be a valid Deprecated value");
        }
        if (!BjoernStructureInspection.isValidDeprecatedValue("false")) {
            throw new AssertionError("'false' should be a valid Deprecated value");
        }
    }

    public void testInvalidDeprecatedValues() {
        if (BjoernStructureInspection.isValidDeprecatedValue("yes")) {
            throw new AssertionError("'yes' should not be a valid Deprecated value");
        }
        if (BjoernStructureInspection.isValidDeprecatedValue("1")) {
            throw new AssertionError("'1' should not be a valid Deprecated value");
        }
        if (BjoernStructureInspection.isValidDeprecatedValue("")) {
            throw new AssertionError("empty string should not be a valid Deprecated value");
        }
    }
}
