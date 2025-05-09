package Thread;

import GRPC.CleaningRobot;
import REST.Client.RobotsREST;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.util.concurrent.TimeUnit;

public class ExitThread extends Thread{
    CleaningRobot robot;
    public ExitThread(CleaningRobot robot){
        this.robot = robot;
    }
    @Override
    public void run() {
        /*try {
            Thread.sleep(30*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        try {
            new MqttClient("tcp://localhost:1883", robot.getMqttId()).unsubscribe("greenfield/pollution/district" + robot.getDistrict());
        }catch (MqttException me){}
        robot.increaseClock();
        System.out.println("[TIME " + robot.getClock()+ "] Sent goodbye to robots");

        for(CleaningRobot r : robot.getMyCity()){
            sayGoodbye(r.getPort());
        }
        RobotsREST.removeRobot(robot.getId());
        System.exit(0);
    }

    public void sayGoodbye(int port){
        robot.increaseClock();

        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + port).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
        RobotServiceOuterClass.ByeMex request = RobotServiceOuterClass.ByeMex.newBuilder()
                .setId(robot.getId())
                .setClock(robot.getClock())
                .build();
        stub.goodbye(request, new StreamObserver<RobotServiceOuterClass.ByeMex>() {
            @Override
            public void onNext(RobotServiceOuterClass.ByeMex value) {
                robot.syncClock(value.getClock());
                //System.out.println("[syncTIME "+ robot.getClock()+"]Receive goodbye Ack from " + value.getId());
                //robot.removeRobotInTheCity(value.getId());
                //System.out.println(robot.getMyCity());
            }

            @Override
            public void onError(Throwable t) {
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });
        try {
            channel.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Await termination error");
        }
    }
}
