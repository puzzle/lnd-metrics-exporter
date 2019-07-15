package ch.puzzle.lnd.metricsexporter.common.api;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import org.springframework.stereotype.Component;

@Component
public class ApiFactory {

    public LndApi create(ScrapeConfig scrapeConfig) {
        return new LndApi(
                scrapeConfig.getHost(),
                scrapeConfig.getPort(),
                scrapeConfig.getSslContext(),
                scrapeConfig.getMacaroonContext()
        );
    }

}
