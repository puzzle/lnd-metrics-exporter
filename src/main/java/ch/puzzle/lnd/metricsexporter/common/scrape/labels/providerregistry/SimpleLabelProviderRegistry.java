package ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
public class SimpleLabelProviderRegistry implements LabelProviderRegistry, ApplicationListener<ContextRefreshedEvent> {

    private volatile Map<String, LabelProvider> labelProviders;

    public SimpleLabelProviderRegistry() {
        labelProviders = new HashMap<>();
    }

    public LabelProvider lookup(String name) throws NoSuchLabelProviderException {
        if (!labelProviders.containsKey(name)) {
            throw new NoSuchLabelProviderException(name);
        }
        return labelProviders.get(name);
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        labelProviders = context.getBeansOfType(LabelProvider.class)
                .values().stream()
                .collect(Collectors.toMap(LabelProvider::name, labelProvider -> labelProvider));
    }

}
