package com.jeanbarcellos.project110.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jeanbarcellos.project110.properties.AppConfigProperties;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {

    private final AppConfigProperties appConfigProperties;

    @Bean
    OpenAPI openAPI() {

        var config = new OpenAPI();

        var info = new Info()
                .title(this.appConfigProperties.getName())
                .description(this.appConfigProperties.getDescription())
                .version(this.appConfigProperties.getVersion());

        info.contact(new Contact()
                .name(this.appConfigProperties.getContact().getName())
                .url(this.appConfigProperties.getContact().getUrl()));

        config.info(info);

        config.externalDocs(new ExternalDocumentation()
                .description("Jean Barcellos - Github")
                .url("https://github.com/jeanbarcellos"));

        return config;
    }

}
