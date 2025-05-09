package Thread;

import GRPC.CleaningRobot;
import REST.Client.RobotsREST;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import proto.classes.RobotServiceGrpc;
import proto.classes.RobotServiceOuterClass;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AreYouAliveThread extends Thread{
    private CleaningRobot robot;
    private CleaningRobot faultyRobot;
    private Map<Integer, List<Integer>> current_city = new HashMap<>();
    private int[] district = {0,0,0,0};

    private int allAnswer;
    public AreYouAliveThread(CleaningRobot robot){
        this.robot=robot;
        updateDistrict(robot.getDistrict(), robot.getPort());
    }

    public synchronized int getAllAnswer(){
        return allAnswer;
    }

    public synchronized void setAllAnswer(int value){
        allAnswer = value;
    }

    public int[] getDistrict(){
        synchronized (district){
            return district;
        }
    }

    public void setDistrict(int i){
        synchronized (district){
            district[i] += 1;
        }
    }
    public void decreaseDistrict(int i){
        synchronized (district){
            district[i]-=1;
        }
    }

    public synchronized void emptyAnswer(){
        allAnswer=0;
    }
    @Override
    public void run() {

        while(!robot.getWantToExit()){
            try {
                Thread.sleep(20 * 1000 );//+ new Random().nextInt(10)*100);
            } catch (InterruptedException io) {
                io.printStackTrace();
            }
            //non voglio uscire, non ne ho rilevato uno, non sono dal meccanico && !robot.getNeedMec()
            if (!robot.getWantToExit() && !robot.getFaultyRobot() && !robot.getAtMec() ) {
                robot.increaseClock();
                for (CleaningRobot c : robot.getMyCity()) {
                    faultyRobot = c;
                    sendAreYouAlive(c);
                }
            }
        }
    }


    public synchronized void decreaseAllAnswer(){
        allAnswer --;
    }
    public void sendAreYouAlive(CleaningRobot c){
        robot.increaseClock();
       // System.out.println("\t[TIME " +robot.getClock()+" ]Sent 'Are You Alive message' to robot " + c.getId());
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + c.getPort()).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.AreYouAliveMex request = RobotServiceOuterClass.AreYouAliveMex.newBuilder()
                .setId(robot.getId()).setClock(robot.getClock()).build();
        stub.areYouAlive(request, new StreamObserver<RobotServiceOuterClass.AreYouAliveMex>() {
            @Override
            public void onNext(RobotServiceOuterClass.AreYouAliveMex value) {
                robot.syncClock(value.getClock());
                //System.out.println("\t[syncTIME " + robot.getClock() + "]Received 'Yes I'm live' message from "+ value.getId());
            }

            @Override
            public void onError(Throwable t) {
                /*The robot detect a Dead robot:
                - notify the Server -> DONE
                - remove the faulty robot from the city -> DONE
                - notify everybody that a robot was crashed.-> DONE
                - balance the city -> DONE
                * */
                channel.shutdown();

                robot.setFaultyRobot(true);
                robot.setFaultyTime(robot.getClock());
                setAllAnswer(robot.getMyCity().size());
                if(robot.getAtMec() || robot.getNeedMec())
                    robot.removeNeedMechanical(faultyRobot.getPort());
                if(robot.getMyCity().size()==1 && robot.alreadyKnow(faultyRobot.getId())){
                    //System.out.println("I'm the only one");
                    robot.removeRobotInTheCity(faultyRobot.getId());
                    System.out.println("[TIME "+ robot.getClock() +"]Robot " + faultyRobot.getId() + " crashed." +
                            "\n\tNow in my city there is only me." );
                    RobotsREST.removeRobot(faultyRobot.getId());
                    robot.setFaultyRobot(false);
                    robot.setFaultyTime(-1);
                }

                for(CleaningRobot c : robot.getMyCity())
                    if (c.getId() != faultyRobot.getId()){
                            adviseAll(c.getPort(), faultyRobot.getId(), robot.getClock(), robot.getFaultyTime());
                    }
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

    public void adviseAll(int senderPort, int faultyID, int clock, int faultyTime){
        robot.increaseClock();
        if(!robot.alreadyKnow(faultyRobot.getId())){return;}
        //System.out.println("\t[TIME " +robot.getClock()+" ]Sent 'Faulty Robot " + faultyID + " '"  );

        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + senderPort).usePlaintext().build();
        RobotServiceGrpc.RobotServiceStub stub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.DeadRobotMex request = RobotServiceOuterClass.DeadRobotMex.newBuilder()
                .setFaultyRobotId(faultyID).setClock(clock).setSenderId(robot.getId()).setFaultyClock(faultyTime).build();
        stub.deadRobot(request, new StreamObserver<RobotServiceOuterClass.BalanceMex>() {
            @Override
            public void onNext(RobotServiceOuterClass.BalanceMex value) {
                robot.syncClock(value.getClock());
                //System.out.println("\t[syncTIME " + robot.getClock() + "]Received 'Balance mex' message from robot" );

                if(!robot.alreadyKnow(faultyRobot.getId())){
                    System.out.println("[TIME "+ robot.getFaultyTime() +"]Robot " + faultyRobot.getId() + " crashed." +
                             "\n\tThe Faulty Robot is already resolve");
                    robot.setFaultyRobot(false);
                    robot.setFaultyTime(-1);
                    return;
                }

                if(value.getStatus()==-1){
                    System.out.println("\tThe Faulty Robot is resolving to another robot.");
                    robot.setFaultyRobot(false);
                    robot.setFaultyTime(-1);
                    return;
                }

                updateDistrict(value.getDistrict(), value.getPort());
                //System.out.println(Arrays.stream(getDistrict()).sum() + " current answer " + getAllAnswer());

                if(getAllAnswer() == Arrays.stream(getDistrict()).sum()) {
                    emptyAnswer();
                    //System.out.println("ricevute tutte le risposte");
                    //System.out.println(isBalance(district));
                    System.out.println("[TIME "+ robot.getClock() +"]Robot " + faultyRobot.getId() + " crashed." +
                            "\n\tNow in my city there are " + (robot.getMyCity().size()) +" robots.");

                    robot.removeRobotInTheCity(faultyRobot.getId());
                    RobotsREST.removeRobot(faultyRobot.getId());
                    balanceCity();

                    robot.setFaultyRobot(false);
                    robot.setFaultyTime(-1);
                }
            }

            @Override
            public void onError(Throwable t) {
                decreaseAllAnswer();
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

    public void updateDistrict(int district, int port){
        synchronized (current_city) {
            current_city.computeIfAbsent(district - 1, k -> new ArrayList<>());
            current_city.get(district - 1).add(port);
            setDistrict(district-1);
        }
    }

    public boolean balanceCity(){

        int maxDistrict = maxDistrict(getDistrict());
        //System.out.println("BEFORE: " + Arrays.toString(district));
        while (!isBalance(getDistrict())){
            decreaseDistrict(maxDistrict);
            //System.out.println(current_city);
            int portToUpdate = current_city.get(maxDistrict).remove(0);
            //System.out.println(Arrays.toString(district));
            //System.out.println("DENTRO BALANCE " + portToUpdate + " " + minDistrict(getDistrict()));
            new UpdateThread(robot, portToUpdate, minDistrict(Arrays.stream(getDistrict()).min().getAsInt())).start();
            updateDistrict(minDistrict(Arrays.stream(getDistrict()).min().getAsInt()), portToUpdate);
        }
        return true;
    }

    public int minDistrict(int minValue){
        for(int i=0; i< district.length; i++){
            if(district[i]==minValue)
                return i+1;
        }
        return -1;
    }

    public int maxDistrict(int[] district){
        int maxDistrict = 0;
        int maxValue = district[0];
        for (int i = 1; i < district.length; i++) {
            if (district[i] > maxValue) {
                maxValue = district[i];
                maxDistrict = i;
            }
        }
        return maxDistrict;
    }

    public int minDistrict(int[] district){
        int minDistrict=0;
        int maxValue=district[0];
        for (int i = 1; i < district.length; i++) {
            if (district[i] < maxValue) {
                maxValue = district[i];
                minDistrict = i;
            }
        }
        return minDistrict;
    }

    public boolean isBalance(int[] district){
        if(Arrays.stream(district).max().getAsInt() - Arrays.stream(district).min().getAsInt()<=1)
            return true;
        return false;
    }
}
