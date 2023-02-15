package org.example.util.timstamp_extractor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.example.model.SensorRecord;

public class SensorRecordTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        SensorRecord sensorRecord = (SensorRecord) record.value();
        return sensorRecord.getTimestamp();
    }
}
