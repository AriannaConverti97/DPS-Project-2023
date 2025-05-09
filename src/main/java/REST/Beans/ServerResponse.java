package REST.Beans;

import REST.Beans.Robot.RobotForServer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerResponse {
    @XmlElement(name = "my_city")
    private ArrayList<RobotForServer> myCity;
    @XmlElement(name="initial_position")
    private Coordinate initialPosition;

    public ServerResponse(){}

    public ServerResponse(ArrayList<RobotForServer> myCity, Coordinate position){
        this.myCity=myCity;
        this.initialPosition=position;
    }

    public ArrayList<RobotForServer> getMyCity() {
        return myCity;
    }

    public void setMyCity(ArrayList<RobotForServer> myCity) {
        this.myCity = myCity;
    }

    public Coordinate getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Coordinate initialPosition) {
        this.initialPosition = initialPosition;
    }
}
