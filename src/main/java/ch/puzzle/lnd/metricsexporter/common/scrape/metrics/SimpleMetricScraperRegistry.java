package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SimpleMetricScraperRegistry implements MetricScraperRegistry, ApplicationListener<ContextRefreshedEvent> {

    private volatile Map<String, MetricScraper> scrapers;

    public SimpleMetricScraperRegistry() {
        scrapers = new HashMap<>();
    }

    @Override
    public MetricScraper find(String name) {
        return scrapers.get(name); // FIXME - > not found?
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        scrapers = context.getBeansOfType(MetricScraper.class)
                .values().stream()
                .collect(Collectors.toMap(MetricScraper::name, scraper -> scraper));
    }
}
