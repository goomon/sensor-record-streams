package org.example.transformer;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.example.model.SensorMeanRecord;
import org.example.model.cache.SensorWindowQueue;

import java.util.LinkedList;

import static org.example.config.ProcessingConfig.*;

public class SensorWindowingTransformer implements ValueTransformer<SensorMeanRecord, SensorMeanRecord> {

    private KeyValueStore<String, SensorWindowQueue> stateStore;
    private String storeName;
    private ProcessorContext context;

    public SensorWindowingTransformer(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        this.stateStore = (KeyValueStore) this.context.getStateStore(storeName);
    }

    @Override
    public SensorMeanRecord transform(SensorMeanRecord value) {
        SensorWindowQueue sensorWindowQueue = stateStore.get(value.getUserId());
        if (sensorWindowQueue == null) {
            stateStore.put(value.getUserId(), new SensorWindowQueue(value.getTimestamp(), new LinkedList<>()));
            sensorWindowQueue = stateStore.get(value.getUserId());
        }
        sensorWindowQueue.push(value);
        if (sensorWindowQueue.getQueueSize() > SLIDING_WINDOW_SECOND) {
            sensorWindowQueue.pop();
        }
        stateStore.put(value.getUserId(), sensorWindowQueue);
        return sensorWindowQueue.getFeature();
    }

    @Override
    public void close() {

    }
}
