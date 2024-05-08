package ru.rabramov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimedRestController {

    private static final Logger logger = LoggerFactory.getLogger(TimedRestController.class);

    @GetMapping("/test/{time}")
    public String sendMessage(@PathVariable Long time) {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Health check invocation with param: {}", time);

        return time + " OK";
    }
}
