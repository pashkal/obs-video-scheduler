package servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DataProvider;
import util.ScheduleEntry;
import util.Item;

/**
 * Servlet implementation class ScheduleGet
 */
@WebServlet("/ScheduleGet")
public class ScheduleGet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String scheduleFile;

	private String fileList;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScheduleGet() throws FileNotFoundException, IOException {
		super();
		Properties pr = new Properties();
		pr.load(new FileInputStream("../../sched.properties"));
		scheduleFile = pr.getProperty("schedule-file");
		fileList = pr.getProperty("video-list-file");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		Map<String, Item> videoMap = DataProvider.getMapByName();
		List<ScheduleEntry> schedule = DataProvider.getSchedule();
		PrintWriter pw = new PrintWriter(response.getWriter());
		pw.print("[");
		for (int i = 0; i < schedule.size(); i++) {
			ScheduleEntry e = schedule.get(i);
			if (i > 0) {
				pw.print(",");
			}
			pw.print("{\"_id\": \"" + e.id + "\",");
			pw.print("\"start\": " + e.start + ",");
			pw.print("\"stop\": " + (e.start + videoMap.get(e.itemName).duration) + ",");
			pw.print("\"name\": \"" + e.itemName + "\"}");
		}
		pw.print("]");

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
