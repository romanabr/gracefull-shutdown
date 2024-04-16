package ru.rabramov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("--- start test app");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("--- shutdown hook detected")));

        SpringApplication.run(Application.class, args);
    }
}


