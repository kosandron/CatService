package org.kosandron;

import org.kosandron.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"org.kosandron"})
@EnableConfigurationProperties({ApplicationConfig.class})
@EnableJpaRepositories(basePackages = "org.kosandron")
@EnableKafka
public class OwnerApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(OwnerApplication.class, args);

    }
}
