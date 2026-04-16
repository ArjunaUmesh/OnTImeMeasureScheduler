package org.scheduler.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private final String logFilePath;

    public Logger(String fileName) {
        // This ensures the logs go into the 'target' folder of your project
        this.logFilePath = "target/" + fileName + ".log";
    }

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("FATAL: Could not write to debug file: " + e.getMessage());
        }
    }
}