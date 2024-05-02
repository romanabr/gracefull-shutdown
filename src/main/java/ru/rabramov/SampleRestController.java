package ru.rabramov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleRestController {

    private static final Logger logger = LoggerFactory.getLogger(SampleRestController.class);

    @GetMapping("/test/{message}")
    public String sendMessage(@PathVariable String message) {
        logger.info("Health check invocation with param: {}", message);

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return message + " OK";
    }
}
