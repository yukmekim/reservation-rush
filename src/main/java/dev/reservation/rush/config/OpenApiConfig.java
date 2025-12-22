package dev.reservation.rush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean
      public OpenAPI openAPI() {
          return new OpenAPI()
                  .info(new Info()
                          .title("Reservation Rush API")
                          .description("Reservation Rush API Docs")      
                          .version("v1.0.0")
                          .contact(new Contact()
                                  .name("Developer YUK JANG HUN")
                                  .email("gyjj1243@gmail.com")))
                  .addServersItem(new Server()
                          .url("http://localhost:7010")
                          .description("로컬 개발 서버"))
                  .components(new Components());
      }
}
