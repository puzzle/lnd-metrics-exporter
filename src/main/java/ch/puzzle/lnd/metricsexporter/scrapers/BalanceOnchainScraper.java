package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.springframework.stereotype.Component;

@Component
public class BalanceOnchainScraper implements MetricScraper<Gauge> {

    @Override
    public String name() {
        return "balance_onchain";
    }

    @Override
    public String description() {
        return "Exports the current total onchain wallet balance.";
    }

    @Override
    public Gauge scrape(LndApi lndApi) throws Exception {
        var walletBalanceResponse = lndApi.synchronous().walletBalance();
        return Gauge.create().value((double) walletBalanceResponse.getTotalBalance());
    }
}
