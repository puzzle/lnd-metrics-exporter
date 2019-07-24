package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.stereotype.Component;

@Component
public class ConnectedPeersScraper implements MetricScraper {

    @Override
    public String name() {
        return "connected_peers";
    }

    @Override
    public String description() {
        return "Exports the number of connected peers.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var getInfoResponse = lndApi.synchronous().getInfo();
        return Measurement.gauge(getInfoResponse.getNumPeers());
    }
}
