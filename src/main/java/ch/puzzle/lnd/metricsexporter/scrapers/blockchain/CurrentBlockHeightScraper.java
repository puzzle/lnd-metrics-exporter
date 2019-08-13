package ch.puzzle.lnd.metricsexporter.scrapers.blockchain;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.springframework.stereotype.Component;

@Component
public class CurrentBlockHeightScraper implements MetricScraper<Gauge> {

    @Override
    public String name() {
        return "current_block_height";
    }

    @Override
    public String description() {
        return "Exports the current block height.";
    }

    @Override
    public Gauge scrape(LndApi lndApi) throws Exception {
        var info = lndApi.synchronous().getInfo();
        return Gauge.create()
                .value((double) info.getBlockHeight());
    }
}
