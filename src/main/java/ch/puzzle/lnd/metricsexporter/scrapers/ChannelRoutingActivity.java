package ch.puzzle.lnd.metricsexporter.scrapers;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.message.ForwardingEvent;
import org.lightningj.lnd.wrapper.message.ForwardingHistoryRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ChannelRoutingActivity implements MetricScraper {

    @Override
    public String name() {
        return "channel_routing_activity";
    }

    @Override
    public String description() {
        return "Exports the routing activity of all channels";
    }

    @Override
    public Measurement scrape(LndApi lndApi) throws Exception {
        var currentUnixTime = Instant.now().getEpochSecond();
        var forwardingHistoryRequest = LightningApi.ForwardingHistoryRequest.newBuilder()
                .setStartTime(currentUnixTime - 3600 * 24 * 120)
                .setEndTime(currentUnixTime)
                .build();
        var forwardingHistoryResponse = lndApi.synchronous().forwardingHistory(new ForwardingHistoryRequest(forwardingHistoryRequest));

        for (ForwardingEvent event : forwardingHistoryResponse.getForwardingEvents()) {

        }

        // TODO: Throw error and don't return measurement? Or is channel considered inactive if not found?

        return Measurement.counter(0);
    }
}
