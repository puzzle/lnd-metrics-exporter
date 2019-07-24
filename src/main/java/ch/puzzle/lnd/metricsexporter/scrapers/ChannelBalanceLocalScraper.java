package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.config.ChannelIdentificationConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.lightningj.lnd.wrapper.message.Channel;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;

public class ChannelBalanceLocalScraper implements MetricScraper<Gauge> {

    private static final String CHANNEL_ID_LABEL = "channel_id";

    private ChannelIdentificationConfig metricConfig;

    public ChannelBalanceLocalScraper(ChannelIdentificationConfig metricConfig) {
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
    public Gauge scrape(LndApi lndApi) throws Exception {
        var listChannelsResponse = lndApi.synchronous().listChannels(new ListChannelsRequest());

        for (Channel channel : listChannelsResponse.getChannels()) {
            if (channel.getChanId() == metricConfig.getChannelId()) {
                continue;
            }
            return Gauge.create()
                    .label(CHANNEL_ID_LABEL, String.valueOf(metricConfig.getChannelId()))
                    .value((double) channel.getLocalBalance());

        }

        // TODO: Throw error and don't return measurement? Or is channel considered inactive if not found?

        return Gauge.create()
                .label(CHANNEL_ID_LABEL, String.valueOf(metricConfig.getChannelId()))
                .value(0d);
    }
}
