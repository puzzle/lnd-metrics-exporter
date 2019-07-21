package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.lightningj.lnd.wrapper.message.ListChannelsRequest;
import org.springframework.stereotype.Component;

@Component
public class ChannelOpenScraper implements MetricScraper {

    @Override
    public String name() {
        return "channel_open";
    }

    @Override
    public String description() {
        return "Exports the number of open channels.";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var listChannelsResponse = lndApi.synchronous().listChannels(new ListChannelsRequest());
        return Measurement.gauge(listChannelsResponse.getChannels().size());
    }
}
