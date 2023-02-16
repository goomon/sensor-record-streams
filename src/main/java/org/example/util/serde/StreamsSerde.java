package org.example.util.serde;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.example.model.SensorMeanRecord;
import org.example.model.SensorRecord;
import org.example.model.cache.SensorWindowQueue;
import org.example.util.serializer.JsonDeserializer;
import org.example.util.serializer.JsonSerializer;

import java.util.Map;

public class StreamsSerde {

    public static Serde<SensorRecord> SensorRecordSerde() {
        return new SensorRecordSerde();
    }

    public static Serde<SensorMeanRecord> SensorMeanRecordSerde() {
        return new SensorMeanRecordSerde();
    }

    public static Serde<SensorWindowQueue> SensorWindowQueueSerde() {
        return new SensorWindowQueueSerde();
    }

    public static class SensorRecordSerde extends WrapperSerde<SensorRecord> {
        public SensorRecordSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(SensorRecord.class));
        }
    }

    public static class SensorMeanRecordSerde extends WrapperSerde<SensorMeanRecord> {
        public SensorMeanRecordSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(SensorMeanRecord.class));
        }
    }

    public static class SensorWindowQueueSerde extends WrapperSerde<SensorWindowQueue> {

        public SensorWindowQueueSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(SensorWindowQueue.class));
        }
    }

    @AllArgsConstructor
    private static class WrapperSerde<T> implements Serde<T> {

        private JsonSerializer<T> serializer;
        private JsonDeserializer<T> deserializer;

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
        }

        @Override
        public void close() {
        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }
    }
}
