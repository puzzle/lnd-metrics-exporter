package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BeanMetricScraperRegistryTest {

    private static final String EXISTING_METRIC_SCRAPER = "metric-scraper";

    @Mock
    ApplicationContext context;

    @Mock
    MetricScraper metricScraper;

    BeanMetricScraperRegistry registry;

    @Before
    public void setup() {
        doReturn(EXISTING_METRIC_SCRAPER).when(metricScraper).name();
        doReturn(Map.of("bean", metricScraper)).when(context).getBeansOfType(MetricScraper.class);

        registry = new BeanMetricScraperRegistry();
        registry.onApplicationEvent(new ContextRefreshedEvent(context));
    }

    @Test
    public void testMetricScraperExists() throws NoSuchMetricScraperException {
        var found = registry.lookup(EXISTING_METRIC_SCRAPER);
        assertSame(metricScraper, found);
    }

    @Test(expected = NoSuchMetricScraperException.class)
    public void testMetricScraperDoesNotExists() throws NoSuchMetricScraperException {
        registry.lookup("inexistent-metric-scraper");
    }

}
