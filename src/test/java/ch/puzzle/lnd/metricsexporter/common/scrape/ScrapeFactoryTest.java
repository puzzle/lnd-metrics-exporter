package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.ApiFactory;
import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.InvalidScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry.LabelProviderRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry.NoSuchLabelProviderException;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.MetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.NoSuchMetricScraperException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScrapeFactoryTest {

    private static final String METRIC_SCRAPER_ONE_NAME = "metric-one";

    private static final String METRIC_SCRAPER_TWO_NAME = "metric-two";

    private static final String LABEL_PROVIDER_ONE_NAME = "label-one";

    private static final String LABEL_PROVIDER_TWO_NAME = "label-two";

    @Mock
    private MetricScraperRegistry metricScraperRegistry;

    @Mock
    private LabelProviderRegistry labelProviderRegistry;

    @Mock
    private ApiFactory apiFactory;

    @Mock
    private LndApi lndApi;

    @Mock
    private MetricScraper metricScraperOne;

    @Mock
    private MetricScraper metricScraperTwo;

    @Mock
    private LabelProvider labelProviderOne;

    @Mock
    private LabelProvider labelProviderTwo;

    private ScrapeConfig scrapeConfig;

    private ScrapeFactory factory;

    @Before
    public void setup() throws NoSuchMetricScraperException, NoSuchLabelProviderException {
        doReturn(lndApi).when(apiFactory).create(any());

        scrapeConfig = ScrapeConfig.builder()
                .host("localhost")
                .metricNames(List.of("metric-one", "metric-two"))
                .labelProviderNames(List.of("label-one", "label-two"))
                .build();

        doReturn(metricScraperOne).when(metricScraperRegistry).lookup(METRIC_SCRAPER_ONE_NAME);
        doReturn(metricScraperTwo).when(metricScraperRegistry).lookup(METRIC_SCRAPER_TWO_NAME);
        doReturn(labelProviderOne).when(labelProviderRegistry).lookup(LABEL_PROVIDER_ONE_NAME);
        doReturn(labelProviderTwo).when(labelProviderRegistry).lookup(LABEL_PROVIDER_TWO_NAME);

        factory = new ScrapeFactory(apiFactory, metricScraperRegistry, labelProviderRegistry);
    }

    @Test
    public void testSuccessfulCreate() throws InvalidScrapeConfigException {
        final var scrape = factory.create(scrapeConfig);

        assertNotNull(scrape.metricScraperExecutor);
        assertNotNull(scrape.labelProviderExecutor);
        assertNotNull(scrape.scrapeSuccessfulCollectorFactory);
        assertSame(lndApi, scrape.api);

        var metricScrapers = StreamSupport.stream(scrape.metricScraperExecutor.scrapers.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(2, metricScrapers.size());
        assertSame(metricScraperOne, metricScrapers.get(0));
        assertSame(metricScraperTwo, metricScrapers.get(1));

        var labelProviders = StreamSupport.stream(scrape.labelProviderExecutor.labelProviders.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(2, labelProviders.size());
        assertSame(labelProviderOne, labelProviders.get(0));
        assertSame(labelProviderTwo, labelProviders.get(1));
    }

    @Test(expected = InvalidScrapeConfigException.class)
    public void testMetricScraperRegistryFails() throws NoSuchMetricScraperException, InvalidScrapeConfigException {
        doThrow(new NoSuchMetricScraperException("")).when(metricScraperRegistry).lookup(anyString());

        factory.create(scrapeConfig);
    }

    @Test(expected = InvalidScrapeConfigException.class)
    public void testLabelProviderRegistryFails() throws NoSuchLabelProviderException, InvalidScrapeConfigException {
        doThrow(new NoSuchLabelProviderException("")).when(labelProviderRegistry).lookup(anyString());

        factory.create(scrapeConfig);
    }
}
