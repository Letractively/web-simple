package org.onpu.websimlpe.common;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used for saving users sessions
 */
public class SessionStore {
    private Map<InetSocketAddress, Map<String, Object>> sessions = new ConcurrentHashMap<InetSocketAddress, Map<String, Object>>();

    public Map<String, Object> get(InetSocketAddress clientAddress) {
        Map<String, Object> clientSession = sessions.get(clientAddress);
        if (clientSession == null) {
            clientSession = new ConcurrentHashMap<String, Object>();
            sessions.put(clientAddress, clientSession);
        }
        return clientSession;
    }
}
