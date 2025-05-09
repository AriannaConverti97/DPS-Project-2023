/*package Server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;

public class AdminServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
        server.start();

        System.out.println("Administrator Server running!");
        System.out.println("Administrator Server started on: https://"+HOST+":"+PORT);

    }
}*/
package Server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.*;
import Thread.MqttSubscriberThread;
import java.io.IOException;

public class AdminServer {
    private static final String HOST = "localhost";
    private static final int PORT = 1337;


    public static void main(String[] args) throws IOException, MqttException {
        HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
        server.start();

        System.out.println("Administrator Server running!");
        System.out.println("Administrator Server started on: http://"+HOST+":"+PORT);

        new MqttSubscriberThread().start();
    }


}

