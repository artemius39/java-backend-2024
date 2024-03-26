package edu.java.scrapper.controller;

import edu.java.scrapper.service.Updater;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@EnableScheduling
@Log4j2
@AllArgsConstructor
public class LinkUpdaterScheduler {
    private final Updater updater;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        int updateCount = updater.update();
        log.info("made " + updateCount + " update(s)");
    }
}
