package cs263w16;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

//Map this class to /ds route
@Path("/ds")
public class DatastoreResource {
  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  // Return the list of todos to the user in the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public List<TaskData> getEntitiesBrowser() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //datastore dump -- only do this if there are a small # of entities
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    List<TaskData> list = new ArrayList<TaskData>();
    System.out.println("Processing datastore entities");
    for (Entity result : pq.asIterable()) {
        Key eKey = result.getKey();
        String mykey = eKey.getName();
        Date date = (Date) result.getProperty("date");
        String value = (String) result.getProperty("value");
        TaskData td = new TaskData(mykey,value,date);
        System.out.println("Found: "+mykey+" val: "+value+" ts: "+date);
        list.add(td);
    }
    return list;
  }

  // Return the list of todos to applications
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public List<TaskData> getEntities() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //datastore dump -- only do this if there are a small # of entities
    Query q = new Query("TaskData");
    PreparedQuery pq = datastore.prepare(q);
    List<TaskData> list = new ArrayList<TaskData>();
    System.out.println("Processing datastore entities");
    for (Entity result : pq.asIterable()) {
        Key eKey = result.getKey();
        String mykey = eKey.getName();
        Date date = (Date) result.getProperty("date");
        String value = (String) result.getProperty("value");
        TaskData td = new TaskData(mykey,value,date);
        System.out.println("Found: "+mykey+" val: "+value+" ts: "+date);
        list.add(td);
    }
    return list;
  }

  @POST
  @Produces(MediaType.TEXT_HTML)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public void newTaskData(@FormParam("keyname") String keyname,
      @FormParam("value") String value,
      @Context HttpServletResponse servletResponse) throws IOException {
    //TaskData td = new TaskData(keyname, value, Date());
    //TodoDao.instance.getModel().put(id, todo);
    Date date = new Date();
    System.out.println("Posting new TaskData: " +keyname+" val: "+value+" ts: "+date);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity tsk = new Entity("TaskData",keyname);
    tsk.setProperty("value", value);
    tsk.setProperty("date", date);
    try {
        datastore.put(tsk);
    } catch(Exception e) {
        e.printStackTrace();
    }

    servletResponse.sendRedirect("../done.html");
  }

  //The @PathParam annotation says that keyname can be inserted as parameter after this class's route /ds
  @Path("{keyname}")
  public TaskDataResource getEntity(@PathParam("keyname") String keyname) {
    System.out.println("GETting TaskData for " +keyname);
    return new TaskDataResource(uriInfo, request, keyname);
  }
}
