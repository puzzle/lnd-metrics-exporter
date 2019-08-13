package ch.puzzle.lnd.metricsexporter.scrapers.node;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.springframework.stereotype.Component;

@Component
public class BalanceOffchainScraper implements MetricScraper<Gauge> {

    @Override
    public String name() {
        return "balance_offchain";
    }

    @Override
    public String description() {
        return "Exports the current total offchain balance.";
    }

    @Override
    public Gauge scrape(LndApi lndApi) throws Exception {
        var channelBalanceResponse = lndApi.synchronous().channelBalance();
        return Gauge.create().value((double) channelBalanceResponse.getBalance());
    }
}
