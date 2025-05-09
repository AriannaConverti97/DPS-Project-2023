package GRPC;

import REST.Beans.Coordinate;
import REST.Beans.Robot.RobotForServer;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;

public class CleaningRobot extends RobotForServer {

    //LAMPORT CLOCK
    private int lamportClock;
    private int offsetClock;
    private Object clockLock;

    //CITY
    private volatile ArrayList<CleaningRobot> myCity;

    //EXIT
    private boolean wantToExit;
    private Object exitLock;

    //FAULTY ROBOT
    private boolean faultyRobot;
    private Object faultyObject;
    private volatile int faultyTime;
    private Object faultyTimeObject;

    //MECHANICAL
    private int timeFix;
    private boolean needMec;
    private Object needMecLock;
    private boolean atMec;
    private Object atMecLock;

    private int countResponse;
    private Object responseLock;
    private int maxResponse;
    private ArrayList<Integer> needMechanical;

    private Object waitLock;

    //MQTT
    private final String clientId = MqttClient.generateClientId();

    public CleaningRobot(RobotForServer r){
        super(r);
        lamportClock = 0;
        offsetClock = (int)((Math.random()*6)+1);
        clockLock = new Object();
        myCity = new ArrayList<>();
        wantToExit =false;
        exitLock =new Object();
        faultyRobot=false;
        faultyObject = new Object();
        faultyTime=-1;
        faultyTimeObject = new Object();
        timeFix=0;
        needMec=false;
        needMecLock=new Object();
        atMec=false;
        atMecLock = new Object();
        needMechanical=new ArrayList<>();
        countResponse=1;
        responseLock=new Object();
        waitLock= new Object();
    }

    public CleaningRobot(int id, String localhost, int port, Coordinate coordinate, int district) {
        super(id, localhost, port, coordinate, district);
    }

    //MY CITY
    public ArrayList<CleaningRobot> getMyCity(){
        return new ArrayList<>(myCity);
    }

    public void addRobotInTheCity(RobotForServer r){
        synchronized (myCity){
            myCity.add(new CleaningRobot(r));
        }
    }

    public void  removeRobotInTheCity(int id){
        synchronized (myCity){
            myCity.removeIf(cleaningRobot -> (cleaningRobot.getId()==id));
        }
    }

    public boolean alreadyKnow(int id) {
        for (CleaningRobot r : getMyCity()){
            if(r.getId() == id)
                return true;
        }
        return false;
    }

    // Lamport CLOCK
    public int getOffsetClock(){
        return offsetClock;
    }
    public void increaseClock(){
        synchronized (clockLock){
            lamportClock += offsetClock;
        }
    }

    public int getClock(){
        synchronized (clockLock){
            return lamportClock;
        }
    }

    public void setClock(int newClock){
        synchronized (clockLock){
            this.lamportClock = newClock;
        }
    }

    public void syncClock(int otherClock){
        synchronized (clockLock){
           int myClock = getClock();
           setClock(Math.max(myClock, otherClock)+1);
        }
    }

    //EXIT
    public void setWantToExit(boolean value){
        synchronized (exitLock){
            wantToExit =value;
        }
    }

    public boolean getWantToExit(){
        synchronized (exitLock){
            return wantToExit;
        }
    }

    public void setFaultyRobot(boolean b) {
        synchronized (faultyObject){
            faultyRobot=b;
        }
    }

    public boolean getFaultyRobot(){
        synchronized (faultyObject){
            return faultyRobot;
        }
    }

    public int getFaultyTime() {
        synchronized (faultyTimeObject){
            return faultyTime;
        }
    }

    public void setFaultyTime(int newTime){
        synchronized (faultyTimeObject){
            faultyTime = newTime;
        }
    }

    //MECHANICAL
    public void setNeedMec(boolean value){
        synchronized (needMecLock){
            needMec = value;
        }
    }

    public boolean getNeedMec(){
        synchronized (needMecLock){
            return needMec;
        }
    }

    public void setAtMec(boolean value){
        synchronized (atMecLock){
            atMec=value;
        }
    }

    public boolean getAtMec(){
        synchronized (atMecLock){
            return atMec;
        }
    }

    public void setTimeFix(int timeFix) {
        this.timeFix = timeFix;
    }

    public int getTimeFix() {
        return timeFix;
    }

    public int getMaxCount() {
        synchronized (responseLock) {
            return maxResponse;
        }
    }

    public void setMaxCount(int maxResponse) {
        synchronized (responseLock) {
            this.maxResponse = maxResponse;
        }
    }

    public int getCountResponse() {
        synchronized (responseLock) {
            return countResponse;
        }
    }

    public void addNeedMechanical(int port){
        synchronized (needMechanical){
            needMechanical.add(port);
        }
    }

    public ArrayList<Integer> getNeedMechanical(){
        synchronized (needMechanical){
            return needMechanical;
        }
    }

    public void removeNeedMechanical(int port){
        synchronized (needMechanical){
            setMaxCount(getMaxCount()-1);
            needMechanical.removeIf(n-> n==port);
            synchronized (waitLock){
                waitLock.notifyAll();
            }
            //System.out.println(needMechanical.removeIf(n-> n==port));
           //System.out.println(countResponse + " su " + maxResponse);
        }
    }

    public void increaseResponse() {
        synchronized (responseLock) {
            countResponse++;
            synchronized (waitLock) {
                waitLock.notifyAll();
            }
        }
        //System.out.println(countResponse + " su " + maxResponse);
    }
    public void resetResponse(){
        synchronized (responseLock){
            countResponse=0;
        }
    }
   public ArrayList<Integer> getAndReset(){
        synchronized (needMechanical){
            ArrayList<Integer> copy = new ArrayList<>(needMechanical);
            needMechanical.clear();
            return copy;
        }
   }

   public synchronized Object getWaitLock(){
        return waitLock;
   }
   //MQTT
    public String getMqttId(){
        return clientId;
    }

}
