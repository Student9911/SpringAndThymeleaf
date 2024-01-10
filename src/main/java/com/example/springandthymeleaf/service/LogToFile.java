package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.LogEntry;
import com.example.springandthymeleaf.repository.LogEntryRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@Slf4j
@Service
public class LogToFile {
    private LogEntryRepository logEntryRepository;
    private LogService logService;


    public LogToFile(LogEntryRepository logEntryRepository, LogService logService) {
        this.logEntryRepository = logEntryRepository;

        this.logService = logService;
    }


    public String exportLogs(Principal principal) {
        log.info("connect -> exportLogs");

        // Логика для получения логов из базы данных H2
        List<LogEntry> logs = logEntryRepository.findAll();

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
            logService.saveLog("ERROR", "пользователь \n" +
                    "" + principal.getName() + "произошла ошибка при экспорте логов в файл");
            return "redirect:/logs?error";
        }


        try (FileWriter writer = new FileWriter(logPath)) {
            for (LogEntry log : logs) {
                writer.write(log.getId() + "| " + log.getTimeStamp() + "| " +
                        log.getLevel() + " | " + log.getMessage() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logService.saveLog("ERROR", "пользователь \n" +
                    "" + principal.getName() + "произошла ошибка при экспорте логов в файл" +
                    "не удалось создать файл в: " + logPath);
            return "redirect:/logs?error";
        }
        logService.saveLog("INFO", "пользователь: " + principal.getName() + " выгрузил логи в файл");

        return "redirect:/logs?success";
    }
}