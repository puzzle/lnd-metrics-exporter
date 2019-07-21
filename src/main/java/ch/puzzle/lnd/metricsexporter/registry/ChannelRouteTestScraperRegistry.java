package ch.puzzle.lnd.metricsexporter.registry;

import ch.puzzle.lnd.metricsexporter.common.config.ChannelMetricConfig;
import ch.puzzle.lnd.metricsexporter.common.config.LndConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.scrapers.ChannelRouteTestScraper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelRouteTestScraperRegistry implements MetricScraperRegistry {

    private volatile Map<String, MetricScraper> scrapers;

    public ChannelRouteTestScraperRegistry(LndConfig lndConfig) {
        Map<String, ChannelMetricConfig> channelMetricConfigs = lndConfig.getScrapers().getChannel_route_test();
        scrapers = new HashMap<>();
        channelMetricConfigs.forEach((metricConfigName, channelMetricConfig) -> {
            var scraper = new ChannelRouteTestScraper(metricConfigName, channelMetricConfig);
            scrapers.put(String.format("%s.%s", scraper.name(), metricConfigName), scraper);
        });
    }

    @Override
    public MetricScraper find(String name) {
        return scrapers.get(name); // FIXME - > not found?
    }
}
