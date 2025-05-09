package REST.Client;

import REST.Beans.Coordinate;
import REST.Beans.Robot.RobotForServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RobotsREST {
    private static ClientResponse postRequest(Client client, String url, RobotForServer r){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(r);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server!");
            return null;
        }
    }

    private static ClientResponse deleteRequest(Client client, String url, int id){
        WebResource webResource = client.resource(url+"/" + id);
        System.out.println(webResource);
        try {
            return webResource.type("application/json").delete(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server!");
            return null;
        }
    }

    private static ClientResponse putRequest(Client client, String url, int id, Coordinate coordinate){
        WebResource webResource = client.resource(url+ "/" + id + "/" + coordinate.getX() + "/" + coordinate.getY());
        try {
            return webResource.type("application/json").put(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server!");
            return null;
        }
    }

    public static ClientResponse updateRobotCoordinate(int id, int x, int y) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337/";
        String updatePath = "robots/updateCoordinate";
        return putRequest(client, serverAddress + updatePath, id, new Coordinate(x,y)) ;
    }

    public static ClientResponse removeRobot(int id) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337/";
        String removePath = "robots/remove";
        ClientResponse clientResponse = deleteRequest(client, serverAddress + removePath, id);
        return clientResponse;
    }

    public static ClientResponse addRobotInTheServer(RobotForServer r) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";

        String postPath = "/robots/add";
        ClientResponse clientResponse = postRequest(client, serverAddress + postPath, r);

       return clientResponse;
    }
}
