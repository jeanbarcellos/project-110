package com.jeanbarcellos.core.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtils {

    private ThreadUtils() {
    }

    public static void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

}
