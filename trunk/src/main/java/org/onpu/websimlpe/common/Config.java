package org.onpu.websimlpe.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used for to load properties of configuration
 */
public class Config {
    public static final String CONFIG_FILE_NAME = "config.properties";
    private static Config instance;
    private static Properties properties = new Properties();
    private static Logger log = Logger.getLogger(Config.class);

    private Config() throws IOException {
        try {
            InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
            properties.load(source);
        } catch (IOException e) {
            log.error("Error during initialize Config", e);
            throw e;
        }
    }

    public static Config getInstance() throws IOException {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    /**
     * Return single property by name
     * @param name name of property
     * @return return single property by name or null if property is not defined
     */
    public String get(String name) {
        return properties.getProperty(name);
    }
}
