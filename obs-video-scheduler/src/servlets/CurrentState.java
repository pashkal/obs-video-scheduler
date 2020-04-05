package servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
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
import util.ScheduleEntry;
import util.Item;

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/CurrentState")
public class CurrentState extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public CurrentState() throws FileNotFoundException, IOException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Writer w = response.getWriter();
		w.append("<html>\n");
		HashMap<String, Item> list = (HashMap<String, Item>) DataProvider.getMapByName();
		List<ScheduleEntry> schedule = DataProvider.getSchedule();
		Date time = new Date();
		w.append(time.toString() + "<br/>");
		long cTime = new Date().getTime() - new Date().getTimezoneOffset() * 60 * 1000;

		for (ScheduleEntry e : schedule) {
			long start = e.start;
			long stop = e.start + list.get(e.itemName).duration;
			if (cTime > start && cTime < stop) {
				w.append("Currently playing: " + e.itemName + ", " + ((stop - cTime) / 1000) + " seconds left");
				w.append("</html>");
				return;
			}
			if (cTime < start && start - cTime < 30000) {
				long cntdwn = (long) ((start - cTime) / 1000);
				w.append("Playing soon: " + e.itemName + ", in " + cntdwn + " seconds");
				w.append("</html>");

				return;
			}
		}
		w.append("Currently happens nothing.\n");
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
