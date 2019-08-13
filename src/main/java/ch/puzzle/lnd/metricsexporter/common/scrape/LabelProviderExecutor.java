package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;

import java.util.concurrent.ExecutorService;

class LabelProviderExecutor {

    private static final Object LOCK = new Object();

    private final LndApi api;

    private final Iterable<LabelProvider> labelProviders;

    private volatile boolean hasErrors;

    private Labels labels;

    LabelProviderExecutor(Iterable<LabelProvider> labelProviders, LndApi api) {
        this.labelProviders = labelProviders;
        this.api = api;
        hasErrors = false;
        labels = Labels.create();
    }

    void execute(ExecutorService executorService) {
        labelProviders.forEach(labelProvider -> execute(executorService, labelProvider));
    }

    private void execute(ExecutorService executorService, LabelProvider labelProvider) {
        executorService.submit(() -> {
            Labels labels = null;
            try {
                labels = labelProvider.provide(api);
            } catch (Exception e) {
                hasErrors = true;
            }
            synchronized (LOCK) {
                this.labels = this.labels.merge(labels);
            }
        });
    }

    boolean hasErrors() {
        return hasErrors;
    }

    public Labels collect() {
        return labels;
    }
}
