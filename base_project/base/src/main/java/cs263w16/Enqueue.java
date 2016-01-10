package cs263w16;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.taskqueue.*;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

//The Enqueue servlet is mapped to the "/enqueue" route.
public class Enqueue extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("keyname");
        String value = request.getParameter("value");

        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/worker").param("value", value).param("keyname",name));

        response.sendRedirect("/done.html");
        //response.sendRedirect("/tqueue.jsp?keyname=" + name);
        //response.sendRedirect("/test");
    }
}
