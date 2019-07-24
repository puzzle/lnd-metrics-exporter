package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Gauge;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChannelOpenScraper implements MetricScraper<Gauge> {

    @Override
    public String name() {
        return "channel_open";
    }

    @Override
    public String description() {
        return "Exports the number of open channels.";
    }

    @Override
    public Gauge scrape(LndApi lndApi) throws Exception {
        var listChannelsResponse = lndApi.synchronous().listChannels(new ListChannelsRequest());
        return Gauge.create().value((double) listChannelsResponse.getChannels().size());
    }
}
