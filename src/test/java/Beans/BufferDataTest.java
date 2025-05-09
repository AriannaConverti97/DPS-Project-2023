package Beans;

import org.junit.jupiter.api.Test;
import simulators.BufferData;
import simulators.Measurement;

import java.sql.Timestamp;

public class BufferDataTest {
    @Test
    public void checkDataBuffer(){
        BufferData buffer = new BufferData();
        for(int i=0; i<12; i++)
            buffer.addMeasurement(new Measurement("" + i, "PM10" , i, i));
        System.out.println(buffer.getData());
        System.out.println(buffer.readAllAndClean());
        System.out.println(buffer.getAverages());
    }
}
