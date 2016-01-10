package cs263w16;
import java.io.IOException;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

//The Worker servlet is mapped to the "/worker" route.
public class Worker extends HttpServlet {
 protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
         
     DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
     String value = request.getParameter("value");
     String name = request.getParameter("keyname");
     Entity tsk = new Entity("TaskData",name);
     tsk.setProperty("value", value);
     tsk.setProperty("date", new Date());
     datastore.put(tsk);
     //and cache it
     MemcacheService mcache = MemcacheServiceFactory.getMemcacheService();
     mcache.put(name,tsk);
     System.err.println("****************Wrote key/value to datastore: "+ name + " " +value);

 }
}

