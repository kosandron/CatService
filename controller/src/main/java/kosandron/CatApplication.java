package kosandron;

import config.ApplicationConfig;
import kosandron.enums.Color;
import kosandron.services.jpaservices.JpaOwnerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import kosandron.services.jpaservices.JpaCatService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;

@SpringBootApplication(scanBasePackages = {"kosandron"})
@EnableConfigurationProperties({ApplicationConfig.class})
@EnableJpaRepositories(basePackages = "kosandron")
public class CatApplication {
    public static void main(String[] args) {
        var context = SpringApplication.run(CatApplication.class, args);
        //var service = context.getBean(JpaOwnerService.class);
    }
}
