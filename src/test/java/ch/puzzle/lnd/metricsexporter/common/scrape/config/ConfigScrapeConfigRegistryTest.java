package ch.puzzle.lnd.metricsexporter.common.scrape.config;

import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.InvalidScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.NoSuchScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.ScrapeConfigException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ConfigScrapeConfigRegistryTest {

    private ConfigScrapeConfigRegistry registry;

    private LndConfig lndConfig;

    private static final String EXISTING_NODE_CONFIG_NAME = "existing-node";

    private static final String EXISTING_EXPORTER_CONFIG_NAME = "existing-exporter";

    private static final String CERT = "-----BEGIN CERTIFICATE-----\n" +
            "MIIB/DCCAaKgAwIBAgIRAIKfQYkJJDIonUB+ROQU85wwCgYIKoZIzj0EAwIwODEf\n" +
            "MB0GA1UEChMWbG5kIGF1dG9nZW5lcmF0ZWQgY2VydDEVMBMGA1UEAxMMMTVmZTAx\n" +
            "ZmQ1MDk4MB4XDTE5MDYyNDA2NDI1NFoXDTIwMDgxODA2NDI1NFowODEfMB0GA1UE\n" +
            "ChMWbG5kIGF1dG9nZW5lcmF0ZWQgY2VydDEVMBMGA1UEAxMMMTVmZTAxZmQ1MDk4\n" +
            "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+BGIhxOPViNxUo/hqkBJRqOyLfkO\n" +
            "Uw6DCnaiKRfc6B/bUqr/Eq/Ai7reDHWi6j9Vylm8ic6SbVhE7QzsqWoeXKOBjDCB\n" +
            "iTAOBgNVHQ8BAf8EBAMCAqQwDwYDVR0TAQH/BAUwAwEB/zBmBgNVHREEXzBdggwx\n" +
            "NWZlMDFmZDUwOTiCCWxvY2FsaG9zdIISaXA1LmFkcmlhbnBhdWxpLmNoggR1bml4\n" +
            "ggp1bml4cGFja2V0hwR/AAABhxAAAAAAAAAAAAAAAAAAAAABhwSsFAACMAoGCCqG\n" +
            "SM49BAMCA0gAMEUCIDvXHJH8KRt7DcH5jve2XeH5MVUBSNJ9OP+qdchp7n1JAiEA\n" +
            "xAyssKuHn8Q8WNPLedzczBKXmXnI4iEyegZtn8Q+NJM=\n" +
            "-----END CERTIFICATE-----";

    private static final String RPC_HOST = "the-host";

    private static final int RPC_PORT = 1111;

    private static final List<String> METRICS = List.of("metric-one", "metric-two");

    private static final List<String> LABELS = List.of("label-one", "label-two");

    private NodeConfig nodeConfig;

    @Before
    public void setup() {
        nodeConfig = new NodeConfig();
        nodeConfig.setExporters(Map.of(EXISTING_EXPORTER_CONFIG_NAME, METRICS));
        nodeConfig.setCert(CERT);
        nodeConfig.setHost(RPC_HOST);
        nodeConfig.setPort(RPC_PORT);

        lndConfig = new LndConfig();
        lndConfig.setNodes(Map.of(EXISTING_NODE_CONFIG_NAME, nodeConfig));
        lndConfig.setLabels(LABELS);
        lndConfig.setMacaroonPath(new File(getClass().getClassLoader().getResource("macaroons").getFile()));

        registry = new ConfigScrapeConfigRegistry(lndConfig);
    }

    @Test
    public void testConfigExists() throws ScrapeConfigException {
        var scrapeConfig = registry.lookup(EXISTING_NODE_CONFIG_NAME, EXISTING_EXPORTER_CONFIG_NAME);
        assertNotNull(scrapeConfig);
        assertEquals(RPC_HOST, scrapeConfig.getHost());
        assertEquals(RPC_PORT, scrapeConfig.getPort());
        assertEquals(METRICS, scrapeConfig.getMetricNames());
        assertEquals(LABELS, scrapeConfig.getLabelProviderNames());
        assertNotNull(scrapeConfig.getMacaroonContext());
        assertNotNull(scrapeConfig.getSslContext());
    }

    @Test(expected = NoSuchScrapeConfigException.class)
    public void testInexistentNodeConfig() throws ScrapeConfigException {
        registry.lookup("inexistent-config", EXISTING_EXPORTER_CONFIG_NAME);
    }

    @Test(expected = NoSuchScrapeConfigException.class)
    public void testInexistentExporterConfig() throws ScrapeConfigException {
        registry.lookup(EXISTING_NODE_CONFIG_NAME, "inexistent-config");
    }

    @Test(expected = InvalidScrapeConfigException.class)
    public void invalidSslCert() throws ScrapeConfigException {
        nodeConfig.setCert("");
        registry.lookup(EXISTING_NODE_CONFIG_NAME, EXISTING_EXPORTER_CONFIG_NAME);
    }

    @Test(expected = InvalidScrapeConfigException.class)
    public void invalidMacaroonPath() throws ScrapeConfigException {
        lndConfig.setMacaroonPath(new File(lndConfig.getMacaroonPath(), "inexistent-subdir"));
        registry.lookup(EXISTING_NODE_CONFIG_NAME, EXISTING_EXPORTER_CONFIG_NAME);
    }
}
