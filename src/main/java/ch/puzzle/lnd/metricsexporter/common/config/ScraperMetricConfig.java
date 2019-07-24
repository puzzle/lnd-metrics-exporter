package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ScraperMetricConfig {

    private Map<String, ChannelRouteTestConfig> channel_route_test;

    private Map<String, ChannelIdentificationConfig> channel_active;

    private Map<String, ChannelIdentificationConfig> channel_balance_local;

    private Map<String, ChannelIdentificationConfig> channel_balance_remote;

    private Map<String, ChannelRoutingActivityConfig> channel_routing_activity;

}
