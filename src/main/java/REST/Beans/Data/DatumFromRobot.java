package REST.Beans.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;

@XmlRootElement
public class DatumFromRobot{
    private int idRobot;
    private ArrayList<Double> avg;
    private int timestamp;

    public DatumFromRobot(){}

    public DatumFromRobot(int IDRobot, ArrayList<Double> avg, int timestamp) {
        this.idRobot = IDRobot;
        this.avg = avg;
        this.timestamp = timestamp;
    }

    public int getIdRobot() {
        return idRobot;
    }

    public void setIdRobot(int idRobot) {
        this.idRobot = idRobot;
    }

    public ArrayList<Double> getAvg() {
        return avg;
    }

    public void setAvg(ArrayList<Double> avg) {
        this.avg = avg;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DatumFromRobot{" +
                "IDRobot=" + idRobot +
                ", avg=" + avg +
                ", timestamp=" + timestamp +
                '}';
    }
}
