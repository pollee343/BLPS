package app;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "app")
@EnableProcessApplication
// @EnableScheduling //для проверки неоплаченных незаблокированных обещ платежей
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
