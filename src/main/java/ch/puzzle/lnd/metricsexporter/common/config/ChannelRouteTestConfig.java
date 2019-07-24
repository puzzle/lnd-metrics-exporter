package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"channelId", "amount"})
public class ChannelRouteTestConfig {

    private long channelId;

    private int amount;

}
