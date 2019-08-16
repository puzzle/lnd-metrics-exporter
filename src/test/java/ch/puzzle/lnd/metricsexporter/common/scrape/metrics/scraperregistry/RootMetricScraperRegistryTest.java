package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RootMetricScraperRegistryTest {

    private static final String METRIC_SCRAPER_ONE_NAME = "metric-scraper-one";

    private static final String METRIC_SCRAPER_TWO_NAME = "metric-scraper-two";

    private static final String METRIC_SCRAPER_BOTH_NAME = "metric-scraper";

    @Mock
    private MetricScraper metricScraperOne;

    @Mock
    private MetricScraper metricScraperTwo;

    @Mock
    private MetricScraper metricScraperBoth;

    @Mock
    private MetricScraperRegistry registryOne;

    @Mock
    private MetricScraperRegistry registryTwo;

    private RootMetricScraperRegistry registry;

    @Before
    public void setup() throws NoSuchMetricScraperException {
        doThrow(new NoSuchMetricScraperException("")).when(registryOne).lookup(anyString());
        doReturn(metricScraperOne).when(registryOne).lookup(METRIC_SCRAPER_ONE_NAME);
        doReturn(metricScraperBoth).when(registryOne).lookup(METRIC_SCRAPER_BOTH_NAME);

        doThrow(new NoSuchMetricScraperException("")).when(registryTwo).lookup(anyString());
        doReturn(metricScraperTwo).when(registryTwo).lookup(METRIC_SCRAPER_TWO_NAME);
        doReturn(metricScraperBoth).when(registryTwo).lookup(METRIC_SCRAPER_BOTH_NAME);

        registry = new RootMetricScraperRegistry(List.of(registryOne, registryTwo));
    }

    @Test
    public void testMetricScraperExists() throws NoSuchMetricScraperException {
        final var foundOne = registry.lookup(METRIC_SCRAPER_ONE_NAME);
        assertSame(metricScraperOne, foundOne);
        verify(registryOne).lookup(METRIC_SCRAPER_ONE_NAME);
        verify(registryTwo, never()).lookup(METRIC_SCRAPER_ONE_NAME);

        final var foundTwo = registry.lookup(METRIC_SCRAPER_TWO_NAME);
        assertSame(metricScraperTwo, foundTwo);
        verify(registryOne).lookup(METRIC_SCRAPER_TWO_NAME);
        verify(registryTwo).lookup(METRIC_SCRAPER_TWO_NAME);
    }

    @Test(expected = NoSuchMetricScraperException.class)
    public void testMetricScraperDoesNotExists() throws NoSuchMetricScraperException {
        final var name = "inexistent-metric-scraper";
        try {
            registry.lookup(name);
        } finally {
            verify(registryOne).lookup(name);
            verify(registryTwo).lookup(name);
        }
    }

    @Test
    public void testFirstResultIsUsedIfMetricSraperIsKnownByMultipleRegistries() throws NoSuchMetricScraperException {
        final var found = registry.lookup(METRIC_SCRAPER_BOTH_NAME);
        assertSame(metricScraperBoth, found);
        verify(registryOne).lookup(METRIC_SCRAPER_BOTH_NAME);
        verify(registryTwo, never()).lookup(METRIC_SCRAPER_BOTH_NAME);
        assertSame(metricScraperBoth, registryOne.lookup(METRIC_SCRAPER_BOTH_NAME));
        assertSame(metricScraperBoth, registryTwo.lookup(METRIC_SCRAPER_BOTH_NAME));
    }
}
