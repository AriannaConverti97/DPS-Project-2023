package Thread;

import GRPC.CleaningRobot;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.util.concurrent.TimeUnit;

public class UpdateThread extends Thread{
    private int portToUpdate;
    private int newDistrict;
    private CleaningRobot robot;
    public UpdateThread(CleaningRobot robot, int port, int newDistrict){
        this.portToUpdate =port;
        this.newDistrict = newDistrict;
        this.robot = robot;
    }

    @Override
    public void run() {
        robot.increaseClock();
        System.out.println("[TIME " +robot.getClock()+" ] Need to balance city " );

        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + portToUpdate).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.RobotNewCoordinate request = RobotServiceOuterClass.RobotNewCoordinate.newBuilder()
                .setId(robot.getId()).setClock(robot.getClock()).setNewDistrict(newDistrict).
                build();

        stub.balanceCity(request, new StreamObserver<RobotServiceOuterClass.RobotNewCoordinateResponse>() {
            @Override
            public void onNext(RobotServiceOuterClass.RobotNewCoordinateResponse value) {
                robot.syncClock(value.getClock());
                //System.out.println("\t[syncTIME " + robot.getClock() + "]Received 'Balance mex' message from robot" );
                channel.shutdown();
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
