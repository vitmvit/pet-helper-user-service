package by.vitikova.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

    //todo настроить эврику и кеширование
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}