package ch.puzzle.lnd.metricsexporter.scrapers.channel.balanceremote;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.MetricScraperConfigs;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.ParameterizedMetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.common.ChannelIdConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChannelBalanceRemoteConfig {

    @Autowired
    public void registerConfigs(ParameterizedMetricScraperRegistry registry, Configs configs) {
        configs.forEach((configName, config) -> registry.register(
                configName,
                new ChannelBalanceRemoteScraper(config.getChannelId())
        ));
    }

    @Component
    @ConfigurationProperties(prefix = "lnd.scrapers.channelbalanceremote")
    public static class Configs extends MetricScraperConfigs<ChannelIdConfig> {
    }
}
