package ch.puzzle.lnd.metricsexporter.scrapers.nodereachable;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import org.springframework.stereotype.Component;

@Component
public class NodeReachableScraper implements MetricScraper<Counter> {

    @Override
    public String name() {
        return "node_reachable";
    }

    @Override
    public String description() {
        return "Determines whether the node can be reached over LN (1) or not (0).";
    }

    @Override
    public Counter scrape(LndApi lndApi) throws Exception {
        var info = lndApi.synchronous().getInfo();
        var uris = info.getUris();
        for (var uri : uris) {
                if (new LnDaemonDetector().isLnDaemonRunning(uri)) {
                    return Counter.create().value(1);
                }
        }
        return Counter.create().value(0);
    }
}
