package org.cmpe295.meterimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.cmpe295.meterimage", "org.cmpe295.user"})
public class MeterImagesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeterImagesApplication.class, args);
    }
}
