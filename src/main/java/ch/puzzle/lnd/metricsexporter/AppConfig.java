package ch.puzzle.lnd.metricsexporter;

import ch.puzzle.lnd.metricsexporter.common.config.LndConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    @ConfigurationProperties(prefix = "lnd")
    public LndConfig config() {
        return new LndConfig();
    }

    @Autowired
    public void init(LndConfig lndConfig) {
        System.out.println(lndConfig);
    }

}
