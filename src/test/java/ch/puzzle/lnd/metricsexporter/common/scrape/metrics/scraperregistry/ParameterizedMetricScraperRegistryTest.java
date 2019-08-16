package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ParameterizedMetricScraperRegistryTest {

    private static final String METRIC_SCRAPER_NAME = "metric-scraper";

    @Mock
    MetricScraper metricScraper;

    ParameterizedMetricScraperRegistry registry;

    @Before
    public void setup() {
        doReturn(METRIC_SCRAPER_NAME).when(metricScraper).name();

        registry = new ParameterizedMetricScraperRegistry();
    }

    @Test
    public void testMetricScraperExists() throws NoSuchMetricScraperException {
        var configName = "config";
        registry.register(configName, metricScraper);
        var found = registry.lookup(String.format("%s.%s", METRIC_SCRAPER_NAME, configName));
        assertSame(metricScraper, found);
    }

    @Test(expected = NoSuchMetricScraperException.class)
    public void testMetricScraperDoesNotExists() throws NoSuchMetricScraperException {
        registry.lookup("inexistent-metric-scraper");
    }

    @Test(expected = NoSuchMetricScraperException.class)
    public void testMetricScraperIsNotFoundByNameOnly() throws NoSuchMetricScraperException {
        registry.lookup(METRIC_SCRAPER_NAME);
    }
}
