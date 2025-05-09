package GRPC;

import REST.Beans.Coordinate;
import REST.Beans.Robot.RobotForServer;
import REST.Client.RobotsREST;
import com.sun.jersey.api.client.Client;
import io.grpc.stub.StreamObserver;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.util.Arrays;

public class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase{
    CleaningRobot robot;
    public RobotServiceImpl(CleaningRobot robot){
        this.robot = robot;
    }

    @Override
    public void sayHello(RobotServiceOuterClass.HelloRobot request, StreamObserver<RobotServiceOuterClass.ACK> responseObserver) {
        robot.syncClock(request.getClock());
        System.out.println("[SyncTIME " +robot.getClock() + "]Hello from " + request.getId()+ ". In my city there are " + (robot.getMyCity().size()+2) + " robots.");
        if(!robot.alreadyKnow(request.getId()))
            robot.addRobotInTheCity(
                    new CleaningRobot(
                            request.getId(),
                            "localhost" ,
                            request.getPort(),
                            new Coordinate(
                                    request.getPosition().getX(),
                                    request.getPosition().getY()),
                            request.getDistrict())
            );

        robot.increaseClock();
        //System.out.println("\t[TIME " + robot.getClock()+ "]Send HelloACK");

        RobotServiceOuterClass.ACK ack= RobotServiceOuterClass.ACK.newBuilder().setId(robot.getId()).setClock(robot.getClock()).build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }

    @Override
    public void goodbye(RobotServiceOuterClass.ByeMex request, StreamObserver<RobotServiceOuterClass.ByeMex> responseObserver) {
        robot.syncClock(request.getClock());
        System.out.println("[SyncTIME " +robot.getClock()+ "]Received goodbye from " + request.getId() );

        robot.removeRobotInTheCity(request.getId());
        //System.out.println(robot.getMyCity());

        robot.increaseClock();
        //System.out.println("\t[TIME " + robot.getClock()+ "]Send 'bye messageACK'");

        RobotServiceOuterClass.ByeMex bye= RobotServiceOuterClass.ByeMex.newBuilder()
                .setId(robot.getId())
                .setClock(robot.getClock()).build();

        responseObserver.onNext(bye);
        responseObserver.onCompleted();
    }

    @Override
    public void areYouAlive(RobotServiceOuterClass.AreYouAliveMex request, StreamObserver<RobotServiceOuterClass.AreYouAliveMex> responseObserver) {
        robot.syncClock(request.getClock());
        //System.out.println("\t[SyncTIME " +robot.getClock()+ "]Received 'Are you alive' from " + request.getId() + "." + request.getClock());

        RobotServiceOuterClass.AreYouAliveMex response = RobotServiceOuterClass.AreYouAliveMex.newBuilder()
                .setId(robot.getId()).setClock(robot.getClock()).build();
        robot.increaseClock();
        //System.out.println("\t[TIME " + robot.getClock()+ "]Send 'I'm Alive' to robot.");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deadRobot(RobotServiceOuterClass.DeadRobotMex request, StreamObserver<RobotServiceOuterClass.BalanceMex> responseObserver) {
        robot.syncClock(request.getClock());
        //System.out.println("[SyncTIME " +robot.getClock()+ "]Received 'Faulty Robot "+request.getFaultyRobotId() +"' from robot " + request.getSenderId());

        RobotServiceOuterClass.BalanceMex response;
       // System.out.println(""+robot.getFaultyRobot() + robot.getFaultyTime() + " : " + request.getClock());

        // Ho cambiato il segno da < a >
        // 1 OK ; -1 NO
        //ritorna OK se non ho rilevato un Faulty robot o me ne sono accorto dopo (mioClock>altri)

        if(robot.getFaultyTime()==-1 ) {
            System.out.println("Received a faulty robot.");
            robot.removeRobotInTheCity(request.getFaultyRobotId());
            response = RobotServiceOuterClass.BalanceMex.newBuilder()
                    .setStatus(1).setDistrict(robot.getDistrict()).setClock(robot.getClock()).setPort(robot.getPort()).build();
        }
        else if(robot.getFaultyTime()!=-1 && robot.getFaultyTime()>request.getFaultyClock() ){
            System.out.println("Received a faulty robot.");
            robot.removeRobotInTheCity(request.getFaultyRobotId());
            response = RobotServiceOuterClass.BalanceMex.newBuilder()
                    .setStatus(1).setDistrict(robot.getDistrict()).setClock(robot.getClock()).setPort(robot.getPort()).build();
        }else{
            response = RobotServiceOuterClass.BalanceMex.newBuilder()
                    .setStatus(-1).setDistrict(robot.getDistrict()).setClock(robot.getClock()).setPort(robot.getPort()).build();
        }

        robot.increaseClock();
        //System.out.println("\t[TIME " + robot.getClock()+ "]Send 'My District' to robot and status " + response.getStatus());
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void balanceCity(RobotServiceOuterClass.RobotNewCoordinate request, StreamObserver<RobotServiceOuterClass.RobotNewCoordinateResponse> responseObserver){
        robot.syncClock(request.getClock());
        System.out.println("[SyncTIME " +robot.getClock()+ "]Received 'New Coordinate' ");
        try {
            new MqttClient("tcp://localhost:1883", robot.getMqttId()).unsubscribe("greenfield/pollution/district" + robot.getDistrict());
        }catch (MqttException me){}
        robot.setDistrict(request.getNewDistrict());
        robot.setPosition(new Coordinate(request.getNewDistrict()));
        System.out.println("\tMy new Coordinate is " + robot.getPosition() + " in district " + robot.getDistrict());
        RobotsREST.updateRobotCoordinate(robot.getId(), robot.getPosition().getX(), robot.getPosition().getY());

        RobotServiceOuterClass.RobotNewCoordinateResponse response = RobotServiceOuterClass.RobotNewCoordinateResponse.newBuilder()
                .setId(robot.getId()).setClock(robot.getClock()).build();
        robot.increaseClock();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    //MECHANICAL IMPLEMENTATION

    @Override
    public void mechanicRequest(RobotServiceOuterClass.MecReq request, StreamObserver<RobotServiceOuterClass.MecResp> responseObserver) {
        robot.syncClock(request.getClock());
        //System.out.println("[SyncTIME " +robot.getClock()+ "]Received 'Request mechanic' ");
        int answer =0;

        if(request.getId()==robot.getId()){
            answer=1;
        }
        if(!robot.getNeedMec() && !robot.getAtMec()){
            answer=1;
        }else if(robot.getAtMec()){
            System.out.println("\tI'm at the mechanic. Add robot " + request.getId()+" in the queue. " );
            robot.addNeedMechanical(request.getPort());
            answer=-1;

        }else if(robot.getNeedMec() && !robot.getAtMec()){
            if(request.getFixClock()<= robot.getTimeFix()){
                answer=1;
            }else{
                System.out.println("\tI do first. Add robot " + request.getId() + " in the queue." );
                robot.addNeedMechanical(request.getPort());
                answer=-1;
            }
        }
        responseObserver.onNext(RobotServiceOuterClass.MecResp.newBuilder().setId(robot.getId()).setClock(robot.getClock()).setStatus(answer).build());
        responseObserver.onCompleted();
    }

    @Override
    public void mechanicReply(RobotServiceOuterClass.MecResp request, StreamObserver<RobotServiceOuterClass.ACK> responseObserver) {
        robot.syncClock(request.getClock());
        System.out.println("[SyncTIME " +robot.getClock()+ "]Received 'Release mechanic' ");
        robot.increaseResponse();
        responseObserver.onNext(RobotServiceOuterClass.ACK.newBuilder().setClock(robot.getClock()).setId(robot.getId()).build());
        responseObserver.onCompleted();
    }
}
