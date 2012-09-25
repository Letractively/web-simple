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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import org.onpu.websimlpe.common.Config;
import org.onpu.websimlpe.common.ContentType;
import org.onpu.websimlpe.common.HttpMethod;
import org.onpu.websimlpe.common.HttpStatus;
import org.onpu.websimlpe.common.ReflectiveBuilder;
import org.onpu.websimlpe.common.SessionStore;
import org.onpu.websimlpe.defaults.DefaultPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is realized a functions of simple http-server
 */
public class Server {
    public static final String CHARSET_UTF_8_SUFIX = "; charset=utf-8";
    private Config config;
    private HttpServer server;
    private Logger log = Logger.getLogger(Server.class);
    private Map<String, Class<?>> routes = new HashMap<String, Class<?>>();
    private SessionStore sessions = new SessionStore();

    public static final int FILE_BUFFER_SIZE = 4 * 1024;
    public static final String ACCEPT_CHARSET_HEADER = "Accept-charset";
    public static final String CONTENT_LENGTH_HEADER = "Content-length";
    public static final String CONTENT_TYPE_HEADER = "Content-type";

    public Server() throws IOException {
        log.info("Initialization of server...");
        init();

        config = Config.getInstance();
        String port = config.get("server.port");
        log.info("Configuration was loaded.");

        if (port == null || !port.matches("^(\\d){2,4}$")) {
            log.error("Illegal server port in config.");
            throw new IllegalArgumentException("Illegal server port in config.");
        }

        server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
        server.createContext("/", new ServerHandler());
        server.setExecutor(null);
        server.start();
        log.info("Server was started successfully on port " + port);
    }

    /**
     * This method is turned off the server
     */
    public void stop() {
        server.stop(0);
        log.info("Server was stopped.");
    }

    /**
     * This is initialization method, you can to override it to add routes to your pages
     */
    public void init() {
        if (!routes.containsKey("/")) {
            addRoute("/", DefaultPage.class);
        }
    }

    /**
     * This method add new route
     * @param mask mask of url (for example: "/", "/some_page1", "/some_page2")
     * @param pageClass page class
     * For example {@link #init()}
     */
    protected void addRoute(String mask, Class<? extends Page> pageClass) {
        routes.put(mask, pageClass);
        log.info("Add route: " + mask + " => " + pageClass.getName());
    }

    /**
     * Request handler
     */
    private class ServerHandler implements HttpHandler {
        private Class<?> checkForMapping(String url) {
            return routes.get(url);
        }

        public Map<String, String> getQueryMap(String query) {
            if (query == null) {
                return Collections.emptyMap();
            }

            String[] params = query.split("&(?!#)");
            Map<String, String> map = new HashMap<String, String>();
            for (String param : params) {
                String[] cortage = param.split("=");
                String name = cortage[0];
                String value = cortage.length > 1 ? cortage[1] : null;
                map.put(name, value);
            }
            return map;
        }

        public void handle(HttpExchange context) throws IOException {
            URI uri = context.getRequestURI();
            Class<?> clazz = checkForMapping(uri.getPath());
            
            if (clazz == null) {
                InputStream source =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(uri.getPath().substring(1));

                if (source == null) {
                    log.debug("File not found:" + uri.getPath());
                    sendStringResponse(context, ContentType.PLAIN_TEXT, HttpStatus.HTTP_NOT_FOUND,
                            "File not found.");
                    return;
                }
                sendFile(context, source);
                return;
            }

            try {
                Page page = ReflectiveBuilder.<Page>build(clazz);
                page.setConfig(config);
                page.setSession(sessions.get(context.getRemoteAddress()));

                Map<String, String> params;
                String encode = context.getRequestHeaders().get(ACCEPT_CHARSET_HEADER).get(0);
                String query;
                if (HttpMethod.valueOf(context.getRequestMethod()) == HttpMethod.GET) {
                    query = uri.getQuery();
                } else {
                    byte[] buffer = new byte[Integer.parseInt(context.getRequestHeaders().get(CONTENT_LENGTH_HEADER).get(0))];
                    context.getRequestBody().read(buffer);
                    query = new URI("/?" + new String(buffer)).getQuery();
                }
                query = query == null ? "" : query;

                params = getQueryMap(URLDecoder.decode(query, encode));

                String response = page.invoke(HttpMethod.valueOf(context.getRequestMethod()), params);
                response = response == null ? "" : response;
                sendStringResponse(context, page.getContentType(), HttpStatus.HTTP_OK, response);
            } catch (Exception e) {
                log.error("Application error:", e);
                sendStringResponse(context, ContentType.PLAIN_TEXT, HttpStatus.HTTP_APPLICATION_ERROR,
                        e.toString());
            }
        }

        private void sendFile(HttpExchange context, InputStream source) throws IOException {
            try {
                context.sendResponseHeaders(HttpStatus.HTTP_OK.getStatus(), 0);
                OutputStream os = context.getResponseBody();
                try {
                    byte[] buffer = new byte[FILE_BUFFER_SIZE];
                    int readed;

                    while ((readed = source.read(buffer)) > 0) {
                        os.write(buffer, 0, readed);
                    }
                } finally {
                    os.close();
                    source.close();
                }
            } catch (Exception e) {
                log.error("Application error:", e);
                sendStringResponse(context, ContentType.PLAIN_TEXT, HttpStatus.HTTP_APPLICATION_ERROR,
                        e.toString());
            }
        }

        private void sendStringResponse(HttpExchange context, ContentType type, HttpStatus status, String response)
                throws IOException {
            OutputStream os = context.getResponseBody();
            try {
                context.getResponseHeaders().add(CONTENT_TYPE_HEADER, type.getType() + CHARSET_UTF_8_SUFIX);
                context.sendResponseHeaders(status.getStatus(), response.getBytes().length);
                os.write(response.getBytes());
            } finally {
                os.close();
            }
        }
    }
}
