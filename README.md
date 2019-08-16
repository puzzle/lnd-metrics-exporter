# LND Metrics Exporter

LND Metrics Exporter is a [Prometheus](https://prometheus.io)  Metrics Exporter for LND, written in Java.


## Requirements

The LND Metrics Exporter requires at least Java 11.
It can be used with [LND 0.6.1-beta](https://github.com/lightningnetwork/lnd/releases/tag/v0.6.1-beta) or newer.

## Configuration

### Metrics Exporter
A single LND Metrics Exporter instance can be used for monitoring multiple LND nodes.
Each node and it's metrics can be configured within the `application.yml` file.

For further details, please have a look at the following configuration example and it's annotations:

```yaml
lnd:

  # Defines the path where macaroons are stored. See also the corresponding section.
  macaroonPath: macaroons
  scraping:
    # Defines the number of threads used for a scrape.
    threads: 10
    # Defines the timeout for a scrape.
    timeoutSec: 10
    # Defines the name of the metric specifying whether the scrape was successful or not. See also the corresponding section.
    successMetricName: scrape_successful

  # Holds configurations for metric scraper which require parametrization. See also the corresponding section.
  scrapers:
    # Specifies configurations of the channel route test metric.
    channel_route_test:
      # Defines the channel route test metric config name.
      channelRouteTestConfigName:
        # Specifies the channel id used for the channel route test metric scrape.
        channelId: 1637407009740881920
      ...
    channel_active:
      # Defines the channel active metric config name.
      channelActiveConfigName:
        # Specifies the channel id used for the channel active metric scrape.
        channelId: 1637407009740881920
      ...
    channel_balance_local:
      # Defines the channel balance local metric config name.
      channelBalancheLocalConfigName:
        # Specifies the channel id used for the channel balance local metric scrape.
        channelId: 1637407009740881920
      ...
    channel_balance_remote:
      # Defines the channel balance remote metric config name.
      channelBalanceRemoteConfigName:
        # Specifies the channel id used for the channel balance remote metric scrape.
        channelId: 1637407009740881920
      ...
    channel_routing_activity:
      # Defines the channel routing activity metric config name.
      channelRoutingActivityConfigName:
        # Defines the amount of past seconds which shall be searched for routing activities.
        historyRangeSec: 10368000
      ...

  # Specifies the names of label providers. Each label provider adds a defined label to EVERY metric scraped.
  labels:
    - pubkey
    ...

  # Specifies node configurations
  nodes:

    # NODE: Defines the node name used in the metrics exporter endpoint url.
    node-01:
      # Defines the hostname of node used for gRPC access.
      host: lnd01.example.org
      # Defines the gRPC port of the node.
      port: 10009
      # Holds the TLS certificate of the node.
      cert: |
        -----BEGIN CERTIFICATE-----
        ...
        -----END CERTIFICATE-----
      exporters:

        # ENDPOINT: Defines the metrics exporter endpoint name used in the url.
        common:
          # Holds all names of metrics which shall be scraped.
          - node_reachable
          - ...
        testpayments:
          - channel_route_test.channelRouteTestConfig
          - channel_route_test.fhnw01ToPuzzleITC
    # Defines another node configuration.
    node-02:
      host: lnd02.example.org
      ...

```

#### Macaroon path
For each metrics exporter endpoint a macaroon must be provided. 
The macaroon files must be placed under `$MACAROON_ROOT/$NODE/$ENDPOINT.macaroon`, whereas the variables have the following meanings:
* MACAROON_ROOT: The directory configured using `lnd.macaroonPath`.
* NODE: The node configuration name under `lnd.nodes`.
* ENDPOINT: The metrics exporter endpoint name under `lnd.nodes.$NODE.exporters`.


Macaroon files can be copied from `data/chain/bitcoin/testnet/` for testnet and `data/chain/bitcoin/mainnet/` for mainnet after a successful `lncli unlock`. 

#### Successful Metric
There is a metric telling whether the scrape was successful or not.
If no errors occurred, it's value is `1.0`, otherwise `0.0`.
Possible errors are:
* a metric scraper throwing an exception
* the `lnd.scraping.temoutSec` being to small for scraping all configured metrics

The name of the metric can be defined using `lnd.scraping.successMetricName`.

#### Parametrized Metric Scrapers
There are some metric scrapers which require parametrization (e.g. all channel scrapers).

In order to ensure type safety of configurations, each type of parameterizable metric scraper has its own configuration namespace under `lnd.scrapers`.
Configurations can be added as follows:
```yaml
lnd:
  scrapers:
    $METRIC_NAME:
      $CONFIG_NAME:
        # scraper specific config
```

`$METRIC_NAME` holds the name of the metric scraper and `$CONFIG_NAME` defines the configuration's name.
In order to add the metric scraper config to a metrics exporter endpoint, it must be referenced as follows:

```yaml
lnd:
  nodes:
    $NODE:
       exporters:
         - $METRIC_NAME.$CONFIG_NAME
```

### Prometheus
The LND Metrcis Exporter can be configured in `prometheus.yml` as follows:

```yaml
scrape_configs:
  - job_name: 'node-01.common'
    scrape_interval: 15s
    scheme: https
    metrics_path: /lnd/node-01/common
    static_configs:
    - targets: ['host-of-lnd-metrcis-exporter.example.org']
  - job_name: 'node-01.testpayments'
    scrape_interval: 1h
    scheme: https
    metrics_path: /lnd/node-01/testpayments
    static_configs:
    - targets: ['host-of-lnd-metrcis-exporter.example.org']
  - job_name: 'node-02.common'
    scrape_interval: 15s
    scheme: https
    metrics_path: /lnd/node-02/common
    static_configs:
    - targets: ['host-of-lnd-metrcis-exporter.example.org']
    ...
```

The pattern for `metrics_path` is always `/lnd/$NODE/$ENDPOINT` whereas `$NODE` / `$ENDPOINT` are valid node / endpoint configuration keys (see also the previous section).

## Build

The LND Metrics Exporter is intended to be run within a container environment.
Therefore, it has its own Dockerfile.
In order to build the docker image, just run the following command from within the project root:

`docker build .` 
