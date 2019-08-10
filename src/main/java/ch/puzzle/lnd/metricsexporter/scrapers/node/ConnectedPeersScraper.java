package ch.puzzle.lnd.metricsexporter.scrapers.node;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.springframework.stereotype.Component;

@Component
public class ConnectedPeersScraper implements MetricScraper<Gauge> {

    @Override
    public String name() {
        return "connected_peers";
    }

    @Override
    public String description() {
        return "Exports the number of connected peers.";
    }

    @Override
    public Gauge scrape(LndApi lndApi) throws Exception {
        var getInfoResponse = lndApi.synchronous().getInfo();
        return Gauge.create().value((double) getInfoResponse.getNumPeers());
    }
}
