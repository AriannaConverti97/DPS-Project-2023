package REST.Services;

import REST.Beans.Coordinate;
import REST.Beans.Robot.RobotForServer;
import REST.Beans.Robot.RobotsForServer;
import REST.Beans.ServerResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("robots/")
public class RobotsService {
    @GET
    @Produces({"application/json", "application/xml"})
    public Response getRobotsList(){
        return Response.ok(RobotsForServer.getInstance()).build();
    }
    @Path("add")
    @POST
    @Produces({"application/json", "application/xml"})
    @Consumes({"application/json", "application/xml"})
    public Response addRobots(RobotForServer r) throws InterruptedException {
        if(RobotsForServer.getInstance().getRobotByID(r.getId())!= null)
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        RobotsForServer.getInstance().add(r);
        ServerResponse resp = new ServerResponse(RobotsForServer.getInstance().getRobotsList(), r.getPosition());
        return Response.ok(resp).build();
    }

    @Path("remove/{id}")
    @DELETE
    @Produces({"application/json", "application/xml"})
    public Response removeRobots(@PathParam("id") int id) throws InterruptedException {
        if(RobotsForServer.getInstance().getRobotByID(id) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        RobotsForServer.getInstance().removeRobot(id);
        return  Response.ok().build();
    }

    @Path("updateCoordinate/{ID}/{x}/{y}")
    @PUT
    @Produces({"application/json", "application/xml"})
    public Response updateCoordinate(@PathParam("ID") int id, @PathParam("x") int x, @PathParam("y") int y) throws InterruptedException {
        if(RobotsForServer.getInstance().getRobotByID(id) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        Coordinate newCoordinate = new Coordinate(x,y);
        RobotsForServer.getInstance().updateCoordinate(id, newCoordinate);
        return Response.ok().build();
    }


}
