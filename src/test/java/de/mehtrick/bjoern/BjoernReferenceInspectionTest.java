package de.mehtrick.bjoern;

public class BjoernReferenceInspectionTest {

    public void testValidHttpUrl() {
        if (!BjoernReferenceInspection.isValidReferenceValue("\"https://example.com/TICKET-123\"")) {
            throw new AssertionError("https URL should be valid");
        }
    }

    public void testValidHttpUrlNoQuotes() {
        if (!BjoernReferenceInspection.isValidReferenceValue("https://example.com/path")) {
            throw new AssertionError("Unquoted https URL should be valid");
        }
    }

    public void testValidMarkdownLink() {
        if (!BjoernReferenceInspection.isValidReferenceValue("\"[TICKET-123](https://example.com/TICKET-123)\"")) {
            throw new AssertionError("Valid markdown link with https should be valid");
        }
    }

    public void testValidMarkdownLinkHttp() {
        if (!BjoernReferenceInspection.isValidReferenceValue("[Bug Fix](http://github.com/repo/issues/42)")) {
            throw new AssertionError("Valid markdown link with http should be valid");
        }
    }

    public void testPlainTextNoUrl() {
        if (!BjoernReferenceInspection.isValidReferenceValue("\"TICKET-123\"")) {
            throw new AssertionError("Plain text reference (no URL) should be valid");
        }
    }

    public void testEmptyValue() {
        if (!BjoernReferenceInspection.isValidReferenceValue("")) {
            throw new AssertionError("Empty reference should be valid");
        }
    }

    public void testNullValue() {
        if (!BjoernReferenceInspection.isValidReferenceValue(null)) {
            throw new AssertionError("Null reference should be valid");
        }
    }

    public void testInvalidFtpScheme() {
        if (BjoernReferenceInspection.isValidReferenceValue("ftp://example.com/file")) {
            throw new AssertionError("ftp:// URL scheme should be invalid");
        }
    }

    public void testInvalidFileScheme() {
        if (BjoernReferenceInspection.isValidReferenceValue("file:///etc/passwd")) {
            throw new AssertionError("file:// URL scheme should be invalid");
        }
    }

    public void testInvalidMarkdownLinkBadScheme() {
        if (BjoernReferenceInspection.isValidReferenceValue("[text](ftp://example.com)")) {
            throw new AssertionError("Markdown link with ftp:// should be invalid");
        }
    }

    public void testInvalidMarkdownFormat() {
        // Contains "](" but not in valid [text](url) format
        if (BjoernReferenceInspection.isValidReferenceValue("broken](https://example.com")) {
            throw new AssertionError("Malformed markdown link should be invalid");
        }
    }

    public void testPlainTextWithBracketsIsValid() {
        // Plain brackets without "](" are treated as plain text and should be valid
        if (!BjoernReferenceInspection.isValidReferenceValue("[note]")) {
            throw new AssertionError("Plain text containing brackets (no markdown link) should be valid");
        }
    }

    public void testValidHtmlLinkNoUrlSchemeCheck() {
        // HTML anchor format without brackets - treated as plain text with URL check
        if (!BjoernReferenceInspection.isValidReferenceValue("\"<a href=\\\"https://example.com\\\">text</a>\"")) {
            throw new AssertionError("HTML anchor with https should be valid");
        }
    }
}
