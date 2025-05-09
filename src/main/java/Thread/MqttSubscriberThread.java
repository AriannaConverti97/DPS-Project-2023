package Thread;

import REST.Beans.Data.DatumFromRobot;
import REST.Beans.Robot.RobotForServer;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.eclipse.paho.client.mqttv3.*;

import java.security.Timestamp;
import java.util.Scanner;

public class MqttSubscriberThread extends Thread{
    private static MqttClient client;
    private static final String clientID = MqttClient.generateClientId();
    private static String topics = "greenfield/pollution/#";
    private static final int qos = 2;

    private static final String BROKER = "tcp://localhost:1883";
    private Client clientPost = Client.create();
    private final String serverAddress = "http://localhost:1337";
    private final String postPath = "/data/add";


    @Override
    public void run() {
        try {
            client = new MqttClient(BROKER, clientID);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            System.out.println(clientID + " Connecting Broker " + BROKER);
            client.connect(connOpts);

            // Callback
            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) {
                    // Called when a message arrives from the server that matches any subscription made by the client
                    String receivedMessage = new String(message.getPayload());
                    Gson gson = new Gson();
                    DatumFromRobot data = gson.fromJson(receivedMessage, DatumFromRobot.class);
                    postRequest(clientPost,serverAddress+postPath,data);
                }

                public void connectionLost(Throwable cause) {
                    //System.out.println(clientID + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used here
                }

            });
            //System.out.println(clientID + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
            client.subscribe(topics,qos);
            //System.out.println(clientID + " Subscribed to topics : " + topics);

        } catch (MqttException me ) {
            //me.printStackTrace();
        }

    }

    private static ClientResponse postRequest(Client client, String url, DatumFromRobot d){
        WebResource webResource = client.resource(url);
        String input = new Gson().toJson(d);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Unavailable server!");
            return null;
        }
    }
}
