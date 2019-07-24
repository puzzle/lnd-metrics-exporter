package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.stereotype.Component;

@Component
public class BalanceOnchainScraper implements MetricScraper {

    @Override
    public String name() {
        return "balance_onchain";
    }

    @Override
    public String description() {
        return "Exports the current total onchain wallet balance.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var walletBalanceResponse = lndApi.synchronous().walletBalance();
        return Measurement.gauge(walletBalanceResponse.getTotalBalance());
    }
}
