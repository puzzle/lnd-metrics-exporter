package ch.puzzle.lnd.metricsexporter.scrapers.channel.active;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.common.ChannelInexistentException;
import org.lightningj.lnd.wrapper.message.Channel;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;

public class ChannelActiveScraper implements MetricScraper<Counter> {

    private static final String CHANNEL_ID_LABEL = "channel_id";

    private final long channelId;

    ChannelActiveScraper(long channelId) {
        this.channelId = channelId;
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
            if (channel.getChanId() != channelId) {
                continue;
            }
            return Counter.create()
                    .label(CHANNEL_ID_LABEL, String.valueOf(channelId))
                    .value(channel.getActive() ? 1 : 0);
        }

        throw new ChannelInexistentException();
    }
}
