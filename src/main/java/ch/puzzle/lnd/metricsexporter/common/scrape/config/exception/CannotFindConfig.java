package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

import ch.puzzle.lnd.metricsexporter.common.config.NodeConfig;

public class CannotFindConfig extends ScrapeConfigLookupException {

    private CannotFindConfig(String message) {
        super(message);
    }

    public static CannotFindConfig noSuchNode(String node) {
        return new CannotFindConfig(String.format("No node configuration '%s' found.", node));
    }

    public static CannotFindConfig noSuchExporter(NodeConfig nodeConfig, String exporter) {
        return new CannotFindConfig(String.format(
                "No exporter configuration '%s', for node '%s' found.",
                exporter,
                nodeConfig
        ));
    }
}
