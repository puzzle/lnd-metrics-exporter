package ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import org.springframework.stereotype.Component;

public interface LabelProviderRegistry {

    LabelProvider lookup(String name) throws NoSuchLabelProviderException;

}
