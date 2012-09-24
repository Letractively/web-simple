package org.onpu.websimlpe.common;

/**
 * Supported HTTP content types enum
 */
public enum ContentType {
    PLAIN_TEXT ("text/plain"),
    HTML_TEXT  ("text/html");

    private String type;

    private ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ContentType{" +
                "type='" + type + '\'' +
                '}';
    }
}
