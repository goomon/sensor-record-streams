package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.example.model.SensorMeanRecord;
import org.example.model.SensorRecord;
import org.example.model.cache.SensorWindowQueue;
import org.example.transformer.SensorWindowingTransformer;
import org.example.util.serde.StreamsSerde;
import org.example.util.timstamp_extractor.SensorRecordTimestampExtractor;

import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.*;
import static org.example.config.ConnectionConfig.*;
import static org.example.config.ProcessingConfig.*;

public class StateStoreSlidingWindowProcessing {

    private static final String STATE_STORE_NAME = "sensorWindowingStore";

    public static void main(String[] args) throws InterruptedException {

        // Stream processor setting
        StreamsConfig streamsConfig = new StreamsConfig(getProps());

        // Serializer & Deserializer setting
        Serde<String> stringSerde = Serdes.String();
        Serde<SensorRecord> sensorRecordSerde = StreamsSerde.SensorRecordSerde();
        Serde<SensorWindowQueue> sensorWindowQueueSerde = StreamsSerde.SensorWindowQueueSerde();

        // State store setting
        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(STATE_STORE_NAME);
        StoreBuilder<KeyValueStore<String, SensorWindowQueue>> storeBuilder = Stores.keyValueStoreBuilder(storeSupplier, stringSerde, sensorWindowQueueSerde);

        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(storeBuilder);

        KStream<String, SensorRecord> sensorRecordKStream = builder.stream("data",
                Consumed.with(stringSerde, sensorRecordSerde)
                        .withOffsetResetPolicy(EARLIEST)
                        .withTimestampExtractor(new SensorRecordTimestampExtractor()));
        KStream<String, SensorMeanRecord> sensorFeatureKStream = sensorRecordKStream.mapValues(SensorMeanRecord::new)
                .transformValues(() -> new SensorWindowingTransformer(STATE_STORE_NAME), STATE_STORE_NAME)
                .filter((key, value) -> value.getTimestamp() % WINDOWING_PERIOD_MS == 0);
        sensorFeatureKStream.print(Printed.<String, SensorMeanRecord>toSysOut().withLabel("feature"));

        // Execute Kafka streams
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(60000);
        kafkaStreams.close();
    }

    private static Properties getProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "state-store-streams");
        props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "sample-instance-id");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "state-store-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        return props;
    }
}
