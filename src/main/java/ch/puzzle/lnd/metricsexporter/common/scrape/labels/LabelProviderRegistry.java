package ch.puzzle.lnd.metricsexporter.common.scrape.labels;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LabelProviderRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private volatile Map<String, LabelProvider> labelProviders;

    public LabelProviderRegistry() {
        labelProviders = new HashMap<>();
    }

    public LabelProvider find(String name) {
        return labelProviders.get(name); // FIXME - > not found?
    }

    @Override
    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        labelProviders = context.getBeansOfType(LabelProvider.class)
                .values().stream()
                .collect(Collectors.toMap(LabelProvider::name, labelProvider -> labelProvider));
    }

}
