import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.model.Process;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
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
	
	protected static String filename = "TravelCertificate.bpmn";
	protected static String processId = "TravelCertificate";
	
	protected static ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
	protected static HistoryService history = engine.getHistoryService();
	protected static RepositoryService repository = engine.getRepositoryService();
	protected static RuntimeService runtime = engine.getRuntimeService();
	protected static TaskService taskService = engine.getTaskService();
	protected ProcessInstance process;
	
	@BeforeClass
	public static void deployProcess() throws FileNotFoundException {
		repository.createDeployment().addClasspathResource("diagrams/"+ filename).deploy();
		//repository.createDeployment().addInputStream("TravelCertificate.bpmn20.xml", new FileInputStream(filename)).deploy();
	}
	
	@Before
	public void instantiateProcess() {
		this.process = runtime.startProcessInstanceByKey(processId, new HashMap<String, Object>());
		//System.out.println("Started process instance "+ this.process.getProcessInstanceId());
	}
	
	@After
	public void deleteProcessInstance() {
		try {
			runtime.deleteProcessInstance(this.process.getProcessInstanceId(), "Test ended");
		} catch (ActivitiObjectNotFoundException e) {}
		//process = null;
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
	 * Retrieves a task by its definition key (i.e. id in the diagram).
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
}
