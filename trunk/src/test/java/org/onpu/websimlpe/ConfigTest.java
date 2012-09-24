package org.onpu.websimlpe;

import junit.framework.TestCase;
import org.onpu.websimlpe.common.Config;

public class ConfigTest extends TestCase {
    public void test() throws  Exception {
        Config config = Config.getInstance();
        assertEquals(config.get("server.port"), "8000");
        assertNull(config.get("incorrect.key"));
    }
}
