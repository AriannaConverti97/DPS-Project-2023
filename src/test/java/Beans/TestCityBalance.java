package Beans;

import GRPC.CleaningRobot;
import REST.Beans.Coordinate;
import REST.Beans.Robot.RobotForServer;
import Thread.AreYouAliveThread;
import Server.AdminServer;
//import Threads.HandleCrashThread;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestCityBalance {
    CleaningRobot robot;
    CleaningRobot robot1;
    public TestCityBalance(){
        robot = new CleaningRobot( new RobotForServer(0));
        robot.setDistrict(1);
        robot1 = new CleaningRobot(new RobotForServer(1));
        robot.setDistrict(1);
        robot.addRobotInTheCity(robot1);
        robot1.addRobotInTheCity(robot);
    }
    @Test
    public void checkIfTheCityAreBalance(){
        robot = new CleaningRobot( new RobotForServer(0));
        robot.setDistrict(1);
        robot1 = new CleaningRobot(new RobotForServer(1));
        robot1.setDistrict(2);
        CleaningRobot robot2 = new CleaningRobot(new RobotForServer(2));
        robot2.setDistrict(2);
        CleaningRobot robot3 = new CleaningRobot(new RobotForServer(3));
        robot3.setDistrict(3);
        CleaningRobot robot4 = new CleaningRobot(new RobotForServer(4));
        robot4.setDistrict(3);
        CleaningRobot robot5 = new CleaningRobot(new RobotForServer(5));
        robot5.setDistrict(3);
        CleaningRobot robot6 = new CleaningRobot(new RobotForServer(6));
        robot6.setDistrict(3);

        robot.addRobotInTheCity(robot1);
        robot.addRobotInTheCity(robot2);
        robot.addRobotInTheCity(robot3);
        robot.addRobotInTheCity(robot4);
        robot.addRobotInTheCity(robot5);
        robot.addRobotInTheCity(robot6);

        int[] city ={1,2,1,0};
        int[] city1 = {0,1,1,1};
        int[] city2 = {1,2,2,0};

        AreYouAliveThread hthread = new AreYouAliveThread(robot);
        //assert(hthread.(city)==2);
        //assert(hthread.returnMaxDistrict(city)==1);
        /*assert(hthread.isBalance(city)==false);
        assert(hthread.isBalance(city1)==true);
        assert(hthread.isBalance(city2)==false);*/
        assert (hthread.balanceCity());
    }

}
