package ch.puzzle.lnd.metricsexporter.registry;

import ch.puzzle.lnd.metricsexporter.common.config.ChannelIdentificationConfig;
import ch.puzzle.lnd.metricsexporter.common.config.LndConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.ChannelActiveScraper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelActiveScraperRegistry implements MetricScraperRegistry {

    private volatile Map<String, MetricScraper> scrapers;

    public ChannelActiveScraperRegistry(LndConfig lndConfig) {
        Map<String, ChannelIdentificationConfig> metricConfigs = lndConfig.getScrapers().getChannel_active();
        scrapers = new HashMap<>();
        metricConfigs.forEach((metricConfigName, metricConfig) -> {
            var scraper = new ChannelActiveScraper(metricConfig);
            scrapers.put(String.format("%s.%s", scraper.name(), metricConfigName), scraper);
        });
    }

    @Override
    public MetricScraper find(String name) {
        return scrapers.get(name); // FIXME - > not found?
    }
}
