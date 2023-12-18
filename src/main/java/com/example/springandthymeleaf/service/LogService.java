package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.LogEntry;
import com.example.springandthymeleaf.repository.LogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final LogEntryRepository logEntryRepository;

    public LogService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public void saveLog(String level, String message) {
        LogEntry logEntry = new LogEntry();
        logEntry.setTimeStamp(LocalDateTime.now());
        logEntry.setLevel(level);
        logEntry.setMessage(message);

        logEntryRepository.save(logEntry);
    }
}
