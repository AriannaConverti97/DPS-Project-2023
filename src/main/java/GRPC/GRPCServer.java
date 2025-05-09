package GRPC;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GRPCServer extends Thread{
    CleaningRobot robot;

    public GRPCServer(CleaningRobot robot){
        this.robot =robot;
    }

    @Override
    public void run() {
        Server server = ServerBuilder.forPort(robot.getPort())
                .addService(new RobotServiceImpl(robot)).build();
        try{
            server.start();
            robot.increaseClock();
            //System.out.println("[TIME " + robot.getClock() +"] GRPC server started!");
            server.awaitTermination();

        }catch (IOException io){
            throw new RuntimeException(io);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
