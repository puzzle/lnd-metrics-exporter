package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"channelId", "amount"})
public class ChannelMetricConfig {

    private long channelId;

    private int amount;

}
