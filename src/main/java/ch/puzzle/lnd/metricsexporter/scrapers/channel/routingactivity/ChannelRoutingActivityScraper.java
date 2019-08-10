package ch.puzzle.lnd.metricsexporter.scrapers.channel.routingactivity;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Summary;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.message.ForwardingHistoryRequest;

import java.time.Instant;

public class ChannelRoutingActivityScraper implements MetricScraper<Summary> {

    private static final String CHANNEL_ID_IN_LABEL = "channel_id_in";

    private static final String CHANNEL_ID_OUT_LABEL = "channel_id_out";

    private final int historyRangeSec;

    ChannelRoutingActivityScraper(final int historyRangeSec) {
        this.historyRangeSec = historyRangeSec;
    }

    @Override
    public String name() {
        return "channel_routing_activity";
    }

    @Override
    public String description() {
        return "Exports the routing activity of all channels";
    }

    @Override
    public Summary scrape(LndApi lndApi) throws Exception {
        var currentUnixTime = Instant.now().getEpochSecond();
        var forwardingHistoryRequest = LightningApi.ForwardingHistoryRequest.newBuilder()
                .setStartTime(currentUnixTime - historyRangeSec)
                .setEndTime(currentUnixTime)
                .build();

        var summary = Summary.empty();
        var forwardingHistoryResponse = lndApi.synchronous()
                .forwardingHistory(new ForwardingHistoryRequest(forwardingHistoryRequest));

        while (forwardingHistoryResponse.getForwardingEvents().size() > 0) {
            for (var event : forwardingHistoryResponse.getForwardingEvents()) {
                summary = summary.and()
                        .label(CHANNEL_ID_IN_LABEL, String.valueOf(event.getChanIdIn()))
                        .label(CHANNEL_ID_OUT_LABEL, String.valueOf(event.getChanIdOut()))
                        .value((double) event.getAmtOut());
            }

            forwardingHistoryRequest = forwardingHistoryRequest.toBuilder()
                    .setIndexOffset(forwardingHistoryResponse.getLastOffsetIndex())
                    .build();
            forwardingHistoryResponse = lndApi.synchronous()
                    .forwardingHistory(new ForwardingHistoryRequest(forwardingHistoryRequest));
        }

        return summary;
    }
}
