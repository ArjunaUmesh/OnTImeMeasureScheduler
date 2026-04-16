package org.scheduler.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String logFilePath;

    public Logger(String fileName) {
        this.logFilePath = "target/execution_" + DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".log";
    }

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("FATAL: Could not write to debug file: " + e.getMessage());
        }
    }
}