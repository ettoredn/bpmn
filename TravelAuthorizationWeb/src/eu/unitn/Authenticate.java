package eu.unitn;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.task.Task;

import me.ettoredelnegro.ActivitiDeployment;
import me.ettoredelnegro.ActivitiProcessInstance;

/**
 * Servlet implementation class Authenticate
 */
@WebServlet("/Authenticate")
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String pid = (String) session.getAttribute("pid");
		
		if (pid == null) {
			ActivitiProcessInstance process = ActivitiDeployment.createProcessInstance("TravelAuthorization");
			pid = process.getProcessInstanceId();
			session.setAttribute("pid", pid);
		}
		
		ActivitiProcessInstance process = ActivitiDeployment.getProcessInstance(pid);
		
		if ((Boolean) process.getVariable("authenticated")) {
			PrintWriter out = response.getWriter();
			out.println("<h1>Already authenticated</h1>");
		}
		
		request.setAttribute("failure", false);
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp");
		rd.include(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = (String) request.getSession().getAttribute("pid");
		
		if (pid == null)
			return;
		
		ActivitiProcessInstance process = ActivitiDeployment.getProcessInstance(pid);
		if (process == null) {
			request.getSession().invalidate();
			
			PrintWriter out = response.getWriter();
			out.println("<h1>Session invalidated</h1>");
			return;
		}
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("username", request.getParameter("username"));
		values.put("password", request.getParameter("password"));
		values.put("role", request.getParameter("role"));
		
		Task loginTask = process.createTaskQuery().taskDefinitionKey("loginTask").singleResult();
		ActivitiDeployment.completeTask(loginTask.getId(), values);
	}
}
