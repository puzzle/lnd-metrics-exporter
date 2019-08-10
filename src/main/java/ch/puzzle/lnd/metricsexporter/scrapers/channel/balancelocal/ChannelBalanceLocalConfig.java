package ch.puzzle.lnd.metricsexporter.scrapers.channel.balancelocal;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperConfigs;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.ParameterizedMetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.ChannelIdConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChannelBalanceLocalConfig {

    @Autowired
    public void registerConfigs(ParameterizedMetricScraperRegistry registry, Configs configs) {
        configs.forEach((configName, config) -> registry.register(
                configName,
                new ChannelBalanceLocalScraper(config.getChannelId())
        ));
    }

    @Component
    @ConfigurationProperties(prefix = "lnd.scrapers.channelbalancelocal")
    public static class Configs extends MetricScraperConfigs<ChannelIdConfig> {
    }
}
