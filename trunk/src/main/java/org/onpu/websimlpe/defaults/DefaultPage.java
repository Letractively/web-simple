package org.onpu.websimlpe.defaults;

import org.onpu.websimlpe.Page;
import org.onpu.websimlpe.common.ContentType;

import java.util.Map;

/**
 * Default page class
 */
public class DefaultPage extends Page {
    @Override
    protected void doGet(Map<String, String> params) {
        String name = params.get("name");
        if (name == null) {
            name = "student";
        }
        addModel("name", name);
        render();
    }

    @Override
    protected void doPost(Map<String, String> params) {
        doGet(params);
    }

    @Override
    public String getTemplateName() {
        return "default.vm";
    }

    @Override
    public ContentType getContentType() {
        return ContentType.HTML_TEXT;
    }
}
