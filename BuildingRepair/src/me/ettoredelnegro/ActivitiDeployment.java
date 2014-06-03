package me.ettoredelnegro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class ActivitiDeployment
{
	public static final String[] processNames = {"Exam"};
	public static final String deploymentName = "BPMN";
	public static String smtpUsername;
	public static String smtpPassword;
	public static String smtpHost;
	public static Integer smtpPort;
	public static String defaultFrom;
	public static boolean useTLS;
	
	protected static ProcessEngine engine;
	
	/**
	 * Returns the configuration of the current engine.
	 * @return
	 */
	public static ProcessEngineConfiguration getConfiguration()
	{
		ProcessEngine engine = getEngine();
		
		return ((ProcessEngineImpl) engine).getProcessEngineConfiguration();
	}
	
	/**
	 * Returns a connection to the data source.
	 * @return
	 */
	public static Connection getConnection()
	{
		try {
			DataSource dataSource = getConfiguration().getDataSource();
			Connection conneection = dataSource.getConnection();
			
			if (conneection.isClosed())
				throw new RuntimeException("jdbcConnection.isClosed()");
			
			return conneection;
		} catch (SQLException e) {
			throw new RuntimeException("Unable to establish database connection: "+ e.getMessage());
		}
	}
	
	/**
	 * Returns or instantiates an engine.
	 * @return The default engine if available, otherwise an engine instantiated from activiti.cfg.xml
	 */
	public static ProcessEngine getEngine()
	{
		if (engine == null) {
//			Map<String, ProcessEngine> engines = ProcessEngines.getProcessEngines();
//			for (String name : engines.keySet()) {
//				System.out.println("[ActivitiDeployment] Available engine: "+ name);
//			}
			
			engine = ProcessEngines.getDefaultProcessEngine();
			if (engine == null) {
				System.out.println("[ActivitiDeployment] No default process engine found.");
				
				ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
				engine = ProcessEngines.getProcessEngine(configuration.getProcessEngineName());
				
				if (engine != null)
					System.out.println("[ActivitiDeployment] Using "+ engine.getName() +" engine.");
				else {
					engine = configuration.buildProcessEngine();
					System.out.println("[ActivitiDeployment] Creating engine from activiti.cfg.xml");
				}
			}
			
			if (engine == null)
				throw new RuntimeException("[ActivitiDeployment] Unable to create an engine.");
			
			ProcessEngineConfiguration configuration = ((ProcessEngineImpl) engine).getProcessEngineConfiguration();
			//configuration.getJobExecutor().start();
			
			smtpUsername = configuration.getMailServerUsername();
			smtpPassword = configuration.getMailServerPassword();
			smtpHost = configuration.getMailServerHost();
			smtpPort = configuration.getMailServerPort();
			defaultFrom = configuration.getMailServerDefaultFrom();
			useTLS = configuration.getMailServerUseTLS();
		}
		
		Boolean alreadyDeployed = engine.getRepositoryService().createDeploymentQuery()
				.deploymentName(deploymentName)
				.count() > 0 ? true : false;
				
		if (!alreadyDeployed) {
			DeploymentBuilder builder = engine.getRepositoryService().createDeployment().name(deploymentName);
			
			for (String processName : processNames) {
				builder.addClasspathResource(processName + ".bpmn");
				System.out.println("[ActivitiDeployment] Added process "+ processName);
			}
			
			builder.deploy();
		}
		
		return engine;
	}
	
	/**
	 * @param id Process instance id
	 * @return
	 */
	public static ActivitiProcessInstance getProcessInstance(String id)
	{		
		if (id == null)
			throw new RuntimeException("Process instance "+ id +" does not exist.");
		
		// There has to be only one active process instance
		ProcessInstance instance = getEngine().getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(id)
				.singleResult();
				
		return instance != null ? new ActivitiProcessInstance(instance, getEngine()) : null;
	}
	
	public static ActivitiProcessInstance createProcessInstance(String name)
	{
		List<String> names = Arrays.asList(processNames);
		
		if (!names.contains(name))
			throw new RuntimeException("!processNames.contains(name)");
		
		RuntimeService runtime = getEngine().getRuntimeService();
		ProcessInstance i = runtime.startProcessInstanceByKey(name, new HashMap<String, Object>());
		//runtime.addUserIdentityLink(i.getProcessInstanceId(), "ettore", IdentityLinkType.STARTER);
		
		if (i.getProcessInstanceId() == null)
			throw new RuntimeException("i.getProcessInstanceId() == null");
		
		return new ActivitiProcessInstance(i, getEngine());
	}
	
	/**
	 * Retrieves a task by its id.
	 * @param taskId
	 * @return
	 */
	public static Task getTask(String taskId) {
		if (taskId == null)
			return null;
		
		Task task = getEngine().getTaskService().createTaskQuery()
				.taskId(taskId)
				.singleResult();
		
		if (task == null)
			throw new RuntimeException("Task "+ taskId +" does not exist.");
		
		return task;
	}
	
	/**
	 * Completes the given user task.
	 * @param taskId The id of the task.
	 * @param parameters
	 */
	public static void completeTask(String taskId, Map<String, Object> parameters)
	{
		Task t = getEngine().getTaskService().createTaskQuery()
				.taskId(taskId)
				.singleResult();
		try {
			String id = t.getId();
			getEngine().getTaskService().complete(t.getId(), parameters);
			System.out.println("[Deployment] User task "+ id +" completed");
		} catch (ActivitiObjectNotFoundException e) {}
	}
	
	/**
	 * Checks whether a task as already been executed
	 * or it is not scheduled for execution.
	 * 
	 * @param taskName
	 * @return
	 */
	public static boolean isTaskFinished(String taskId) {
		Task task = getEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
		
		if (task == null)
			return true;
		
		HistoricTaskInstance historicTask = getEngine().getHistoryService().createHistoricTaskInstanceQuery()
				.taskId(taskId)
				.finished()
				.singleResult();
		
		return historicTask != null;
	}
}
