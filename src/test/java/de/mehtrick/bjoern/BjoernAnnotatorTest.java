package de.mehtrick.bjoern;

public class BjoernAnnotatorTest {

    public void testBareHttpsUrlIsLink() {
        if (!BjoernAnnotator.isValidLink("https://example.com/path")) {
            throw new AssertionError("Bare https URL should be a valid link");
        }
    }

    public void testBareHttpUrlIsLink() {
        if (!BjoernAnnotator.isValidLink("http://example.com")) {
            throw new AssertionError("Bare http URL should be a valid link");
        }
    }

    public void testQuotedHttpsUrlIsLink() {
        if (!BjoernAnnotator.isValidLink("\"https://example.com/foo-spec\"")) {
            throw new AssertionError("Quoted https URL should be a valid link");
        }
    }

    public void testMarkdownLinkHttpsIsLink() {
        if (!BjoernAnnotator.isValidLink("\"[TICKET-123](https://example.com/TICKET-123)\"")) {
            throw new AssertionError("Quoted markdown link with https should be a valid link");
        }
    }

    public void testMarkdownLinkHttpIsLink() {
        if (!BjoernAnnotator.isValidLink("[Bug Fix](http://github.com/repo/issues/42)")) {
            throw new AssertionError("Markdown link with http should be a valid link");
        }
    }

    public void testPlainTicketRefIsNotLink() {
        if (BjoernAnnotator.isValidLink("\"TICKET-123\"")) {
            throw new AssertionError("Plain ticket reference should NOT be treated as a link");
        }
    }

    public void testPlainTextIsNotLink() {
        if (BjoernAnnotator.isValidLink("some plain text")) {
            throw new AssertionError("Plain text should NOT be treated as a link");
        }
    }

    public void testEmptyStringIsNotLink() {
        if (BjoernAnnotator.isValidLink("")) {
            throw new AssertionError("Empty string should NOT be treated as a link");
        }
    }

    public void testNullIsNotLink() {
        if (BjoernAnnotator.isValidLink(null)) {
            throw new AssertionError("Null should NOT be treated as a link");
        }
    }

    public void testMalformedMarkdownIsNotLink() {
        // Contains "](" but doesn't match full [text](url) pattern
        if (BjoernAnnotator.isValidLink("broken](https://example.com")) {
            throw new AssertionError("Malformed markdown link should NOT be treated as a valid link");
        }
    }

    public void testMarkdownWithFtpIsNotLink() {
        if (BjoernAnnotator.isValidLink("[text](ftp://example.com)")) {
            throw new AssertionError("Markdown link with ftp:// should NOT be treated as a valid link");
        }
    }

    public void testMarkdownWithNoProtocolIsNotLink() {
        if (BjoernAnnotator.isValidLink("[text](example.com)")) {
            throw new AssertionError("Markdown link without protocol should NOT be treated as a valid link");
        }
    }

    public void testPlainBracketsWithoutParenAreNotLink() {
        // "[note]" doesn't contain "](" so it's just plain text
        if (BjoernAnnotator.isValidLink("[note]")) {
            throw new AssertionError("Plain brackets without paren are not a link");
        }
    }
}
