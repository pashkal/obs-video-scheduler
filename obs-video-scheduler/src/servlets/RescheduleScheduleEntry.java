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
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DataProvider;
import util.Item;
import util.ScheduleEntry;

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/RescheduleScheduleEntry")
public class RescheduleScheduleEntry extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public RescheduleScheduleEntry() throws FileNotFoundException, IOException {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uuid = request.getParameter("uuid");
        long newStart = Long.parseLong(request.getParameter("start"));
        
        List<ScheduleEntry> schedule = DataProvider.getSchedule();
        
        for (ScheduleEntry e : schedule) {
            if (e.id == Long.parseLong(uuid)) {
                if (e.start == newStart) {
                    response.getWriter().write("no-op");
                    return;
                }
                e.start = newStart;
            }
        }
        
        DataProvider.updateSchedule(schedule);
        
        DataProvider.writeScheduleToClient(response, schedule);
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
