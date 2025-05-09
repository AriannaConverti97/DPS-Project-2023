package Thread;

import GRPC.CleaningRobot;

import REST.Beans.Data.DatumFromRobot;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class MqttPublisherThread extends Thread{
    private MqttClient client;
    private final String broker = "tcp://localhost:1883";
    private final String clientId;
    private String topic = "greenfield/pollution/district";
    private final int qos = 2;
    private CleaningRobot robot;
    private List<Measurement> buffer;

    public MqttPublisherThread(CleaningRobot robot, List<Measurement> buffer) {
        this.robot = robot;
        this.buffer = buffer;
        this.clientId = robot.getMqttId();
    }

    @Override
    public void run() {
        try {
            robot.increaseClock();
            topic += robot.getDistrict();
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Connect the client
            //System.out.println(clientId + " Connecting Broker " + broker);
            client.connect(connOpts);
            //System.out.println(clientId + " Connected");


            DatumFromRobot data = new DatumFromRobot(robot.getId(), getValues(), robot.getClock() );
            String payload = new Gson().toJson(data);
            MqttMessage message = new MqttMessage(payload.getBytes());

            // Set the QoS on the Message
            message.setQos(qos);
            //System.out.println(clientId + " Publishing message: " + payload + " ...");
            client.publish(topic, message);
            //System.out.println(clientId + " Message published");

        } catch (MqttException  me ) {
            me.printStackTrace();
        }

    }

    public ArrayList<Double> getValues() {
        ArrayList<Double> values = new ArrayList<>();
        for (Measurement m : buffer) {
            values.add(m.getValue());
        }
        return values;
    }
}
