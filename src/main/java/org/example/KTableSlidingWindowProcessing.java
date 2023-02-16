package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.example.model.SensorMeanRecord;
import org.example.model.SensorRecord;
import org.example.util.serde.StreamsSerde;
import org.example.util.timstamp_extractor.SensorRecordTimestampExtractor;

import java.time.Duration;
import java.util.Properties;

import static org.apache.kafka.streams.Topology.AutoOffsetReset.*;
import static org.example.config.ConnectionConfig.*;
import static org.example.config.ProcessingConfig.*;

public class KTableSlidingWindowProcessing {
    public static void main(String[] args) throws InterruptedException {

        StreamsConfig streamsConfig = new StreamsConfig(getProps());

        Serde<String> stringSerde = Serdes.String();
        Serde<SensorRecord> sensorRecordSerde = StreamsSerde.SensorRecordSerde();
        Serde<SensorMeanRecord> sensorMeanRecordSerde = StreamsSerde.SensorMeanRecordSerde();

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, SensorRecord> sensorRecordKStream = builder.stream("data",
                Consumed.with(stringSerde, sensorRecordSerde)
                        .withOffsetResetPolicy(EARLIEST)
                        .withTimestampExtractor(new SensorRecordTimestampExtractor()));
        KStream<String, SensorMeanRecord> sensorMeanRecordKStream = sensorRecordKStream.mapValues(SensorMeanRecord::new);

        KTable<Windowed<String>, SensorMeanRecord> sensorFeatureTable = sensorMeanRecordKStream
                .groupByKey(Grouped.with(stringSerde, sensorMeanRecordSerde))
                .windowedBy(
                        TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(SLIDING_WINDOW_SECOND))
                                .advanceBy(Duration.ofMillis(WINDOWING_PERIOD_MS)))
                .reduce(SensorMeanRecord::add);
        sensorFeatureTable.toStream().print(Printed.<Windowed<String>, SensorMeanRecord>toSysOut().withLabel("feature"));

        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), streamsConfig);
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(100000);
        kafkaStreams.close();
    }

    private static Properties getProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sensor-data-streams");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sensor-data-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        return props;
    }
}
