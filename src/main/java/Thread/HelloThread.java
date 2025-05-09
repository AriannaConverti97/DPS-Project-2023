package Thread;

import GRPC.CleaningRobot;
import REST.Beans.Robot.RobotForServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.util.concurrent.TimeUnit;


public class HelloThread extends Thread{
    CleaningRobot robot;

    public HelloThread(CleaningRobot robot){
        this.robot = robot;
    }
    @Override
    public void run() {
       /* try {
            Thread.sleep(30*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        for(CleaningRobot r : robot.getMyCity()){
          sentHello(r);
        }
    }

    public void sentHello(RobotForServer receiver){
        robot.increaseClock();
        //System.out.println("\t[TIME " + robot.getClock()+ "]Sent hello to robot " + receiver.getId());
        final ManagedChannel channel = ManagedChannelBuilder.
                forTarget("localhost:" + receiver.getPort()).
                usePlaintext()
                .build();

        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);
        RobotServiceOuterClass.HelloRobot request = RobotServiceOuterClass.HelloRobot.newBuilder()
                .setId(robot.getId())
                .setPort(robot.getPort())
                .setDistrict(robot.getDistrict())
                .setPosition( RobotServiceOuterClass.HelloRobot.Coordinate.newBuilder()
                        .setX(robot.getPosition().getX())
                        .setY(robot.getPosition().getY()).build())
                .setClock(robot.getClock())
                .build();
        stub.sayHello(request, new StreamObserver<RobotServiceOuterClass.ACK>() {
            @Override
            public void onNext(RobotServiceOuterClass.ACK value) {
                robot.syncClock(value.getClock());
                //System.out.println("\t[SyncTIME " + robot.getClock() + "] Received ACKHello from " + value.getId() + "." + value.getClock());
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
