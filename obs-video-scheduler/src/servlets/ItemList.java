package servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
 * Servlet implementation class VideoList
 */
@WebServlet("/VideoList")
public class ItemList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public ItemList() throws FileNotFoundException, IOException {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Item> list;
        if (request.getParameter("type").equals("video"))
            list = new ArrayList<>(DataProvider.getVideosByName().values());
        else
            list = new ArrayList<>(DataProvider.getActivitiesByName().values());

        Collections.sort(list);

        List<ScheduleEntry> sc = DataProvider.getSchedule();
        long contestStartTime = DataProvider.getContestStart();

        Writer w = response.getWriter();
        w.append("<table>\n");

        w.append("<tr>\n");
        w.append("<td></td>");
        w.append("<td>Title</td>");
        w.append("<td>Duration</td>");
        w.append("<td>Previous plays</td>");
        w.append("<td>Future plays</td>");

        w.append("<td></td>");

        w.append("</tr>");

        for (int i = 0; i < list.size(); i++) {
            long duration = list.get(i).duration;
            String dur = duration / 60000 + ":" + duration / 1000 % 60;

            w.append("<tr" + (i % 2 == 0 ? "" : " bgcolor = \"#CCCCCC\"") + ">\n");
            w.append("<td><input type = \"submit\" value=\"Schedule\" onclick='add_event(\"" + list.get(i).uuid+ "\");'/></td>");

            w.append("<td>" + list.get(i).name + "</td>");
            w.append("<td>" + dur + "</td>");

            long time = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;
            String previousPlays = "";
            String futurePlays = "";
            int p = 0;
            int f = 0;
            for (ScheduleEntry s : sc) {
                // log(s.video + " " + list.get(i).name + " " + s.start + " " +
                // time);
                long d = Math.abs((s.start - contestStartTime) / 60000);
                String contestDiff = d / 60 + ":" + (d % 60 < 10 ? "0" : "") + d % 60;
                if (s.start < contestStartTime) {
                    contestDiff = "-" + contestDiff;
                }
                if (s.itemName.equals(list.get(i).name)) {
                    if (s.start < time) {
                        if (!previousPlays.equals("")) {
                            previousPlays += ", ";
                        }
                        previousPlays += contestDiff;
                        p++;
                    } else {
                        if (!futurePlays.equals("")) {
                            futurePlays += ", ";
                        }
                        futurePlays += contestDiff;
                        f++;
                    }
                }
            }

            w.append("<td>" + previousPlays + " (" + p + ")</td><td>" + futurePlays + " (" + f + ")</td>");

            w.append("</tr>\n");

        }
        if (request.getParameter("type").equals("activity")) {
            w.append("<tr>"
                    + "<td><form><input type = \"submit\" value=\"Create\" onclick='add_activity();'/></form></td>"
                    + "<td><input type = \"text\" id = \"activity-name\"/></td>"
                    + "<td><input type = \"text\" id = \"activity-duration\"/></td>" + "<td></td>" + "<td></td>"
                    + "</tr>");
        }
        w.append("</table>\n");
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
