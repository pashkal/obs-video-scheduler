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

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/ContestState")
public class ContestState extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ContestState() throws FileNotFoundException, IOException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long time = DataProvider.getContestStart() + new Date().getTimezoneOffset() * 60 * 1000;
		long currentTime = new Date().getTime();
		Writer w = response.getWriter();
		w.append("<html>");
		w.append("Start at: " + new Date(time).toString() + "<br/>\n");
		long d = Math.abs(time - currentTime);
		long h = d / 60 / 1000 / 60;
		long m = d / 60 / 1000 % 60;
		long s = d / 1000 % 60;
		String contestTime = h + ":" + m + ":" + s;
		if (time < currentTime) {
			w.append("Current contest time: " + contestTime);
		} else {
			w.append("Time before start: " + contestTime);
		}
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
