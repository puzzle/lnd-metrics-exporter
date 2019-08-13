package ch.puzzle.lnd.metricsexporter.scrapers.channel.active;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.MetricScraperConfigs;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.ParameterizedMetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.common.ChannelIdConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChannelActiveConfig {

    @Autowired
    public void registerConfigs(ParameterizedMetricScraperRegistry registry, Configs configs) {
        configs.forEach((configName, config) -> registry.register(
                configName,
                new ChannelActiveScraper(config.getChannelId())
        ));
    }

    @Component
    @ConfigurationProperties(prefix = "lnd.scrapers.channelactive")
    public static class Configs extends MetricScraperConfigs<ChannelIdConfig> {
    }
}
