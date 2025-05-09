package Thread;

import GRPC.CleaningRobot;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MechanicalThread extends Thread{
    CleaningRobot robot;
    ArrayList<CleaningRobot> currentCity;


    public MechanicalThread(CleaningRobot robot, ArrayList<CleaningRobot> currentCity){
        this.robot = robot;
        this.currentCity=currentCity;
        currentCity.add(robot);
    }

    @Override
    public void run() {
        robot.setNeedMec(true);
        robot.setTimeFix(robot.getClock());
        robot.resetResponse();
        robot.setMaxCount(currentCity.size());

        //invio a tutti che voglio il meccanico
        for(CleaningRobot c: currentCity){
            requestMechanical(c.getPort());
        }

        //mi metto in attesa

            while(robot.getCountResponse()<robot.getMaxCount()) {
                synchronized (robot.getWaitLock()){
                try {
                    robot.getWaitLock().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        goToMechanical();

        if(robot.getWantToExit()){
            new ExitThread(robot).start();
        }
    }

    public void requestMechanical(int port){
        robot.increaseClock();
        //System.out.println("[TIME " +robot.getClock()+" ] Need Mechanical" );

        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + port).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.MecReq request = RobotServiceOuterClass.MecReq.newBuilder()
                .setId(robot.getId()).setFixClock(robot.getTimeFix()).setClock(robot.getClock()).setPort(robot.getPort())
                .build();

        stub.mechanicRequest(request, new StreamObserver<RobotServiceOuterClass.MecResp>() {
            @Override
            public void onNext(RobotServiceOuterClass.MecResp value) {
                robot.syncClock(value.getClock());
                if (value.getStatus() == 1){
                    robot.increaseResponse();
                }else{
                    System.out.println("\tThe mechanic is busy.");
                }
            }

            @Override
            public void onError(Throwable t) {
                robot.increaseResponse();
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });
        try{
            channel.awaitTermination(10, TimeUnit.SECONDS);
        }catch (Exception e){

        }
    }

    public void goToMechanical() {
        robot.setAtMec(true);
        System.out.println("[TIME " + robot.getClock() + "]At mechanical... ");
        try {
            Thread.sleep(30 * 1000);
        }catch (Exception e){}

        System.out.println("[TIME " +robot.getClock() +"]Exit mechanical. Weak up all the waiting robots.");

        robot.setAtMec(false);

        if(robot.getNeedMechanical().size()==0){
            System.out.println("\tNobody in waiting.");
            robot.setNeedMec(false);
            return;
        }

        for(int port : robot.getAndReset()){
            releaseMechanical(port);
        }

        robot.setNeedMec(false);
    }

    public void releaseMechanical(int port){
        robot.increaseClock();

        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + port).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.MecResp request = RobotServiceOuterClass.MecResp.newBuilder().
                setId(robot.getId()).setClock(robot.getClock()).setStatus(1).build();
        stub.mechanicReply(request, new StreamObserver<RobotServiceOuterClass.ACK>() {
            @Override
            public void onNext(RobotServiceOuterClass.ACK value) {
                robot.syncClock(value.getClock());
            }

            @Override
            public void onError(Throwable t) {
                robot.increaseResponse();
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
