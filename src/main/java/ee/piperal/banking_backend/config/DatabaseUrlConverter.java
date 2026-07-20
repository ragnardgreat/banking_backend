package ee.piperal.banking_backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties
public class DatabaseUrlConverter {

    @PostConstruct
    public void convertDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            // Convert postgres://user:pass@host:port/db to jdbc:postgresql://host:port/db
            String jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            System.setProperty("spring.datasource.url", jdbcUrl);
        }
    }
}
