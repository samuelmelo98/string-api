package org.stringtecnologia.string_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public StringTemplateResolver stringTemplateResolver() {

        StringTemplateResolver resolver = new StringTemplateResolver();

        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);
        resolver.setOrder(1);

        return resolver;
    }
}