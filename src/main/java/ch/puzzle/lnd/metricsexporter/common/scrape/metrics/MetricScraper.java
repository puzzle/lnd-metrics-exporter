package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Measurement;

public interface MetricScraper<TMeasurement extends Measurement<?, ?>> {

    String name();

    String description();

    TMeasurement scrape(LndApi lndApi) throws Exception;

}
