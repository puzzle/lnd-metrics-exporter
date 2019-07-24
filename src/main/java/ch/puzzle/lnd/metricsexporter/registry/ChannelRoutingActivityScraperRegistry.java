package ch.puzzle.lnd.metricsexporter.registry;

import ch.puzzle.lnd.metricsexporter.common.config.ChannelRoutingActivityConfig;
import ch.puzzle.lnd.metricsexporter.common.config.LndConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.ChannelRoutingActivityScraper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelRoutingActivityScraperRegistry implements MetricScraperRegistry {

    private volatile Map<String, MetricScraper> scrapers;

    public ChannelRoutingActivityScraperRegistry(LndConfig lndConfig) {
        Map<String, ChannelRoutingActivityConfig> channelMetricConfigs = lndConfig.getScrapers().getChannel_routing_activity();
        scrapers = new HashMap<>();
        channelMetricConfigs.forEach((metricConfigName, metricConfig) -> {
            var scraper = new ChannelRoutingActivityScraper(metricConfig);
            scrapers.put(String.format("%s.%s", scraper.name(), metricConfigName), scraper);
        });
    }

    @Override
    public MetricScraper find(String name) {
        return scrapers.get(name); // FIXME - > not found?
    }
}
