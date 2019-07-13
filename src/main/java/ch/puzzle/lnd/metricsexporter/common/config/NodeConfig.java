package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString(of = {"host", "port", "exporters"})
public class NodeConfig {

    private String host;

    private Integer port;

    private String cert;

    private Map<String, List<String>> exporters;

}
