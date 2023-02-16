package org.example.model.cache;

import lombok.AllArgsConstructor;
import org.example.model.SensorMeanRecord;

import java.util.Queue;

@AllArgsConstructor
public class SensorWindowQueue {

    private long timestamp;
    private Queue<SensorMeanRecord> sensorMeanRecordQueue;

    public void push(SensorMeanRecord record) {
        sensorMeanRecordQueue.add(record);
    }

    public void pop() {
        sensorMeanRecordQueue.poll();
    }

    public int getQueueSize() {
        return sensorMeanRecordQueue.size();
    }

    public SensorMeanRecord getFeature() {
        return sensorMeanRecordQueue.stream().reduce(SensorMeanRecord::add).orElse(null);
    }
}
