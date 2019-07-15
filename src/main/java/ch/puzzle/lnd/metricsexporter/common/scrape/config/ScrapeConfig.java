package ch.puzzle.lnd.metricsexporter.common.scrape.config;

import io.netty.handler.ssl.SslContext;
import lombok.Builder;
import lombok.Data;
import org.lightningj.lnd.wrapper.MacaroonContext;

import java.util.List;

@Data
@Builder
public class ScrapeConfig {

    private String host;

    private int port;

    private SslContext sslContext;

    private MacaroonContext macaroonContext;

    private List<String> metricNames;

    private List<String> labelProviderNames;

}
