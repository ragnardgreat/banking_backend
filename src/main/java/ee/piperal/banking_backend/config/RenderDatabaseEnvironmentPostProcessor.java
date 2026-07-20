package ee.piperal.banking_backend.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                URI dbUri = new URI(databaseUrl);
                String host = dbUri.getHost();
                int port = dbUri.getPort() != -1 ? dbUri.getPort() : 5432;
                String database = dbUri.getPath().substring(1);
                
                String userInfo = dbUri.getUserInfo();
                String username = "postgres";
                String password = "";
                
                if (userInfo != null && !userInfo.isEmpty()) {
                    String[] parts = userInfo.split(":");
                    username = parts[0];
                    if (parts.length > 1) {
                        password = parts[1];
                    }
                }

                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                
                Map<String, Object> props = new HashMap<>();
                props.put("spring.datasource.url", jdbcUrl);
                props.put("spring.datasource.username", username);
                props.put("spring.datasource.password", password);
                
                environment.getPropertySources().addFirst(new MapPropertySource("render-database", props));
                
                System.out.println("Render DATABASE_URL parsed: " + jdbcUrl);
            } catch (Exception e) {
                System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
