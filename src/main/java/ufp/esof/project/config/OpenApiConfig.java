package ufp.esof.project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI customerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java 11 and Spring Boot 2.7.1")
                        .version("v1")
                        .description("Basic Implementation of Restful APIs")
                        .termsOfService("https://github.com/edsonwade/Software-Engineering-Project-From-University-Fernando-Pessoa.git")
                        .license(new License().name("Apache 2.0")
                                .url("https://github.com/edsonwade/Software-Engineering-Project-From-University-Fernando-Pessoa.git"))
                );
    }

}
