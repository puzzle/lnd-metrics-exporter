package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ScraperMetricConfig {

    private Map<String, ChannelMetricConfig> channel_route_test;

}
