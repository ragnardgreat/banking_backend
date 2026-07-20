package ee.piperal.banking_backend.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbPassword = environment.getProperty("POSTGRES_PASSWORD");
        if (dbPassword != null && !dbPassword.isEmpty()) {
            System.out.println("POSTGRES_PASSWORD environment variable found, using Render database");
        }
    }
}
