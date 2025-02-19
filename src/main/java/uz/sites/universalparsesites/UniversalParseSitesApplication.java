package uz.sites.universalparsesites;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // @Scheduled anotatsiyasini faollashtirish
public class UniversalParseSitesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniversalParseSitesApplication.class, args);
    }

}
