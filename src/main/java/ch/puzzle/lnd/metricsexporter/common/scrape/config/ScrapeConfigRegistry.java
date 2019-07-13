package ch.puzzle.lnd.metricsexporter.common.scrape.config;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigLookupException;

public interface ScrapeConfigRegistry {

    ScrapeConfig lookup(String node, String exporter) throws ScrapeConfigLookupException;

}
