package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricScraperExecutorTest {

    private static final String METRIC_SCRAPER_ONE_NAME = "metric_scraper_one";

    private static final String METRIC_SCRAPER_TWO_NAME = "metric_scraper_two";

    private static final int METRIC_SCRAPER_ONE_VALUE = 1;

    private static final int METRIC_SCRAPER_TWO_VALUE = 2;

    @Mock
    private MetricScraper<?> metricScraperOne;

    @Mock
    private MetricScraper<?> metricScraperTwo;

    @Mock
    private LndApi lndApi;

    @Mock
    private ExecutorService executorService;

    private MetricScraperExecutor executor;

    @Before
    public void setup() throws Exception {
        final var measurementOne = Counter.create().value(METRIC_SCRAPER_ONE_VALUE);
        final var measurementTwo = Counter.create().value(METRIC_SCRAPER_TWO_VALUE);

        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(executorService).submit(any(Runnable.class));
        doReturn(METRIC_SCRAPER_ONE_NAME).when(metricScraperOne).name();
        doReturn(METRIC_SCRAPER_ONE_NAME).when(metricScraperOne).description();
        doReturn(METRIC_SCRAPER_TWO_NAME).when(metricScraperTwo).name();
        doReturn(METRIC_SCRAPER_TWO_NAME).when(metricScraperTwo).description();
        doReturn(measurementOne).when(metricScraperOne).scrape(same(lndApi));
        doReturn(measurementTwo).when(metricScraperTwo).scrape(same(lndApi));

        executor = new MetricScraperExecutor(List.of(metricScraperOne, metricScraperTwo), lndApi);
    }

    @Test
    public void testMetricScrapersAreExecuted() throws Exception {
        executor.execute(executorService);
        var registry = executor.collect(Labels.create());

        assertEquals(METRIC_SCRAPER_ONE_VALUE, registry.getSampleValue(METRIC_SCRAPER_ONE_NAME).intValue());
        assertEquals(METRIC_SCRAPER_TWO_VALUE, registry.getSampleValue(METRIC_SCRAPER_TWO_NAME).intValue());

        verify(metricScraperOne).scrape(same(lndApi));
        verify(metricScraperTwo).scrape(same(lndApi));
        assertFalse(executor.hasErrors());
    }

    @Test
    public void testMetricScraperFailed() throws Exception {
        doThrow(new Exception()).when(metricScraperOne).scrape(lndApi);

        executor.execute(executorService);
        var registry = executor.collect(Labels.create());

        assertNull(registry.getSampleValue(METRIC_SCRAPER_ONE_NAME));
        assertEquals(METRIC_SCRAPER_TWO_VALUE, registry.getSampleValue(METRIC_SCRAPER_TWO_NAME).intValue());
        verify(metricScraperOne).scrape(same(lndApi));
        verify(metricScraperTwo).scrape(same(lndApi));
        assertTrue(executor.hasErrors());
    }
}
