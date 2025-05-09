package REST.Test;

import REST.Beans.Robot.RobotForServer;
import REST.Beans.ServerResponse;
import REST.Client.RobotsREST;
import com.sun.jersey.api.client.ClientResponse;


import java.awt.*;
import java.util.Scanner;

public class CleaningRobotTest {
    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        RobotForServer newRobot;
        ClientResponse response;
        int id;


        // insert correct ID e PORT, create a robot and register to Server
        do{
            System.out.print("Insert Robot's ID: ");
            id = in.nextInt();
            if(id<0)
                System.out.println("INVALID id, please try again!");
            newRobot = new RobotForServer(id);
            //response = register(newRobot);
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

        /*INITIALIZE ROBOT:
            - create Complete Robot
            - assign the initial position with its district
            - fill the list with other robots in the city (inCity)
        */

        System.out.println("[TIME 0] Initialize current robot ...");
        ServerResponse infoFromServer = response.getEntity(ServerResponse.class);
        newRobot.setPosition(infoFromServer.getInitialPosition());
        newRobot.setDistrict(infoFromServer.getInitialPosition().CoordinateToDistrict());

        System.out.println("\tAdd Robot at the district: " + newRobot.getDistrict());
        System.out.println("\tThe initial position is: " + newRobot.getPosition());

        /*CompleteRobot myRobot = new CompleteRobot(newRobot);
        myRobot.increaseClock();
        System.out.println("[TIME " +myRobot.getClock() + "] Fill the city ");*/

        for(RobotForServer inCity: infoFromServer.getMyCity()){
            if(inCity.getId() != newRobot.getId())
                //myRobot.addRobotInTheCity(inCity);
                System.out.println(inCity);
        }

        RobotsREST.updateRobotCoordinate(newRobot.getId(), 3,3);

        Thread.sleep(60*1000);

        RobotsREST.removeRobot(newRobot.getId());

        /* SENSOR:
            - starts acquiring data from its pollution sensor
            - connects with MQTT
         */
        /*new PM10Simulator("" + myRobot.getId(), myRobot.getBuffer()).start();
        new MqttPublisherThread(myRobot).start();

        /*GRPC
            - say hello to everyone
            - start GRPC server
         */
        /*new GRPCServer(myRobot).start();
        if(!myRobot.getMyCity().isEmpty()) {
            myRobot.increaseClock();
            System.out.println("[TIME " + myRobot.getClock() + "] Say Hello to everybody");
            new HelloThread(myRobot).start();
        }
        new InputThread(myRobot).start();
        new AreYouAliveThread(myRobot).start();

        //new MechanicalThread(myRobot).start();
        /* Cleaning Robot connects as a publisher to the MQTT topics */
    }
}
