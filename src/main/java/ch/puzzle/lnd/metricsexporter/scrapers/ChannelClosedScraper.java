package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.lightningj.lnd.wrapper.message.ClosedChannelsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChannelClosedScraper implements MetricScraper {

    @Override
    public String name() {
        return "channel_closed";
    }

    @Override
    public String description() {
        return "Exports the number of closed channels.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var closedChannelsResponse = lndApi.synchronous().closedChannels(new ClosedChannelsRequest());
        return Measurement.counter(closedChannelsResponse.getChannels().size());
    }
}
