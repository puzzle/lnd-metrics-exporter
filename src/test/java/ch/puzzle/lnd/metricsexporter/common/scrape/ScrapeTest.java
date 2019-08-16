package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lightningj.lnd.wrapper.StatusException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScrapeTest {

    @Mock
    private MetricScraperExecutor metricScraperExecutor;

    @Mock
    private Scrape.ScrapeSuccessfulCollectorFactory scrapeSuccessfulCollectorFactory;

    @Mock
    private LabelProviderExecutor labelProviderExecutor;

    @Mock
    private Counter scrapeSuccessfulCollector;

    @Mock
    private Counter.Child scrapeSuccessfulCollectorChild;

    @Mock
    private LndApi lndApi;

    @Mock
    private CollectorRegistry collectorRegistry;

    private Scrape scrape;

    @Before
    public void setup() throws Exception {
        doReturn(scrapeSuccessfulCollector).when(scrapeSuccessfulCollectorFactory).create(any());
        doReturn(collectorRegistry).when(metricScraperExecutor).collect(any());
        doReturn(scrapeSuccessfulCollectorChild).when(scrapeSuccessfulCollector).labels(any());
        doReturn(Labels.create()).when(labelProviderExecutor).collect();

        scrape = new Scrape(metricScraperExecutor, labelProviderExecutor, scrapeSuccessfulCollectorFactory, lndApi);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotStartTwice() {
        scrape.start(1);
        scrape.start(1);
    }

    @Test
    public void testSuccessfulCollect() throws StatusException {
        scrape.start(1);
        scrape.collect(0, TimeUnit.SECONDS);

        verify(metricScraperExecutor).execute(any(ExecutorService.class));
        verify(labelProviderExecutor).execute(any(ExecutorService.class));
        verify(lndApi).close();
        verify(scrapeSuccessfulCollectorChild).inc();
        verify(collectorRegistry).register(scrapeSuccessfulCollector);
    }

    @Test
    public void testLabelProviderExecutorFailed() throws StatusException {
        doReturn(true).when(labelProviderExecutor).hasErrors();

        scrape.start(1);
        scrape.collect(0, TimeUnit.SECONDS);

        verify(metricScraperExecutor).execute(any(ExecutorService.class));
        verify(labelProviderExecutor).execute(any(ExecutorService.class));
        verify(lndApi).close();
        verify(scrapeSuccessfulCollectorChild,never()).inc();
        verify(collectorRegistry).register(scrapeSuccessfulCollector);
    }

    @Test
    public void tesMetricScraperExecutorFailed() throws StatusException {
        doReturn(true).when(metricScraperExecutor).hasErrors();

        scrape.start(1);
        scrape.collect(0, TimeUnit.SECONDS);

        verify(metricScraperExecutor).execute(any(ExecutorService.class));
        verify(labelProviderExecutor).execute(any(ExecutorService.class));
        verify(lndApi).close();
        verify(scrapeSuccessfulCollectorChild,never()).inc();
        verify(collectorRegistry).register(scrapeSuccessfulCollector);
    }
}
