package org.devzen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    public void run(String... strings) throws Exception {
        log.info("Joining thread, you can press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }
}
