package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.stereotype.Component;

@Component
public class BalanceOffchainScraper implements MetricScraper {

    @Override
    public String name() {
        return "balance_offchain";
    }

    @Override
    public String description() {
        return "Exports the current total offchain balance.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var channelBalanceResponse = lndApi.synchronous().channelBalance();
        return Measurement.gauge(channelBalanceResponse.getBalance());
    }
}
