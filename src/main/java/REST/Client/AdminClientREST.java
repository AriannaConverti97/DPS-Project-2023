package REST.Client;

import REST.Beans.Robot.RobotForServer;
import REST.Beans.Robot.RobotsForServer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Arrays;
import java.util.Scanner;

public class AdminClientREST {
    static final private String serverAddress = "http://localhost:1337";
    static final private Client client = Client.create();
    public static void main(String[] args) {
        welcome();
        help();

        Scanner stdin = new Scanner(System.in);
        int scelta = -1;

        do{
            System.out.print("> ");
            scelta = stdin.nextInt();

            switch (scelta){
                case 1:
                    showCurrentRobotsList();
                    break;
                case 2:
                    int id =0;
                    int n=1;
                    do{
                        if(id<0)
                            System.out.println("Invalid ID, please try again!");
                        System.out.print("Insert the robot's ID: ");
                        id = stdin.nextInt();
                    }while (id<0);
                    do{
                        if(n<1)
                            System.out.println("Invalid number, please try again!");
                        System.out.print("Insert how many number do you want: ");
                        n = stdin.nextInt();
                    }while (n<1);
                    showRobotData(id,n);
                    break;
                case 3:
                    long t1= 0;
                    long t2 = 0;

                    do {
                        if (t1<0)
                            System.out.println("Invalid timestamp, please try again!");
                        System.out.print("Please, enter the LATEST timestamp: ");
                        t1 = stdin.nextLong();
                    } while (t1 < 0);
                    do {
                        if (t2<0)
                            System.out.println("Invalid timestamp, please try again!");
                        if (t2>0 && t1 > t2)
                            System.out.println("IMPOSSIBLE: The second timestamp is lower than the first. Please, try again!");
                        System.out.print("Please, enter the EARLIEST timestamp: ");
                        t2 = stdin.nextLong();
                    } while (t2 < 0 || t1 > t2);
                    showDataBetween(t1,t2);
                    break;
                case 4:
                    help();
                    break;
                case 5:
                    bye();
                    break;
                default:
                    System.out.println("Invalid number, please try again!\n");
                    help();
            }
        }while(scelta != 5);
    }

    public static void showRobotData(int id, int n){
        String getPath = "/data/averageDataByRobot";
        ClientResponse clientResponse = getRequest(client, serverAddress + getPath, id ,n);
        System.out.println(clientResponse.toString());
        if(clientResponse.getStatus()!=200){
            System.out.println("Robot doesn't exist");
        }else
            System.out.println("The average for Robot " + id + " is " + clientResponse.getEntity(String.class));
    }

    public static void showDataBetween(long t1, long t2){
        String getPath ="/data/averageBetweenTwoTimestamps";
        ClientResponse clientResponse = getRequest(client, serverAddress + getPath, t1,t2);
        System.out.println(clientResponse.toString());
        if(clientResponse.getStatus()!=200)
            System.out.println("No data for this interval.");
        else
            System.out.println(clientResponse.getEntity(String.class));
    }

    public static void showCurrentRobotsList() {
        String getPath = "/robots";
        ClientResponse clientResponse = getRequest(client, serverAddress + getPath);
        RobotsForServer robots = clientResponse.getEntity(RobotsForServer.class);
        System.out.println("Robots List");
        for (RobotForServer r : robots.getRobotsList()){
            System.out.println(r.toString());
        }
    }

    public static ClientResponse getRequest(Client client, String url){
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server!");
            return null;
        }
    }

    public static ClientResponse getRequest(Client client, String url, int id, int n){
        WebResource webResource = client.resource(url + "/"  + id + "/" + n);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server");
            return null;
        }
    }

    public static ClientResponse getRequest(Client client, String url, long t1, long t2){
        WebResource webResource = client.resource(url + "/" + t1 + "/" + t2);
        try{
            return webResource.type("application/json").get(ClientResponse.class);
        }catch (ClientHandlerException e){
            System.out.println("Unavailable server");
            return null;
        }
    }

    public static void welcome(){
        System.out.println("  _____                      __ _      _     _\n " +
                "/ ____|                    / _(_)    | |   | | \n" +
                "| |  __ _ __ ___  ___ _ __ | |_ _  ___| | __| | \n" +
                "| | |_ | '__/ _ \\/ _ \\ '_ \\|  _| |/ _ \\ |/ _` | \n" +
                "| |__| | | |  __/  __/ | | | | | |  __/ | (_| |\n" +
                " \\_____|_|  \\___|\\___|_| |_|_| |_|\\___|_|\\__,_| \n"
        );
    }

    public static void help(){
        System.out.println("\nChoose an option and press enter:");
        System.out.println("1.Show all the cleaning robots currently located in Greenfield.\n" +
                "2.Show the average of the last N air pollution levels sent to the server by a given robots.\n" +
                "3.Show the average of the air pollution levels sent by all the robots to the server and occurred from timestamp t1 and t2.\n" +
                "4.Help\n"+
                "5.Quit\n");
    }

    public static void bye(){
        System.out.println("  ____               ____             _ \n" +
                " |  _ \\             |  _ \\           | |\n" +
                " | |_) |_   _  ___  | |_) |_   _  ___| |\n" +
                " |  _ <| | | |/ _ \\ |  _ <| | | |/ _ \\ |\n" +
                " | |_) | |_| |  __/ | |_) | |_| |  __/_|\n" +
                " |____/ \\__, |\\___| |____/ \\__, |\\___(_)\n" +
                "         __/ |              __/ |       \n" +
                "        |___/              |___/        "
        );
    }
}
