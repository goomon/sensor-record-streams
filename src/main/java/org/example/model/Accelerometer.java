package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class Accelerometer {

    private int hz;
    private List<Axis> value;


}
