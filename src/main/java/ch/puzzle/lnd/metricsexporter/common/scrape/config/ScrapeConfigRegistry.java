package ch.puzzle.lnd.metricsexporter.common.scrape.config;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigException;

public interface ScrapeConfigRegistry {

    ScrapeConfig lookup(String node, String exporter) throws ScrapeConfigException;

}
