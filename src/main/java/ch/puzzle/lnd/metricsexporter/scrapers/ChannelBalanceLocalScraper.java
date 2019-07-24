package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.config.ChannelIdentificationConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.lightningj.lnd.wrapper.message.Channel;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;

public class ChannelBalanceLocalScraper implements MetricScraper {

    private String metricConfigName;
    private ChannelIdentificationConfig metricConfig;

    public ChannelBalanceLocalScraper(String metricConfigName, ChannelIdentificationConfig metricConfig) {
        this.metricConfigName = metricConfigName;
        this.metricConfig = metricConfig;
    }

    @Override
    public String name() {
        return "channel_balance_local";
    }

    @Override
    public String description() {
        return "Exports the channel local balance.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var listChannelsResponse = lndApi.synchronous().listChannels(new ListChannelsRequest());

        for (Channel channel : listChannelsResponse.getChannels()) {
            if(channel.getChanId() == metricConfig.getChannelId()) {
                return Measurement.gauge(channel.getLocalBalance())
                        .label("config", metricConfigName);
            }
        }

        // TODO: Throw error and don't return measurement? Or is channel considered inactive if not found?

        return Measurement.gauge(0)
                .label("config", metricConfigName);
    }
}
