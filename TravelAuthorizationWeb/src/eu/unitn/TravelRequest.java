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

import me.ettoredelnegro.ActivitiDeployment;
import me.ettoredelnegro.ActivitiProcessInstance;

import org.activiti.engine.task.Task;

/**
 * Servlet implementation class ActivitiTravelRequestForm
 */
@WebServlet("/TravelRequest")
public class TravelRequest extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TravelRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = (String) request.getSession().getAttribute("pid");
		
		if (pid != null) {
			ActivitiProcessInstance process = ActivitiDeployment.getProcessInstance(pid);
			Boolean authenticated = (Boolean) process.getVariable("authenticated");
			
			if (authenticated) {
				RequestDispatcher rd = request.getRequestDispatcher("/createRequest.jsp");
				rd.include(request, response);
				return;
			}
		}
		
		response.setContentType("text/html");
		PrintWriter r = response.getWriter();
		
		r.println("<h1>You must <a href='Authenticate'>login</a> first!");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ActivitiProcessInstance process = ActivitiDeployment.createProcessInstance("TravelAuthorization");
		
		String name = request.getParameter("name");
		String motivation = request.getParameter("motivation");
		Long amount = Long.parseLong(request.getParameter("amount"));
		String email = request.getParameter("email");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("studentName", name);
		params.put("studentEmail", email);
		params.put("amount", amount);
		params.put("travelMotivation", motivation);
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		Task travelRequestTask = process.createTaskQuery()
				.taskCandidateGroup("students")
				.taskDefinitionKey("request")
				.singleResult();
		
		if (travelRequestTask == null)
			throw new RuntimeException("travelRequestTask == null");
		
		ActivitiDeployment.completeTask(travelRequestTask.getId(), params);
			
		out.println("<html><head><title>Travel request form</title></head><body>");
		
		if (process.isProcessFinished()) {
			out.println("<h1>Form completed successfully!</h1>");
		}
		else {
			out.println("<h2><a href='ManageTravelRequest'>Manage travel request</a></h2>");
		}
		out.println("</body></html>");
		out.close();
	}
}
