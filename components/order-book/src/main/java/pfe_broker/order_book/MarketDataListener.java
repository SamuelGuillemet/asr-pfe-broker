package pfe_broker.order_book;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pfe_broker.avro.MarketData;
import pfe_broker.avro.Trade;

@Singleton
public class MarketDataListener {

  private static final Logger LOG = LoggerFactory.getLogger(
    MarketDataListener.class
  );

  @Property(name = "kafka.common.symbol-topic-prefix")
  private String symbolTopicPrefix;

  @Inject
  private OrderBookCatalog orderBooks;

  @Inject
  private TradeProducer tradeProducer;

  @KafkaListener(
    groupId = "order-book-market-data",
    batch = true,
    threadsValue = "${kafka.common.market-data-thread-pool-size}"
  )
  @Topic(patterns = "${kafka.common.symbol-topic-prefix}[A-Z]+")
  public void receiveMarketData(
    List<ConsumerRecord<String, MarketData>> records
  ) {
    records.forEach(record -> {
      MarketData marketData = record.value();
      String symbol = record.topic().substring(symbolTopicPrefix.length());
      LimitOrderBook orderBook = orderBooks.getOrderBook(symbol);
      if (orderBook == null) {
        return;
      }

      Map<String, Trade> trades = orderBook.matchOrdersToTrade(marketData);
      if (trades.isEmpty()) {
        return;
      }
      LOG.debug("Sending {} trades to Kafka", trades.size());
      trades.forEach((key, trade) -> {
        tradeProducer.sendTrade(key, trade);
      });
    });
  }
}