package de.mehtrick.bjoern;

public class BjoernAnnotatorTest {

    public void testMarkdownLinkHttpsIsLink() {
        if (!BjoernAnnotator.isValidMarkdownLink("\"[TICKET-123](https://example.com/TICKET-123)\"")) {
            throw new AssertionError("Quoted markdown link with https should be a valid markdown link");
        }
    }

    public void testMarkdownLinkHttpIsLink() {
        if (!BjoernAnnotator.isValidMarkdownLink("[Bug Fix](http://github.com/repo/issues/42)")) {
            throw new AssertionError("Markdown link with http should be a valid markdown link");
        }
    }

    public void testUnquotedMarkdownLinkIsLink() {
        if (!BjoernAnnotator.isValidMarkdownLink("[AUTH-100](https://example.com/issues/AUTH-100)")) {
            throw new AssertionError("Unquoted markdown link with https should be a valid markdown link");
        }
    }

    // Bare URLs are intentionally NOT highlighted (markdown-link only)
    public void testBareHttpsUrlIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("https://example.com/path")) {
            throw new AssertionError("Bare https URL should NOT be treated as a markdown link");
        }
    }

    public void testBareHttpUrlIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("http://example.com")) {
            throw new AssertionError("Bare http URL should NOT be treated as a markdown link");
        }
    }

    public void testQuotedBareHttpsUrlIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("\"https://example.com/foo-spec\"")) {
            throw new AssertionError("Quoted bare https URL should NOT be treated as a markdown link");
        }
    }

    public void testPlainTicketRefIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("\"TICKET-123\"")) {
            throw new AssertionError("Plain ticket reference should NOT be treated as a markdown link");
        }
    }

    public void testPlainTextIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("some plain text")) {
            throw new AssertionError("Plain text should NOT be treated as a markdown link");
        }
    }

    public void testEmptyStringIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink("")) {
            throw new AssertionError("Empty string should NOT be treated as a markdown link");
        }
    }

    public void testNullIsNotMarkdownLink() {
        if (BjoernAnnotator.isValidMarkdownLink(null)) {
            throw new AssertionError("Null should NOT be treated as a markdown link");
        }
    }

    public void testMalformedMarkdownIsNotLink() {
        // Contains "](" but doesn't match full [text](url) pattern
        if (BjoernAnnotator.isValidMarkdownLink("broken](https://example.com")) {
            throw new AssertionError("Malformed markdown link should NOT be treated as a valid markdown link");
        }
    }

    public void testMarkdownWithFtpIsNotLink() {
        if (BjoernAnnotator.isValidMarkdownLink("[text](ftp://example.com)")) {
            throw new AssertionError("Markdown link with ftp:// should NOT be treated as a valid markdown link");
        }
    }

    public void testMarkdownWithNoProtocolIsNotLink() {
        if (BjoernAnnotator.isValidMarkdownLink("[text](example.com)")) {
            throw new AssertionError("Markdown link without protocol should NOT be treated as a valid markdown link");
        }
    }

    public void testPlainBracketsWithoutParenAreNotLink() {
        // "[note]" doesn't contain "](" so it's just plain text
        if (BjoernAnnotator.isValidMarkdownLink("[note]")) {
            throw new AssertionError("Plain brackets without paren are not a markdown link");
        }
    }
}
