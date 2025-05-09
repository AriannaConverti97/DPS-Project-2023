package Thread;

import GRPC.CleaningRobot;
import simulators.BufferData;
import simulators.Measurement;
import java.util.List;

public class SensorThread extends Thread{
    private CleaningRobot robot;
    private BufferData buffer;

    public SensorThread(CleaningRobot robot, BufferData data){
        this.robot =robot;
        this.buffer=data;
    }
    @Override
    public void run() {

        while(!robot.getWantToExit()) {
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            publish(buffer.readAllAndClean());

        }
    }

    public void publish(List<Measurement> buffer ){
        new MqttPublisherThread(robot, buffer).start();
    }
}
