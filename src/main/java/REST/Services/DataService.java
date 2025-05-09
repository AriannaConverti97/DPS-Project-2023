package REST.Services;

import REST.Beans.Data.DataFromRobot;
import REST.Beans.Data.DatumFromRobot;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("data/")
public class DataService {
    @GET
    @Produces({"application/json", "application/cml"})
    public Response get(){
        return Response.ok(DataFromRobot.getInstance().getDataList()).build();
    }
    @Path("averageDataByRobot/{id}/{size}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response averageDataByRobot(@PathParam("id") int id, @PathParam("size") int size){
        double data =  DataFromRobot.getInstance().getById(id,size);
        if(data == -1)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(data).build();
    }

    @Path("averageBetweenTwoTimestamps/{t1}/{t2}")
    @GET
    @Produces({"application/json", "application/xml"})
    public Response averageBetween(@PathParam("t1") long t1, @PathParam("t2") long t2){
        double avg = DataFromRobot.getInstance().getBetween(t1,t2);
        if(avg==-1)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(avg).build();
    }

    @Path("add")
    @POST
    @Consumes({"application/json", "application/xml"})
    public Response add(DatumFromRobot d){
        DataFromRobot.getInstance().add(d);
        return Response.ok().build();
    }
}
