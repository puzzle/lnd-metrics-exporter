package ch.puzzle.lnd.metricsexporter.scrapers.channel.routetest;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.MetricScraperConfigs;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.ParameterizedMetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.ChannelIdConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChannelRouteTestConfig {

    @Autowired
    public void registerConfigs(ParameterizedMetricScraperRegistry registry, Configs configs) {
        configs.forEach((configName, config) -> registry.register(
                configName,
                new ChannelRouteTestScraper(config.getChannelId(), config.getAmount())
        ));
    }

    @Component
    @ConfigurationProperties(prefix = "lnd.scrapers.channelroutetest")
    public static class Configs extends MetricScraperConfigs<Config> {
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class Config extends ChannelIdConfig {

        private int amount;
    }
}
