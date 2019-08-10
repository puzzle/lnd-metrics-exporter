package ch.puzzle.lnd.metricsexporter.scrapers.channel.routingactivity;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperConfigs;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.ParameterizedMetricScraperRegistry;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class RoutingActivityConfig {

    @Autowired
    public void registerConfigs(ParameterizedMetricScraperRegistry registry, Configs configs) {
        configs.forEach((configName, config) -> registry.register(
                configName,
                new ChannelRoutingActivityScraper(config.getHistoryRangeSec())
        ));
    }

    @Component
    @ConfigurationProperties(prefix = "lnd.scrapers.channelroutingactivity")
    public static class Configs extends MetricScraperConfigs<Config> {
    }

    @Data
    private static class Config {

        private int historyRangeSec;
    }
}
