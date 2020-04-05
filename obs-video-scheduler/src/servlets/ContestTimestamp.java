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

/**
 * Servlet implementation class VideoList
 */
@WebServlet("/ContestTimestamp")
public class ContestTimestamp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ContestTimestamp() throws FileNotFoundException, IOException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		long time = DataProvider.getContestStart();
		Writer w = response.getWriter();
		w.append("{\"contest_timestamp\": " + String.valueOf(time) + "}");

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
