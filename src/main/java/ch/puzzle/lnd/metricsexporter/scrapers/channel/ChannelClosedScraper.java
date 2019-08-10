package ch.puzzle.lnd.metricsexporter.scrapers.channel;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import org.lightningj.lnd.wrapper.message.ClosedChannelsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChannelClosedScraper implements MetricScraper<Counter> {

    @Override
    public String name() {
        return "channel_closed";
    }

    @Override
    public String description() {
        return "Exports the number of closed channels.";
    }

    @Override
    public Counter scrape(LndApi lndApi) throws Exception {
        var closedChannelsResponse = lndApi.synchronous().closedChannels(new ClosedChannelsRequest());
        return Counter.create().value(closedChannelsResponse.getChannels().size());
    }
}
