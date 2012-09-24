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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is using for start http-server
 */
public class WebSimpleApp implements Runnable {
    private AtomicBoolean work = new AtomicBoolean(true);
    private Thread workerThread;
    private Server server;

    public WebSimpleApp(Server server) throws InterruptedException {
        this.server = server;
        workerThread = new Thread(this);
        workerThread.start();
        workerThread.join();
    }

    /**
     * This method is turned off the server
     */
    public void stop() {
        work.set(false);
    }

    @Override
    public void run() {
        while (work.get()) {}
        server.stop();
    }
}
