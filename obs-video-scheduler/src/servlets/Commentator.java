package servlets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DataProvider;
import util.Disclaimer;
import util.Item;
import util.ScheduleEntry;

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/Commentator")
public class Commentator extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Commentator() throws FileNotFoundException, IOException {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Writer w = response.getWriter();
        w.append("<html>\n");
        Map<String, Item> list = DataProvider.getAllItemsByName();
        List<ScheduleEntry> schedule = DataProvider.getSchedule();
        long cTime = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;

        for (ScheduleEntry e : schedule) {
            long start = e.start;
            long stop = e.start + list.get(e.itemName).duration + Disclaimer.getDuration() * 2;
            if (cTime > start && cTime < stop) {
                w.append("<h1 class=\"green-text\">Now Playing<br><br></h1>");
                w.append("<h1 class=\"green-text\">" + e.itemName + "<br><br></h1>");
                w.append("<h1 class=\"green-text\">");
                long rem = (stop - cTime) / 1000;
                if (rem > 59) {
                    w.append(rem / 60 + " minutes ");
                }
                w.append(rem % 60 + " seconds left</h1>");
                w.append("</html>");
                return;
            }
            if (cTime < start && start - cTime < 60000) {
                w.append("<h1 class=\"red-text\">Playing Soon<br><br></h1>");
                w.append("<h1 class=\"red-text\">" + e.itemName + "<br><br></h1>");
                long rem = (start - cTime) / 1000;
                w.append("<h1 class=\"red-text\">in " + rem % 60 + " seconds</h1>");
                w.append("</html>");
                return;
            }
        }
        w.append("<h1 class=\"yellow-text\">Live Stream</h1>\n");
        w.append("</html>");
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
