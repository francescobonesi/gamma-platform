package it.francesco.gamma.pec;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PecApplication {

    public static void main(String[] args) {
        SpringApplication.run(PecApplication.class, args);
    }

}