package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;

public interface MetricScraper<TMeasurement extends ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.Measurement<?, ?>> {

    String name(); // TODO: Use info for prometheus help stuff

    String description(); // TODO: Use info for prometheus help stuff

    TMeasurement scrape(LndApi lndApi) throws Exception;

}
