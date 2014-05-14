import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import org.junit.Test;


public abstract class ActivitiTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	protected static String filename = "Events.bpmn";
	protected static String processId = "Events";
	
	protected static ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
	protected static ProcessEngine engine = configuration.buildProcessEngine();
	protected static ManagementService management = engine.getManagementService();
	protected static RepositoryService repository = engine.getRepositoryService();
	protected static HistoryService history = engine.getHistoryService();
	protected static RuntimeService runtime = engine.getRuntimeService();
	protected static TaskService taskService = engine.getTaskService();
	protected ProcessInstance process;
	protected JobExecutor jobExecutor;
	
	@BeforeClass
	public static void deployProcess() throws FileNotFoundException {
		repository.createDeployment().addClasspathResource("diagrams/"+ filename).deploy();
		//repository.createDeployment().addInputStream("TravelCertificate.bpmn20.xml", new FileInputStream(filename)).deploy();
	}
	
	public void startProcess() {
		this.process = runtime.startProcessInstanceByKey(processId, new HashMap<String, Object>());
		System.out.println("Started process instance "+ this.process.getProcessInstanceId());
	}
	public void startProcess(Calendar calendar) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("calendar", calendar);
		
		this.process = runtime.startProcessInstanceByKey(processId, vars);
		System.out.println("Started process instance "+ this.process.getProcessInstanceId());
	}
	
	public void startJobExector(Calendar calendar)
	{
		if (calendar != null) {
			configuration.getClock().reset();
			configuration.getClock().setCurrentCalendar(calendar);
		}
		
		this.jobExecutor = configuration.getJobExecutor();
		jobExecutor.start();
	}
	public void startJobExector()
	{
		this.jobExecutor = configuration.getJobExecutor();
		jobExecutor.start();
	}
	
	@After
	public void deleteProcessInstance() {
		if (this.process != null) {		
			try {
				runtime.deleteProcessInstance(this.process.getProcessInstanceId(), "Test ended");
			} catch (ActivitiObjectNotFoundException e) {}
		}
		
		if (this.jobExecutor != null) {
			this.jobExecutor.shutdown();
		}
	}
	
	/**
	 * Whether the current process is finished.
	 * @return
	 */
	protected boolean isProcessFinished() {
		HistoricProcessInstance process = history.createHistoricProcessInstanceQuery()
				.processInstanceId(this.process.getProcessInstanceId())
				.finished()
				.singleResult();
		
		return process != null;
	}
	
	/**
	 * Checks whether a task as already been executed
	 * or it is not scheduled for execution.
	 * 
	 * @param taskName
	 * @return
	 */
	protected boolean isTaskFinished(String taskName) {
		Task task = taskService.createTaskQuery().taskDefinitionKey(taskName).singleResult();
		
		if (task == null)
			return true;
		
		HistoricTaskInstance historicTask = this.history.createHistoricTaskInstanceQuery()
				.processInstanceId(this.process.getProcessInstanceId())
				.taskDefinitionKey(taskName)
				.finished()
				.singleResult();
		
		return historicTask != null;
	}
	
	/**
	 * Retrieves a task by its definition key (i.e. id of the diagram).
	 * 
	 * @param taskDefinitionKey
	 * @return
	 */
	protected Task getTask(String taskDefinitionKey) {
		Task task = taskService.createTaskQuery()
				.taskDefinitionKey(taskDefinitionKey)
				.processInstanceId(this.process.getProcessInstanceId())
				.singleResult();
		
		if (task == null)
			throw new RuntimeException("Task "+ taskDefinitionKey +" does not exist.");
		
		return task;
	}
	
	/**
	 * Returns the value of a variable in the current process.
	 * 
	 * @param name
	 * @return Variable's value.
	 */
	protected Object getVariable(String name)
	{
		return runtime.getVariable(process.getProcessInstanceId(), name);
	}
}
