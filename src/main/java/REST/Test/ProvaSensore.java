package REST.Test;

import REST.Beans.Data.DataFromRobot;
import REST.Beans.Data.DatumFromRobot;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProvaSensore {
    public static void main(String[] args) {
        Client client = Client.create();
        String serverAddress = "http://localhost:1337";
        ClientResponse clientResponse = null;

        // POST EXAMPLE
        /*String postPath = "/data/add";
        ArrayList<Double> avg = new ArrayList<>();
        avg.add(1.0);
        avg.add(2.0);
        avg.add(3.0);
       for(int i = 0; i<10; i++){
           int finalI = i;
            DatumFromRobot data = new DatumFromRobot(0, (ArrayList<Double>) avg.stream().map(n-> n+ finalI).collect(Collectors.toList()),i);
            System.out.println(data);
            clientResponse = postRequest(client,serverAddress+postPath,data);
            System.out.println(clientResponse.toString());
        }*/


        //GET REQUEST #1
        String getPath = "/data";
        clientResponse = getRequest(client,serverAddress+getPath);
        System.out.println(clientResponse.toString());
        DataFromRobot users = clientResponse.getEntity(DataFromRobot.class);
        System.out.println("Data List");
        for (DatumFromRobot u : users.getDataList()){
            System.out.println("Id Robot: " + u.getIdRobot() + " Avg: " + u.getAvg() + " Timestamp: " + u.getTimestamp());
        }

        //GET REQUEST 2
        String getPath1 = "/data/averageDataByRobot/0/2";
        clientResponse = getRequest(client, serverAddress+getPath1);
        System.out.println(clientResponse.toString());
        System.out.println(clientResponse.getEntity(String.class));

        //GET REQUEST 3

        String getPath2 = "/data/averageBetweenTwoTimestamps/0/1";
        clientResponse = getRequest(client, serverAddress+getPath2);
        System.out.println(clientResponse.toString());
        System.out.println(clientResponse.getEntity(String.class));
    }

    public static ClientResponse postRequest(Client client, String url, DatumFromRobot u){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(u);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server");
            return null;
        }
    }

    public static ClientResponse getRequest(Client client, String url){
        WebResource webResource = client.resource(url);
        try {
            return webResource.type("application/json").get(ClientResponse.class);
        } catch (ClientHandlerException e) {
            System.out.println("Server non disponibile");
            return null;
        }
    }

}
