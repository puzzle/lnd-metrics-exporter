package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.NodeConfig;

public class NoSuchScrapeConfigException extends ScrapeConfigException {

    private NoSuchScrapeConfigException(String message) {
        super(message);
    }

    public static NoSuchScrapeConfigException noSuchNode(String node) {
        return new NoSuchScrapeConfigException(String.format("No node configuration '%s' found.", node));
    }

    public static NoSuchScrapeConfigException noSuchExporter(NodeConfig nodeConfig, String exporter) {
        return new NoSuchScrapeConfigException(String.format(
                "No exporter configuration '%s', for node '%s' found.",
                exporter,
                nodeConfig
        ));
    }
}
