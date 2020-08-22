package servlets;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Config;

/**
 * Servlet implementation class UpdateConfig
 */
@WebServlet("/UpdateConfig")
public class UpdateConfig extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateConfig() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        Map<String, String> configData = new HashMap<>();
        for (String key : params.keySet()) {
            String[] value = params.get(key);
            if (value.length != 1) {
                throw new InvalidParameterException("Request parameter with invalid number of items: " + value.length);
            }
            configData.put(key, value[0]);
        }
        Config.writeData(configData);
        response.sendRedirect("/settings.jsp");
    }

}
