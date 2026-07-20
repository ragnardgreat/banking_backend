package ee.piperal.banking_backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.net.URI;

@Configuration
@EnableConfigurationProperties
public class DatabaseConfig {

    @PostConstruct
    public void parseRenderDatabaseUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                URI dbUri = new URI(databaseUrl);
                String host = dbUri.getHost();
                int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432;
                String database = dbUri.getPath().substring(1);
                String username = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":")[0] : "postgres";
                String password = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":")[1] : "";

                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                System.setProperty("spring.datasource.url", jdbcUrl);
                System.setProperty("spring.datasource.username", username);
                System.setProperty("spring.datasource.password", password);
            } catch (Exception e) {
                System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
            }
        }
    }
}
