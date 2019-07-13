package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentBlockHeightScraper implements MetricScraper {

    @Override
    public String name() {
        return "current_block_height";
    }

    @Override
    public String description() {
        return "Exports the current block height.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var info = lndApi.synchronous().getInfo();
        return Measurement.counter(info.getBlockHeight()).label("exporterversion", UUID.randomUUID().toString());
    }
}
