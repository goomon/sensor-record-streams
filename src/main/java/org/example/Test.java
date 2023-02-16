package org.example;

import java.util.Date;

public class Test {

    public static void main(String[] args) {
        Long timestamp = 1676434971274L;
        Date date = new Date(timestamp);
        System.out.println("date = " + date);
    }
}
