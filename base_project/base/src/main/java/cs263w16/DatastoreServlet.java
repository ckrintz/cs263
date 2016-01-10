package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
      out = resp.getWriter();
      String key = req.getParameter("keyname");

      /* Step 3 in Assignment3.html */
      if (key == null) {/*no keyname passed in */
          //datastore dump
          Query q = new Query("TaskData");
          PreparedQuery pq = datastore.prepare(q);
          List<String> list = new ArrayList<>();
          System.out.println("Printing datastore entities");
          out.println("<p>Datastore entries</p>");
          for (Entity result : pq.asIterable()) {
                Key eKey = result.getKey();
                String mykey = eKey.getName();
                Date date = (Date) result.getProperty("date");
                String value = (String) result.getProperty("value");
                out.println("<p>"+eKey + " "  +mykey+" "+ date + " " + value+"</p>");
                //Store the key for use with memcache 
                list.add(mykey);
          }
	  /* Step 4 in Assignment3.html */
          System.out.println("Printing memcache entities");
          out.println("<p>Memcache entities</p>");
            MemcacheService mcache = MemcacheServiceFactory.getMemcacheService();
            mcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
            //elements: Key,Entity (TaskData)
            Map<Key,Object> eles = mcache.getAll(list);
            Iterator<Entry<String, Object>> iter = eles.entrySet().iterator();
            while (iter.hasNext()){
                Entry<String,Object> ent = iter.next();
                String mykey = (String) ent.getKey();
                Entity ele = (Entity) ent.getValue();
                Date date = (Date) ele.getProperty("date");
                String value = (String) ele.getProperty("value");
                System.out.println("key: "+mykey+ " Entity(date,val): " + date + " " + value);
                out.println("<p>key: "+mykey + " Entity(date,val):  " + date + " " + value+"</p>");
            }

      } else { /* keyname has been passed in */
	    whereString = "Neither"

	    //check memcache first
            MemcacheService mcache = MemcacheServiceFactory.getMemcacheService();
	    if (mcache.contains(key)) {
	        foundInMcache = true;
            } 
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Key dsKey = KeyFactory.createKey("TaskData", key);
	    Entity ent = datastore.get(dsKey);
            if (ent != null) {
                if (foundInMcache) {
                    whereString = "Both"
	        } else {
                    whereString = "Datastore"
	        } 
                mcache.put(key,ent);
            }else {
                whereString = "Neither"
            }
      }

      out.println("</body></html>");

        
  }     
} 
