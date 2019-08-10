package ch.puzzle.lnd.metricsexporter.common.config;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.NoSuchScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.InvalidScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfigRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigException;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.lightningj.lnd.wrapper.ClientSideException;
import org.lightningj.lnd.wrapper.MacaroonContext;
import org.lightningj.lnd.wrapper.StaticFileMacaroonContext;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Component
public class ConfigScrapeConfigRegistry implements ScrapeConfigRegistry {

    private static final String MACAROON_FILENAME_TEMPLATE = "%s.macaroon";

    private final LndConfig lndConfig;

    public ConfigScrapeConfigRegistry(LndConfig lndConfig) {
        this.lndConfig = lndConfig;
    }

    @Override
    public ScrapeConfig lookup(String node, String exporter) throws ScrapeConfigException {
        var nodeConfig = findNodeConfig(node);
        return ScrapeConfig.builder()
                .metricNames(findMetrics(nodeConfig, exporter))
                .host(nodeConfig.getHost())
                .port(nodeConfig.getPort())
                .sslContext(createSslContext(nodeConfig))
                .macaroonContext(createMacaroonContext(node, exporter))
                .labelProviderNames(lndConfig.getLabels())
                .build();
    }

    private SslContext createSslContext(NodeConfig nodeConfig) throws InvalidScrapeConfigException {
        try {
            return GrpcSslContexts.configure(SslContextBuilder.forClient(), SslProvider.OPENSSL)
                    .trustManager(new ByteArrayInputStream(nodeConfig.getCert().getBytes()))
                    .build();
        } catch (Exception e) {
            throw InvalidScrapeConfigException.invalidCertificate(e);
        }
    }

    private MacaroonContext createMacaroonContext(String node, String exporter) throws InvalidScrapeConfigException {
        var nodeConfigPath = new File(lndConfig.getMacaroonPath(), node);
        var macaroonFile = new File(nodeConfigPath, String.format(MACAROON_FILENAME_TEMPLATE, exporter));
        if (!macaroonFile.canRead()) {
            throw InvalidScrapeConfigException.invalidMacaroon(macaroonFile);
        }
        try {
            return new StaticFileMacaroonContext(macaroonFile);
        } catch (ClientSideException e) {
            throw InvalidScrapeConfigException.invalidMacaroon(e, macaroonFile);
        }
    }

    private NodeConfig findNodeConfig(String node) throws NoSuchScrapeConfigException {
        if (!lndConfig.getNodes().containsKey(node)) {
            throw NoSuchScrapeConfigException.noSuchNode(node);
        }
        return lndConfig.getNodes().get(node);
    }

    private List<String> findMetrics(NodeConfig nodeConfig, String exporter) throws NoSuchScrapeConfigException {
        if (!nodeConfig.getExporters().containsKey(exporter)) {
            throw NoSuchScrapeConfigException.noSuchExporter(nodeConfig, exporter);
        }
        return nodeConfig.getExporters().get(exporter);
    }
}
