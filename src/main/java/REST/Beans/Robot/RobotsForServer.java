package REST.Beans.Robot;

import REST.Beans.Coordinate;
import REST.Beans.Data.DataFromRobot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD)
public class RobotsForServer {
    /*
     - list of robot
     - insert a robot
     - remove a robot
     - update a robot district
     */
    private ArrayList<RobotForServer> robotsList;
    private static RobotsForServer instance;

    private RobotsForServer(){
        robotsList = new ArrayList<>();
    }

    //singleton
    public synchronized static RobotsForServer getInstance(){
        if(instance==null)
            instance = new RobotsForServer();
        return instance;
    }

    public int[] getRobotDistrict(){
        int[] city = {0,0,0,0};
        for(RobotForServer r : robotsList){
            city[r.getPosition().CoordinateToDistrict()-1] +=1;
        }
        return city;
    }

    public synchronized ArrayList<RobotForServer> getRobotsList(){
        return new ArrayList<>(robotsList);
    }

    public synchronized void add(RobotForServer r) throws InterruptedException {
        //Thread.sleep(60*1000);
        if(robotsList.size()==0)
            r.setPosition(new Coordinate(0,0));
        else
            r.setPosition(new Coordinate(getRobotDistrict()));

        r.setDistrict(r.getPosition().CoordinateToDistrict());
        robotsList.add(r);
        System.out.println("\033[32mAdd robot "+ r.getId() +" to the city in district "
                + r.getDistrict() + " ( Current city "  + Arrays.toString(getRobotDistrict()) + " ).\033[0m"
        );
    }

    public RobotForServer getRobotByID(int ID){
        ArrayList<RobotForServer> robotsCopy = getRobotsList();
        for(RobotForServer r: robotsCopy)
            if(r.getId() == ID)
                return r;
        return null;
    }

    public synchronized void removeRobot(int id) throws InterruptedException {
        //Thread.sleep(60*1000);
        robotsList.removeIf(n -> (n.getId() == id));
        System.out.println("\033[31mRemove robot " + id + " to the city ( Current city "
                + Arrays.toString(getRobotDistrict())+ " ).\033[0m");
        DataFromRobot.getInstance().removeById(id);
    }

    public synchronized void updateCoordinate(int id, Coordinate newCoordinate) throws InterruptedException {
        //Thread.sleep(60*1000);
        RobotForServer currentRobot = getRobotByID(id);
        Coordinate old = currentRobot.getPosition();
        DataFromRobot.getInstance().removeById(id);
        currentRobot.setPosition(newCoordinate);
        currentRobot.setDistrict(newCoordinate.CoordinateToDistrict());

        System.out.println("\033[33mUpdate Coordinate for robot " + id + " from " + old + " to " + newCoordinate+
                ". The new district is " + newCoordinate.CoordinateToDistrict() +
                ". ( Current city: " + Arrays.toString(getRobotDistrict()) + " ).\033[0m");
    }

}
