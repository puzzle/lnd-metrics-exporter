package ch.puzzle.lnd.metricsexporter.scrapers.channel.routetest;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Counter;
import org.lightningj.lnd.proto.LightningApi;
import org.lightningj.lnd.wrapper.ClientSideException;
import org.lightningj.lnd.wrapper.message.ChannelEdge;
import org.lightningj.lnd.wrapper.message.Invoice;
import org.lightningj.lnd.wrapper.message.Route;

import java.util.Collections;

public class ChannelRouteTestScraper implements MetricScraper<Counter> {

    private static final String CHANNEL_ID_LABEL = "channel_id";

    private final long chanId;

    private final int amount;

    ChannelRouteTestScraper(long chanId, int amount) {
        this.chanId = chanId;
        this.amount = amount;
    }

    @Override
    public String name() {
        return "channel_route_test";
    }

    @Override
    public String description() {
        return "Describes whether the channel is working bidirectional or not.";
    }

    @Override
    public Counter scrape(LndApi lndApi) throws Exception {
        var invoice = LightningApi.Invoice.newBuilder().setValue(1).build();
        var invoiceMessage = new Invoice(invoice);
        var addInvoiceResponse = lndApi.synchronous().addInvoice(invoiceMessage);

        var info = lndApi.synchronous().getInfo();
        var chanInfo = lndApi.synchronous().getChanInfo(chanId);
        var routeMessages = Collections.singletonList(constructRoute(info.getBlockHeight(), amount, chanInfo));

        var sendToRouteResponse = lndApi.synchronous().sendToRouteSync(
                addInvoiceResponse.getRHash(),
                null,
                routeMessages,
                null);

        return Counter.create()
                .label(CHANNEL_ID_LABEL, String.valueOf(chanId))
                .value("".equals(sendToRouteResponse.getPaymentError()) ? 1 : 0);
    }

    private Route constructRoute(int currentBlockHeight, int amount, ChannelEdge channelInfo) throws ClientSideException {
        var fee = channelInfo.getNode1Policy().getFeeBaseMsat();
        var timeLockDelta = channelInfo.getNode1Policy().getTimeLockDelta();
        var totalTimeLock = currentBlockHeight + timeLockDelta + 100;

        var hop = LightningApi.Hop.newBuilder()
                .setChanId(channelInfo.getChannelId())
                .setExpiry(totalTimeLock - timeLockDelta)
                .setAmtToForwardMsat(amount * 1000);

        var route = LightningApi.Route.newBuilder()
                .setTotalAmtMsat((amount + fee) * 1000)
                .setTotalTimeLock(totalTimeLock)
                .addHops(hop)
                .addHops(hop)
                .build();

        var routeMessage = new Route(route);
        routeMessage.getHops();
        return routeMessage;
    }
}
