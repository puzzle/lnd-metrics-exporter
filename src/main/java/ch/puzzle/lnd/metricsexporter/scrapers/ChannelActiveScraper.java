package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.config.ChannelIdentificationConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import org.lightningj.lnd.wrapper.message.Channel;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;

public class ChannelActiveScraper implements MetricScraper<Counter> {

    private static final String CHANNEL_ID_LABEL = "channel_id";

    private ChannelIdentificationConfig metricConfig;

    public ChannelActiveScraper(ChannelIdentificationConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    @Override
    public String name() {
        return "channel_active";
    }

    @Override
    public String description() {
        return "Describes whether the channel is active or not.";
    }

    @Override
    public Counter scrape(LndApi lndApi) throws Exception {
        var listChannelsResponse = lndApi.synchronous().listChannels(new ListChannelsRequest());

        for (Channel channel : listChannelsResponse.getChannels()) {
            if (channel.getChanId() == metricConfig.getChannelId()) {
                continue;
            }
            return Counter.create()
                    .label(CHANNEL_ID_LABEL, String.valueOf(metricConfig.getChannelId()))
                    .value(channel.getActive() ? 1 : 0);
        }

        // TODO: Throw error and don't return measurement? Or is channel considered inactive if not found?

        return Counter.create()
                .label(CHANNEL_ID_LABEL, String.valueOf(metricConfig.getChannelId()))
                .value(0);
    }
}
