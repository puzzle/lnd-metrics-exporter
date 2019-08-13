package ch.puzzle.lnd.metricsexporter;

import ch.puzzle.lnd.metricsexporter.common.scrape.ScrapeFactory;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.LndConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfigRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.NoSuchScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigException;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/lnd/{node}/{exporter}")
public class LndMetricsController {

    private final ScrapeFactory scrapeFactory;

    private final ScrapeConfigRegistry scrapeConfigRegistry;

    private final LndConfig lndConfig;

    public LndMetricsController(ScrapeFactory scrapeFactory, ScrapeConfigRegistry scrapeConfigRegistry, LndConfig lndConfig) {
        this.scrapeFactory = scrapeFactory;
        this.scrapeConfigRegistry = scrapeConfigRegistry;
        this.lndConfig = lndConfig;
    }

    @GetMapping(produces = TextFormat.CONTENT_TYPE_004)
    public String scrape(@PathVariable String node, @PathVariable String exporter) throws IOException, ScrapeConfigException {
        var scrapeConfig = scrapeConfigRegistry.lookup(node, exporter);
        var scrape = scrapeFactory.create(scrapeConfig);
        scrape.start(lndConfig.getScraping().getThreads());
        var registry = scrape.collect(lndConfig.getScraping().getTimeoutSec(), TimeUnit.SECONDS);
        Writer writer = new StringWriter();
        TextFormat.write004(writer, registry.metricFamilySamples());
        return writer.toString();
    }

    @ExceptionHandler(NoSuchScrapeConfigException.class)
    public ResponseEntity<String> handleScrapeConfigNotFound(NoSuchScrapeConfigException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
