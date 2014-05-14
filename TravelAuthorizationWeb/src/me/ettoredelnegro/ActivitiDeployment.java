package me.ettoredelnegro;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;

public class ActivitiDeployment
{
	public static final String[] processNames = {"TravelAuthorization"};
	public static final String deploymentName = "BPMN";
	
	protected static ProcessEngine engine;
	protected static ProcessEngineConfiguration configuration;
	
	public static ProcessEngine getEngine()
	{
		if (engine == null) {
			configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
			engine = configuration.buildProcessEngine();
			//configuration.getJobExecutor().start();
			
			SendEmail.smtpUsername = configuration.getMailServerUsername();
			SendEmail.smtpPassword = configuration.getMailServerPassword();
			SendEmail.smtpHost = configuration.getMailServerHost();
			SendEmail.smtpPort = configuration.getMailServerPort();
			SendEmail.defaultFrom = configuration.getMailServerDefaultFrom();
			SendEmail.useTLS = configuration.getMailServerUseTLS();
		}
		
		Boolean alreadyDeployed = engine.getRepositoryService().createDeploymentQuery()
				.deploymentName(deploymentName)
				.count() > 0 ? true : false;
				
		if (!alreadyDeployed) {
			DeploymentBuilder builder = engine.getRepositoryService().createDeployment().name(deploymentName);
			
			for (String processName : processNames)
				builder.addClasspathResource(processName + ".bpmn");
			
			builder.deploy();
		}
		
		return engine;
	}
	
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
		
		ProcessInstance i = getEngine().getRuntimeService().startProcessInstanceByKey(name, new HashMap<String, Object>());
		
		if (i.getProcessInstanceId() == null)
			throw new RuntimeException("i.getProcessInstanceId() == null");
		
		return new ActivitiProcessInstance(i, getEngine());
	}
}
