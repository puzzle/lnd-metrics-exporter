package ch.puzzle.lnd.metricsexporter;

import ch.puzzle.lnd.metricsexporter.common.scrape.ScrapeFactory;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfigRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigLookupException;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/lnd/{node}/{exporter}")
public class LndMetricsController {

    private final ScrapeFactory scrapeFactory;

    private final ScrapeConfigRegistry scrapeConfigRegistry;

    public LndMetricsController(ScrapeFactory scrapeFactory, ScrapeConfigRegistry scrapeConfigRegistry) {
        this.scrapeFactory = scrapeFactory;
        this.scrapeConfigRegistry = scrapeConfigRegistry;
    }

    @GetMapping
    public String scrape(@PathVariable String node, @PathVariable String exporter) throws IOException, ScrapeConfigLookupException {
        var scrapeConfig = scrapeConfigRegistry.lookup(node, exporter);
        var scrape = scrapeFactory.create(scrapeConfig);
        scrape.start(10); // FIXME: Use config
        var registry = scrape.awaitTermination(100, TimeUnit.SECONDS);
        Writer writer = new StringWriter();
        TextFormat.write004(writer, registry.metricFamilySamples());
        return writer.toString();
    }

}
