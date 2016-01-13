package cs263w16;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
      PrintWriter out = resp.getWriter();
      String key = req.getParameter("keyname");
      String val = req.getParameter("value");

      //if other params are passed in, report an error
      int param_count = 0;
      Enumeration reqenum = req.getParameterNames();
      while (reqenum.hasMoreElements()) {
         String name = (String) reqenum.nextElement();
         param_count++;
      }
      if (param_count > 2) {
          out.println("Error: parameter count > 2");
          out.println("</body></html>");
          return;
      }
      if (param_count == 2) {
          if ((!req.getParameterMap().containsKey("keyname")) ||
             (!req.getParameterMap().containsKey("value"))) {
             out.println("Error: parameters expected to be keyname AND value");
             out.println("</body></html>");
             return;
          }
      }
      if (param_count == 1 && (!req.getParameterMap().containsKey("keyname"))) {
          out.println("Error: parameter expected to be keyname");
          out.println("</body></html>");
          return;
      }
      //param_count == 0 is fine
     

      MemcacheService mcache = MemcacheServiceFactory.getMemcacheService();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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
          mcache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
          Map<String,Object> eles = mcache.getAll(list);
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
            if (val != null) { /*keyname and value have been passed in */
		//store it in the datastore and memcache
                Entity tsk = new Entity("TaskData",key);
                tsk.setProperty("value", val);
		Date date = new Date();
                tsk.setProperty("date", date);
                try {
                    datastore.put(tsk);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                //cache it
                //String,TaskData Object
                mcache.put(key,tsk);
                out.println("<p>Stored key: "+key + " Entity date:  " + date + " value: " + val+ "</p>");
            } else {  /* only keyname has been passed in (read it) */
	        String whereString = "Neither";
	        boolean foundInMcache = false;

	        //check memcache first
	        if (mcache.contains(key)) {
	            foundInMcache = true;
                } 
                Key dsKey = KeyFactory.createKey("TaskData", key);
	        Entity ent = null;
                try {
		     ent = datastore.get(dsKey);
                } catch (Exception e) {}
                if (ent != null) {
                    if (foundInMcache) {
                        whereString = "Both";
	            } else {
                        whereString = "Datastore";
	            } 
                    mcache.put(key,ent);
                    Date date = (Date) ent.getProperty("date");
                    String value = (String) ent.getProperty("value");
                    out.println("<p>key: "+key + " Entity date:  " + date + " value: " + value+ " (" + whereString+")</p>");
                }else {
                    whereString = "Neither";
                    out.println("<p>key: "+key + " not found</p>");
                }
          }
      }
      out.println("</body></html>");
  }     
} 
