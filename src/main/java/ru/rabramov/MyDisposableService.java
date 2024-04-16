package ru.rabramov;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class MyDisposableService implements DisposableBean, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MyDisposableService.class);

    @Override
    public void afterPropertiesSet() {
        logger.info("--- MyService.init");
    }

    @Override
    public void destroy() {
        logger.info("--- MyService.destroy");
    }
}
