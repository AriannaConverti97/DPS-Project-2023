package Thread;

import GRPC.CleaningRobot;
import REST.Beans.Robot.RobotForServer;

import java.util.Random;
import java.util.Scanner;

public class InputThread extends Thread{
    CleaningRobot robot;
    Scanner in;
    private String color = "\033[32m";
    private String endColor = "\033[0m";
    public InputThread(CleaningRobot robot){
        this.robot=robot;
    }
    @Override
    public void run() {
        Thread rollADice= new Thread(()-> {
            while (true){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int probability = new Random().nextInt(10 - 1 + 1) + 1;

                if (!robot.getNeedMec() && !robot.getAtMec() && !robot.getWantToExit()) {
                    //System.out.println("Probability to broke: " + probability);
                    if (probability == 1) {
                        System.out.println("[TIME " + robot.getClock() +"]The robot need to fix.");
                        new MechanicalThread(robot, robot.getMyCity()).start();
                    }
                }
            }
        });

        System.out.println( color + "Choose and option: " +
                "\n Fix: robot wants to go to the mechanical" +
                "\n Quit: robot wants to exit from the city"+ endColor);

        rollADice.start();

        do {
            in = new Scanner(System.in);
            String choice = in.nextLine();

            switch (choice.toLowerCase()) {
                case "fix":
                    System.out.println("You choose FIX.");
                    if (robot.getNeedMec() || robot.getAtMec()) {
                        System.out.println("The robot is doing this operation!");
                    } else if(robot.getWantToExit()) {
                        System.out.println("The robot wants to exit, this operation can't happen.");
                    } else {
                        new MechanicalThread(robot, robot.getMyCity()).start();
                    }
                    break;
                case "quit":
                    System.out.println(color + "You choose QUIT." + endColor);
                    if(robot.getWantToExit()){
                        System.out.println(color + "The robot is doing this operation"+ endColor);
                    } else if(robot.getAtMec()){
                        System.out.println(color + "The robot is at the mechanical. This operation will do quickly." + endColor);
                        robot.setWantToExit(true);
                    }else {
                        robot.setWantToExit(true);
                        ExitThread exitThread = new ExitThread(robot);
                        exitThread.start();
                    }
                    break;
                default:
                    System.out.println(color + "The choice is not correct, please try again!" + endColor);
            }
        }while(true);
    }
}
