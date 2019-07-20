package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.Counter;
import org.springframework.stereotype.Component;

@Component
public class BlockchainSyncedScraper implements MetricScraper<Counter> {

    @Override
    public String name() {
        return "blockchain_synced";
    }

    @Override
    public String description() {
        return "Describes whether the blockchain is synced or not.";
    }

    @Override
    public Counter scrape(LndApi lndApi) throws Exception {
        var info = lndApi.synchronous().getInfo();
        return Counter.create()
                .value(info.getSyncedToChain() ? 1 : 0);
    }
}
