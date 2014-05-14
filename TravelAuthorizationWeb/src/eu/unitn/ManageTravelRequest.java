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
 * Servlet implementation class ManageTravelRequest
 */
@WebServlet("/ManageTravelRequest")
public class ManageTravelRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManageTravelRequest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String pid = request.getParameter("pid");
		ActivitiProcessInstance instance = ActivitiDeployment.getProcessInstance(pid);
		
		if (instance != null) {
			// Retrieves tasks for the management group.
			Task authorizationApproval = instance.createTaskQuery().taskCandidateGroup("management").singleResult();
		
			request.setAttribute("description", authorizationApproval != null ? authorizationApproval.getDescription() : "" );
			request.setAttribute("pid", pid);
		}
		
		RequestDispatcher reqDispatcher = getServletConfig().getServletContext().getRequestDispatcher("/manageRequest.jsp");
	    reqDispatcher.forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		ActivitiProcessInstance instance = ActivitiDeployment.getProcessInstance(pid);

		if (instance == null)
			return;
		
		Boolean approved = Boolean.parseBoolean(request.getParameter("acceptRequest"));
		String rejectMotivation = request.getParameter("rejectMotivation");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("travelApproved", approved);
		params.put("rejectionMotivation", rejectMotivation);
		
		instance.completeTask("authorizationApproval", params);
		
		PrintWriter out = response.getWriter();
		
		if (instance.isProcessFinished())
			out.println("Travel authorization process completed");
		else
			out.println("Travel authorization process not completed yet");
	}

}
