package com.jeanbarcellos.project110.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Value("${app-config.name}")
    private String appName;

    @Value("${app-config.description}")
    private String appDescription;

    @Value("${app-config.version}")
    private String appVersion;

    @Bean
    OpenAPI openAPI() {

        var config = new OpenAPI();

        var info = new Info()
                .title(this.appName)
                .description(this.appDescription)
                .version(this.appVersion);

        info.contact(new Contact()
                .name("Jean Barcellos")
                .url("https://www.jeanbarcellos.com.br"));
        config.info(info);

        config.externalDocs(new ExternalDocumentation()
                .description("Jean Barcellos - Github")
                .url("https://github.com/jeanbarcellos"));

        return config;
    }

}
