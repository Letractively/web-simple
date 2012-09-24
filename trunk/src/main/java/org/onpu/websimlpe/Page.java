package org.onpu.websimlpe;

/**
 *  Copyright 2012 Lemeshev Andrey
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.onpu.websimlpe.common.Config;
import org.onpu.websimlpe.common.ContentType;
import org.onpu.websimlpe.common.HttpMethod;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Page class prototype
 */
public abstract class Page {
    private VelocityContext context = new VelocityContext();
    private String response = null;
    private Map<String, Object> session;
    private Config config;

    public Map<String, Object> getSession() {
        return session;
    }

    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    protected void addModel(String name, Object model) {
        context.put(name, model);
    }

    protected void doGet(Map<String, String> params) {
        render();
    }

    protected void doPost(Map<String, String> params) {
        render();
    }

    protected void render() {
        VelocityEngine engine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                ClasspathResourceLoader.class.getName());

        engine.init(properties);

        Template template = engine.getTemplate(getTemplateName());

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        response = writer.toString();
    }

    public abstract String getTemplateName();

    public abstract ContentType getContentType();

    public String invoke(HttpMethod method, Map<String, String> params) {
        if (method == HttpMethod.GET) {
            doGet(params);
        } else {
            doPost(params);
        }
        return response;
    }
}
