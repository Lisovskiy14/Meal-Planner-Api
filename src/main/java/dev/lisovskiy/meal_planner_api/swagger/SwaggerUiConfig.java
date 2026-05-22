package dev.lisovskiy.meal_planner_api.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerUiConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController(
                "/swagger-ui",
                "/webjars/swagger-ui/index.html?url=/openapi3.yaml"
        );
        registry.addRedirectViewController(
                "/api-docs",
                "/openapi3.yaml"
        );
    }
}
