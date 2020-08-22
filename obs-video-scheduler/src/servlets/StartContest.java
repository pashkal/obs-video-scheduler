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
@WebServlet("/StartContest")
public class StartContest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     * 
     * @throws IOException
     * @throws FileNotFoundException
     */
    public StartContest() throws FileNotFoundException, IOException {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println("Rescheduling");
        if (request.getParameter("time") == null)
            DataProvider.startContest();
        else {
            String newTime = request.getParameter("time");
            int h = Integer.parseInt(newTime.substring(0, 2));
            int m = Integer.parseInt(newTime.substring(3, 5));
            Date newStartDate = new Date();
            newStartDate.setHours(h);
            newStartDate.setMinutes(m);
            newStartDate.setSeconds(0);
            DataProvider.startContest(newStartDate.getTime());
        }
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
