import GRPC.CleaningRobot;
import GRPC.GRPCServer;
import REST.Beans.Robot.RobotForServer;
import REST.Beans.ServerResponse;
import REST.Client.RobotsREST;
import com.sun.jersey.api.client.ClientResponse;
import Thread.*;
import simulators.BufferData;
import simulators.PM10Simulator;

import java.util.Scanner;

public class RobotProcess {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        RobotForServer newRobot;
        ClientResponse response;
        int id;

        // insert correct ID and create a robot and register to Server
        do{
            System.out.print("Insert Robot's ID: ");
            id = in.nextInt();
            if(id<0)
                System.out.println("INVALID id, please try again!");
            newRobot = new RobotForServer(id);
            response = RobotsREST.addRobotInTheServer(newRobot);
            System.out.println("robot register!");
            if (response.getStatus() == 412){
                System.out.println("Robot already exist!");
            }
        }while (id<0 || response.getStatus()==412);

        if(response.getStatus() == 400) {
            System.out.println("something goes wrong!");
            return;
        }

        /*
        INITIALIZE ROBOT:
            - create Cleaning Robot
            - assign the initial position with its district
            - Fill the list with other robots in the city (inCity)
        */

        System.out.println("[TIME 0] Initialize current robot ...");
        ServerResponse infoFromServer = response.getEntity(ServerResponse.class);
        newRobot.setPosition(infoFromServer.getInitialPosition());
        newRobot.setDistrict(infoFromServer.getInitialPosition().CoordinateToDistrict());

        System.out.println("\tAdd Robot at the district: " + newRobot.getDistrict());
        System.out.println("\tThe initial position is: " + newRobot.getPosition());

        CleaningRobot myRobot = new CleaningRobot(newRobot);
        System.out.println("\tThe offset of clock is: " + myRobot.getOffsetClock());

        for(RobotForServer inCity: infoFromServer.getMyCity()){
            if(inCity.getId() != newRobot.getId())
               myRobot.addRobotInTheCity(inCity);
        }

        System.out.println("\tIn the city there are " + (myRobot.getMyCity().size()+1) + " robots.");

        /* SENSOR:
            - starts acquiring data from its pollution sensor
            - connects with MQTT
         */
        BufferData buffer = new BufferData();
        new PM10Simulator("" + myRobot.getId(), buffer).start();

        /*GRPC
            - say hello to everyone
            - start GRPC server
         */

        new GRPCServer(myRobot).start();
        if(!myRobot.getMyCity().isEmpty()) {
            myRobot.increaseClock();
            System.out.println("[TIME " + myRobot.getClock() + "] Say Hello to everybody");
            new HelloThread(myRobot).start();
        }

        new SensorThread(myRobot, buffer).start();
        new InputThread(myRobot).start();
        new AreYouAliveThread(myRobot).start();

        //new MechanicalThread(myRobot).start();
        /* Cleaning Robot connects as a publisher to the MQTT topics */
        //new MqttPublisherThread(myRobot).start();
    }
}
