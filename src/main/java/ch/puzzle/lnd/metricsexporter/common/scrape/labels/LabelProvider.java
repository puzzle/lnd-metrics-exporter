package ch.puzzle.lnd.metricsexporter.common.scrape.labels;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;

public interface LabelProvider {

    String name();

    Labels provide(LndApi api) throws Exception;

}
