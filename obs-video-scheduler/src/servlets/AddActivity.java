package servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DataProvider;
import util.Item;

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/AddActivity")
public class AddActivity extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public AddActivity() throws FileNotFoundException, IOException {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String durationString = request.getParameter("duration");
        long duration;
        if (!durationString.contains("-"))
            duration = Long.parseLong(durationString) * 1000;
        else
            duration = Long.parseLong(durationString.substring(0, durationString.indexOf("-"))) * 60000
                    + Long.parseLong(durationString.substring(durationString.indexOf("-") + 1)) * 1000;

        List<Item> activities = new ArrayList<>(DataProvider.getActivitiesByName().values());
        activities.add(new Item(name, duration, false));
        DataProvider.writeActivities(activities);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
