package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Measurement;

public interface MetricScraper<TMeasurement extends Measurement<?, ?>> {

    String name(); // TODO: Use info for prometheus help stuff

    String description(); // TODO: Use info for prometheus help stuff

    TMeasurement scrape(LndApi lndApi) throws Exception;

}
