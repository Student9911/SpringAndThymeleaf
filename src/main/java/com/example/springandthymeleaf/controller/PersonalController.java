package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.repository.LogEntryRepository;
import com.example.springandthymeleaf.service.LogToFile;
import com.example.springandthymeleaf.service.Persons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Slf4j
@Controller
public class PersonalController {
    public PersonalController(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    private LogEntryRepository logEntryRepository;





    @GetMapping("/log")
    public String getPeople(Model model)  {
        LogToFile log = new LogToFile(logEntryRepository);
        log.exportLogs();
        model.addAttribute("something", "hello in controller");
        model.addAttribute("people", Arrays.asList(
                new Persons("John", 20),
                new Persons("Sarah", 21),
                new Persons("Simon", 22)
        ));
        return "ListPeople";
    }

    @GetMapping("/logs")
    public ModelAndView getAllLogs() {
        log.info(("/Logs -> connection"));
        ModelAndView mav = new ModelAndView("list-logs");
        mav.addObject("logs", logEntryRepository.findAll());
        return mav;
    }
}
