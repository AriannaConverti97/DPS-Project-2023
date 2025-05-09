package simulators;

import java.util.ArrayList;
import java.util.List;

public class BufferData implements Buffer{
    ArrayList<Measurement> data;
    volatile ArrayList<Measurement> averages;
    double overlapping = 0.5;
    int windows = 8 ;

    public BufferData(){
        data = new ArrayList<>();
        averages = new ArrayList<>();
    }

    @Override
    public void addMeasurement(Measurement m) {
        data.add(m);
        if(data.size()==windows){
            mean();
        }
    }

    public void mean(){
        double count = 0;

        for (Measurement m: data){
            count += m.getValue();

        }

        averages.add(new Measurement(
                data.get(data.size()-1).getId(),
                data.get(data.size()-1).getType(),
                count/windows,
                data.get(data.size()-1).getTimestamp()
        ));

        freeOverLappingWindows(data.subList(0, (int)(windows*overlapping)));
    }

    public synchronized void freeOverLappingWindows(List<Measurement> sublist){
        sublist.removeAll(sublist);
    }

    @Override
    public List<Measurement> readAllAndClean() {
        List listCopy = new ArrayList<>(averages);
        averages.clear();
        return listCopy;
    }

    public ArrayList<Measurement> getAverages() {
        return averages;
    }

    public ArrayList<Measurement> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BufferData{" +
                "data=" + averages +
                '}';
    }
}
