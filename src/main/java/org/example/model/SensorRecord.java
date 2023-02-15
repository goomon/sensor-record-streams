package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class SensorRecord {

    private String userId;
    private long timestamp;
    private Value value;

    @Getter
    @ToString
    @AllArgsConstructor
    static class Value {
        private Accelerometer acc;
        private BloodVolumePressure bvp;
        private ElectrodernalActivity eda;
        private HeartRate hr;
        private Temperature temp;
    }
}
