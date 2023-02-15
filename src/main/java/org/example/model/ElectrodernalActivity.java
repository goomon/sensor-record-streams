package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class ElectrodernalActivity {

    private final int hz;
    private final List<Double> value;
}
