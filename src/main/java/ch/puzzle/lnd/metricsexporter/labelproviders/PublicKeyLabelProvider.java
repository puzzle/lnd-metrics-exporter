package ch.puzzle.lnd.metricsexporter.labelproviders;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import org.springframework.stereotype.Component;

@Component
public class PublicKeyLabelProvider implements LabelProvider {

    @Override
    public String name() {
        return "pubkey";
    }

    @Override
    public Labels provide(LndApi api) throws Exception {
        return Labels.create().with("pubkey", api.synchronous().getInfo().getIdentityPubkey());
    }
}
