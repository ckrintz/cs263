package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import javax.xml.bind.JAXBElement;

public class TaskDataResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String keyname;

  public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.keyname = kname;
  }
  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public TaskData getTaskDataHTML() {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key dsKey = KeyFactory.createKey("TaskData", keyname);
    Entity ent = null;
    try {
        ent = datastore.get(dsKey);
    } catch (Exception e) {
        ent = null;   
    }
    if(ent==null)
      throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    TaskData td = new TaskData(keyname,
        (String) ent.getProperty("value"),
        (Date) ent.getProperty("date")
    );
    return td;
  }
  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public TaskData getTaskData() {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key dsKey = KeyFactory.createKey("TaskData", keyname);
    Entity ent = null;
    try {
        ent = datastore.get(dsKey);
    } catch (Exception e) {
        ent = null;   
    }
    if(ent==null)
      throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    TaskData td = new TaskData(keyname,
        (String) ent.getProperty("value"),
        (Date) ent.getProperty("date")
    );
    return td;
  }
  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTaskData(String val) {
    Response res = null;
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    //first check if the Entity exists in the datastore
    Key dsKey = KeyFactory.createKey("TaskData", this.keyname);
    Entity ent = null;
    try {
        ent = datastore.get(dsKey);
    } catch (Exception e) {
        ent = null;   
    }
    if(ent==null) {
        //key was not in datastore, so create it using this objects keyname
        ent = new Entity("TaskData",this.keyname);
        //signal that we created the entity in the datastore 
        res = Response.created(uriInfo.getAbsolutePath()).build();
    } else {
        //signal that the datastore contained the entity already
        res = Response.noContent().build();
    }
    //update this Entities properties
    ent.setProperty("value",val);
    ent.setProperty("date",new Date());
    //put it in the datastore
    try {
        datastore.put(ent);
    } catch(Exception e) {
        e.printStackTrace();
    }
    //return the right response (noContent for updated, 201 for created)
    return res;
  }

  @DELETE
  public void deleteIt() {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key dsKey = KeyFactory.createKey("TaskData", keyname);
    System.out.println("Attempting to delete keyname: "+keyname);
    try {
        datastore.delete(dsKey);
    } catch (Exception e) {
        //throw new RuntimeException("Delete: TaskData with " + keyname +  " not found");
        System.out.println("Delete: TaskData with " + keyname +  " not found");
    }
  }

} 
