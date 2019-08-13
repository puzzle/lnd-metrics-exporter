package ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry;

public class NoSuchLabelProviderException extends Exception {

    public NoSuchLabelProviderException(String name) {
        super(String.format("No label provider with name '%s' found.", name));
    }

}
