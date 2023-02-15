package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SensorMeanRecord {

    private String userId;
    private long timestamp;
    private Axis acc;
    private double bvp;
    private double eda;
    private double hr;
    private double temp;

    public SensorMeanRecord() {
    }

    public SensorMeanRecord(SensorRecord sensorRecord) {
        this.userId = sensorRecord.getUserId();
        this.timestamp = sensorRecord.getTimestamp() / 1000 * 1000;
        double x = 0, y = 0, z = 0;
        int accHz = sensorRecord.getValue().getAcc().getHz();
        for (Axis axis : sensorRecord.getValue().getAcc().getValue()) {
            x += axis.getX();
            y += axis.getY();
            z += axis.getZ();
        }
        this.acc = new Axis(x / accHz, y / accHz, z / accHz);
        this.bvp = sensorRecord.getValue().getBvp().getValue()
                .stream().mapToDouble(Double::doubleValue).sum() / sensorRecord.getValue().getBvp().getHz();
        this.eda = sensorRecord.getValue().getEda().getValue()
                .stream().mapToDouble(Double::doubleValue).sum() / sensorRecord.getValue().getEda().getHz();
        this.hr = sensorRecord.getValue().getHr().getValue()
                .stream().mapToDouble(Double::doubleValue).sum() / sensorRecord.getValue().getHr().getHz();
        this.temp = sensorRecord.getValue().getTemp().getValue()
                .stream().mapToDouble(Double::doubleValue).sum() / sensorRecord.getValue().getTemp().getHz();
    }

    public static SensorMeanRecord add(SensorMeanRecord s1, SensorMeanRecord s2) {
        SensorMeanRecord sensorMeanRecord = new SensorMeanRecord();
        sensorMeanRecord.setUserId(s1.userId);
        sensorMeanRecord.setTimestamp(s1.getTimestamp());
        sensorMeanRecord.setAcc(Axis.add(s1.getAcc(), s2.getAcc()));
        sensorMeanRecord.setBvp(s1.getBvp() + s2.getBvp());
        sensorMeanRecord.setEda(s1.getEda() + s2.getEda());
        sensorMeanRecord.setHr(s1.getHr() + s2.getHr());
        sensorMeanRecord.setTemp(s1.getTemp() + s2.getTemp());
        return sensorMeanRecord;
    }
}
