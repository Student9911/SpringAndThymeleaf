package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.LogEntry;
import com.example.springandthymeleaf.repository.LogEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class LogToFile {
    private LogEntryRepository logEntryRepository;

    public LogToFile(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }


    public void exportLogs() {
        log.info("connect -> exportLogs");

        // Логика для получения логов из базы данных H2
        List<LogEntry> logs = logEntryRepository.findAll(); // предположим, что у вас есть метод findAll() для получения всех логов

        // Логика для сохранения логов в файл
        String logPath = "C:/Users/aurakhov/IdeaProjects/SpringAndThymeleaf/Logs/logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        File logFile = new File(logPath);

        // Проверяем, существует ли папка для логов, и создаем ее, если она отсутствует
        File logFolder = logFile.getParentFile();
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }

        // Проверяем, существует ли файл для логов, и создаем его, если он отсутствует
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        try (FileWriter writer = new FileWriter(logPath)) {
            for (LogEntry log : logs) {
                writer.write(log.getId() + "| " + log.getTimeStamp() + "| " +
                        log.getLevel() + " | " + log.getMessage() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}