package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;

public interface MetricScraper {

    String name(); // TODO: Use info for prometheus help stuff

    String description(); // TODO: Use info for prometheus help stuff

    Measurement scrape(LndApi lndApi) throws Exception;

}
