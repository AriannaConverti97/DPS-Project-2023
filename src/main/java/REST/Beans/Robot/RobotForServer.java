package REST.Beans.Robot;

import REST.Beans.Coordinate;

import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;

@XmlRootElement
public class RobotForServer {
    private int id;
    private String address="localhost";
    private int port;
    private int district;
    private Coordinate position;

    public RobotForServer(){}

    public RobotForServer(int ID){
        this.id =ID;
        this.port =  49152 + ID;
    }

    public RobotForServer(RobotForServer r){
        this.id=r.getId();
        this.port=r.getPort();
        this.district=r.getDistrict();
        this.position=r.getPosition();
        this.address = r.getAddress();
    }

    public RobotForServer(int id, String localhost, int port, Coordinate coordinate, int district) {
        this.id=id;
        this.address=localhost;
        this.port=port;
        this.position=coordinate;
        this.district = district;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public Coordinate getPosition() {
        return position;
    }

    public synchronized void setPosition(Coordinate position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "\n\tid=" + id +
                "\n\taddress='" + address + '\'' +
                "\n\tport=" + port +
                "\n\tdistrict=" + district +
                "\n\tposition=" + position +
                "\n}";
    }
}
