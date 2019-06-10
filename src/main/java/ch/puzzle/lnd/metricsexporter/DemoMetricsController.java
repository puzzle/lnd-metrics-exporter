package ch.puzzle.lnd.metricsexporter;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@RestController
@RequestMapping("/metrics")
public class DemoMetricsController {

    private final Gauge gauge;

    private final Counter counter;

    public DemoMetricsController() {
        gauge = Gauge.build()
                .name("test_gauge")
                .help("some helpful text")
                .labelNames("some_label", "another_label")
                .create();
        counter = Counter.build()
                .name("test_counter")
                .help("another helpful text")
                .create();
    }

    @GetMapping
    public String demo() throws IOException {
//        gauge.set(3); // --> NullPtr
        gauge.labels("some-value", "another-value").set(2);
        gauge.labels("value", "").set(2);

        counter.inc(800);

        var collectorRegistry = new CollectorRegistry();
        collectorRegistry.register(gauge);
        collectorRegistry.register(counter);

        Writer writer = new StringWriter();
        TextFormat.write004(writer, collectorRegistry.metricFamilySamples());
        return writer.toString();
    }

}
