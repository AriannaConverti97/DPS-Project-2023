package REST.Beans.Data;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataFromRobot {
    private static DataFromRobot instance;

    @XmlElement(name="my_data")
    private ArrayList<DatumFromRobot> dataList;

    public DataFromRobot(){
        dataList = new ArrayList<>();
    }

    //singleton
    public synchronized static DataFromRobot getInstance(){
        if(instance==null)
            instance = new DataFromRobot();
        return instance;
    }

    public synchronized void add(DatumFromRobot data){
        dataList.add(data);
    }

    public synchronized void removeById(int id){
        List<DatumFromRobot> copy = getDataList();
        for(DatumFromRobot d : copy){
            if(d.getIdRobot()==id)
                dataList.remove(d);
        }
    }

    public ArrayList<DatumFromRobot> getDataList(){
        return new ArrayList<>(dataList);
    }

    public void setDataList(ArrayList<DatumFromRobot> dataList) {
        this.dataList = dataList;
    }

    public double getById(int id , int size){
        ArrayList<DatumFromRobot> dataById = new ArrayList<>();
        ArrayList<DatumFromRobot> dataCopy = getDataList();

        for(DatumFromRobot d: dataCopy)
            if(d.getIdRobot()== id)
                dataById.add(d);

        if (dataById.size()==0)
            return -1;

        if(size> dataById.size())
            size = dataById.size();

        Collections.sort(dataById, (dx, sx) -> {
            // 1 - less than, -1 - greater than, 0 - equal,
            return dx.getTimestamp() > sx.getTimestamp() ? 1 : (dx.getTimestamp() < sx.getTimestamp()) ? -1 : 0;
        });
        List<DatumFromRobot> temp = dataById.subList(dataById.size()-size, dataById.size());
        return makeAvg(temp);
    }

    public double getBetween(long t1, long t2){
        ArrayList<DatumFromRobot> dataCopy = getDataList();
        ArrayList<DatumFromRobot> temp = new ArrayList<>();

        for(DatumFromRobot d: dataCopy){
            if(d.getTimestamp()>=t1 && d.getTimestamp()<=t2){
                temp.add(d);
            }
        }
        return makeAvg(temp);

    }

    private double makeAvg(List<DatumFromRobot> temp){
        double avg=0.0;
        int count =0;
        if(temp.size()==0)
            return -1;

       for(DatumFromRobot d:temp){
            avg = d.getAvg().stream().mapToDouble(Double::intValue).sum();
            count += d.getAvg().size();
        }
        if(count ==0)
            return 0.0;
        return avg/count;
    }

}
