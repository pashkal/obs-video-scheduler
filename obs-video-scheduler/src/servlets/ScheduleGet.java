package servlets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DataProvider;
import util.Disclaimer;
import util.ScheduleEntry;
import util.Item;

/**
 * Servlet implementation class ScheduleGet
 */
@WebServlet("/ScheduleGet")
public class ScheduleGet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ScheduleGet() throws FileNotFoundException, IOException {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Map<String, Item> videoMap = DataProvider.getAllItems();
        List<ScheduleEntry> schedule = DataProvider.getSchedule();
        
        JsonObjectBuilder result = Json.createObjectBuilder().add("contest_timestamp", DataProvider.getContestStart());
        
        JsonArrayBuilder scheduleBuilder = Json.createArrayBuilder();
        
        for (ScheduleEntry e : schedule) {
            long stop = (e.start + videoMap.get(e.itemName).duration + Disclaimer.getDuration() * 2);
            scheduleBuilder.add(Json.createObjectBuilder().add("_id", e.id).add("start", e.start).add("stop", stop).add("name", e.itemName).build());
        }
        
        result.add("schedule", scheduleBuilder.build());
        response.getWriter().print(result.build().toString());
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
