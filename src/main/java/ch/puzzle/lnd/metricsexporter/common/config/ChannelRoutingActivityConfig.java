package ch.puzzle.lnd.metricsexporter.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"historyRangeSec"})
public class ChannelRoutingActivityConfig {

    private int historyRangeSec;

}
