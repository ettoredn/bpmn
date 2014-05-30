package me.ettoredelnegro.servlet;

import java.io.IOException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.ProcessDefinition;

import me.ettoredelnegro.ActivitiDeployment;

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
		ProcessEngineConfiguration configuration = ActivitiDeployment.getConfiguration();
		ProcessEngine engine = ActivitiDeployment.getEngine();
		
		request.setAttribute("jdbcUrl", configuration.getJdbcUrl());
		
		List<ProcessDefinition> ps = engine.getRepositoryService().createProcessDefinitionQuery().list();
		Collection<String> processNames = new ArrayList<String>();
		for (ProcessDefinition p : ps) {
			processNames.add(p.getName());
		}
		request.setAttribute("processNames", processNames);
		
		RequestDispatcher d = request.getRequestDispatcher("/activitiStatus.jsp");
		d.include(request, response);
	}
}
