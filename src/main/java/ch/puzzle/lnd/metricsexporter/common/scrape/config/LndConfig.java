package ch.puzzle.lnd.metricsexporter.common.scrape.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LndConfig {

    private File macaroonPath;

    private List<String> labels;

    private ScrapingConfig scraping;

    private Map<String, NodeConfig> nodes;

    @Data
    @NoArgsConstructor
    public static class ScrapingConfig {

        private int threads;

        private int timeoutSec;

    }

}
