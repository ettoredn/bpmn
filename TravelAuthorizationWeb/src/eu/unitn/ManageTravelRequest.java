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
		Task authorizationApproval = ActivitiDeployment.getEngine().getTaskService().createTaskQuery()
			.processDefinitionKey("AuthorizationApproval")
			.taskDefinitionKey("authorizationApproval")
			.taskCandidateGroup("management")
			.singleResult();
		
		if (authorizationApproval != null) {
			request.setAttribute("description", authorizationApproval != null ? authorizationApproval.getDescription() : "" );
			request.setAttribute("taskid", authorizationApproval.getId());
		}
				
		RequestDispatcher reqDispatcher = getServletConfig().getServletContext().getRequestDispatcher("/manageRequest.jsp");
	    reqDispatcher.forward(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String taskId = request.getParameter("taskid");
		
		if (taskId == null)
			return;

		Task task = ActivitiDeployment.getTask(taskId);
		ActivitiProcessInstance instance = ActivitiDeployment.getProcessInstance(task.getProcessInstanceId());
		
		Boolean approved = "on".equals(request.getParameter("acceptRequest"));
		String rejectMotivation = request.getParameter("rejectMotivation");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("travelApproved", approved);
		params.put("rejectionMotivation", rejectMotivation);
		
		ActivitiDeployment.completeTask(taskId, params);

		PrintWriter out = response.getWriter();
		
		if (instance.isProcessFinished())
			out.println("Travel authorization process completed");
		else
			out.println("Travel authorization process not completed yet");
	}

}
