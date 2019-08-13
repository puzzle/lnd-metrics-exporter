package ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;

public interface LabelProviderRegistry {

    LabelProvider lookup(String name) throws NoSuchLabelProviderException;

}
