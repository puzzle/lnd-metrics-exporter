package ch.puzzle.lnd.metricsexporter.scrapers.channel.balancelocal;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import ch.puzzle.lnd.metricsexporter.scrapers.channel.ChannelInexistentException;
import org.lightningj.lnd.wrapper.message.Channel;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;

public class ChannelBalanceLocalScraper implements MetricScraper<Gauge> {

    private static final String CHANNEL_ID_LABEL = "channel_id";

    private final long channelId;

    ChannelBalanceLocalScraper(long channelId) {
        this.channelId = channelId;
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
            if (channel.getChanId() != channelId) {
                continue;
            }
            return Gauge.create()
                    .label(CHANNEL_ID_LABEL, String.valueOf(channelId))
                    .value((double) channel.getLocalBalance());
        }

        throw new ChannelInexistentException();
    }
}
