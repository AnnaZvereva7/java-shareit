package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;

@SpringBootApplication
@PropertySource(value = "classpath:application.properties")
public class ShareItServer {

    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
