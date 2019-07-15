package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.stereotype.Component;

@Component
public class BlockchainSyncedScraper implements MetricScraper {

    @Override
    public String name() {
        return "blockchain_synced";
    }

    @Override
    public String description() {
        return "Describes whether the blockchain is synced or not.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var info = lndApi.synchronous().getInfo();
        return Measurement.counter(info.getSyncedToChain() ? 1 : 0);
    }
}
