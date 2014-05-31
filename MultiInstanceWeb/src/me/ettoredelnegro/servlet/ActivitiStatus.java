package me.ettoredelnegro.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.ettoredelnegro.ActivitiDeployment;
import me.ettoredelnegro.ActivitiProcessInstance;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

/**
 * Servlet implementation class ActivitiStatus
 */
@WebServlet("/ActivitiStatus")
public class ActivitiStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActivitiStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProcessEngine engine = ActivitiDeployment.getEngine();
				
		List<ProcessDefinition> ps = engine.getRepositoryService().createProcessDefinitionQuery().list();
		Collection<String> processNames = new ArrayList<String>();
		for (ProcessDefinition p : ps) {
			processNames.add(p.getKey());
		}
		request.setAttribute("processNames", processNames);
				
		RequestDispatcher d = request.getRequestDispatcher("/activitiStatus.jsp");
		d.include(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Retrieves engine and command to execute
		ProcessEngine engine = ActivitiDeployment.getEngine();
		String cmd = request.getParameter("cmd");
		
		if (cmd.equalsIgnoreCase("redeploy")) {
			Deployment d = engine.getRepositoryService().createDeploymentQuery()
					.deploymentName(ActivitiDeployment.deploymentName)
					.singleResult();
			
			// Deletes and reloads it
			engine.getRepositoryService().deleteDeployment(d.getId(), true);
			ActivitiDeployment.getEngine();
			
			request.setAttribute("result", "Successfully redeployed "+ ActivitiDeployment.deploymentName);
		}
		else if (cmd.equalsIgnoreCase("start")) {
			String processName = request.getParameter("name");
			ActivitiProcessInstance process = ActivitiDeployment.createProcessInstance(processName);
			
			request.setAttribute("result", "Successfully started process "+ process.getId());
		}
		
		// Forward to GET /activitiStatus
		doGet(request, response);
	}
}
