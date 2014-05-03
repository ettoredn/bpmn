import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.bpmn.model.Process;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class CheckRequestGateway {
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	private static String filename = "/Users/ettore/Workspace/bpm/TravelCertificate/src/main/resources/diagrams/TravelCertificate.bpmn";

	@Test
	public void testErrorFlow() throws FileNotFoundException {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		HistoryService history = processEngine.getHistoryService();
		RepositoryService repository = processEngine.getRepositoryService();
		repository.createDeployment().addInputStream("TravelCertificate.bpmn20.xml", new FileInputStream(filename)).deploy();
		RuntimeService runtime = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "activiti");
		// Must match the id of the diagram
		ProcessInstance process = runtime.startProcessInstanceByKey("TravelCertificate", variableMap);
		TaskService taskService = processEngine.getTaskService();
		
		Task requestCertificate = taskService.createTaskQuery().taskDefinitionKey("requestCertificate").singleResult();
		
		// Fill the request
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("studentName", "Jun");
		values.put("studentSurname", "White");
		values.put("certificateDescription", "My cool travel certificate");
		values.put("certificateType", "travel");
		taskService.complete(requestCertificate.getId(), values);
		
		// Assert process termination
		HistoricProcessInstance processEnded = history.createHistoricProcessInstanceQuery()
				.processInstanceId(process.getId())
				.finished()
				.singleResult();
		assertNotNull(processEnded);
	}
	
	@Test
	public void testNoErrorFlow() throws FileNotFoundException {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		HistoryService history = processEngine.getHistoryService();
		RepositoryService repository = processEngine.getRepositoryService();
		repository.createDeployment().addInputStream("TravelCertificate.bpmn20.xml", new FileInputStream(filename)).deploy();
		RuntimeService runtime = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "activiti");
		// Must match the id of the diagram
		ProcessInstance process = runtime.startProcessInstanceByKey("TravelCertificate", variableMap);
		TaskService taskService = processEngine.getTaskService();
		
		Task requestCertificate = taskService.createTaskQuery().taskDefinitionKey("requestCertificate").singleResult();
		
		// Fill the request
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("studentName", "John");
		values.put("studentSurname", "White");
		values.put("certificateDescription", "My cool travel certificate");
		values.put("certificateType", "travel");
		taskService.complete(requestCertificate.getId(), values);
		
		// Get variable from runtime
		String checkResult = (String) runtime.getVariable(process.getId(), "checkResult");
		assertTrue(checkResult == "OK");
		
		// Assert the process instance must not be finished
		HistoricProcessInstance processEnded = history.createHistoricProcessInstanceQuery()
				.processInstanceId(process.getId())
				.finished()
				.singleResult();
		assertNull(processEnded);
		
		//assertNotNull(taskService.createTaskQuery().taskDefinitionKey("assignReviewer").singleResult());
	}
}
