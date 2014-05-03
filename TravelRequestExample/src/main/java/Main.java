import java.lang.*;
import java.util.*;
import java.io.*;

import org.activiti.*;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class Main {
	
	private static String filename = "/Users/ettore/Workspace/bpm/TravelRequestExample/src/main/resources/diagrams/RequestAuthorization.bpmn";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException
	{	
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().buildProcessEngine();
		
		RepositoryService repository = processEngine.getRepositoryService();
		repository.createDeployment().addInputStream("TravelAuthorization.bpmn20.xml", new FileInputStream(filename)).deploy();
		
		RuntimeService runtime = processEngine.getRuntimeService();
		
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "activiti");
		
		// Must match the id of the diagram
		ProcessInstance process = runtime.startProcessInstanceByKey("TravelAuthorization", variableMap);
		
		TaskService taskService = processEngine.getTaskService();
		
		// Complete the travel request from the student
		Task travelAuthorization = taskService.createTaskQuery().taskDefinitionKey("travelauth")
				.includeProcessVariables()
				.includeTaskLocalVariables()
				.singleResult();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("studentName", "John");
		params.put("amount", new Long(2000));
		params.put("travelMotivation", "Conference presentation");
		params.put("currency", "EUR");
		taskService.complete(travelAuthorization.getId(), params);
		System.out.println("Task '"+ travelAuthorization.getName() + "' completed.");
		
		// Completes the approval
		Task travelApproval = taskService.createTaskQuery().taskDefinitionKey("travelApproval")
				.includeProcessVariables()
				.includeTaskLocalVariables()
				.singleResult();
		if (travelApproval != null) {
			//Map<String, Object> vars = travelAuthorization.getProcessVariables();
			//Map<String, Object> locals = travelAuthorization.getTaskLocalVariables();
			params.clear();
			params.put("travelApproved", "false");
			params.put("motivation", "You are not cool!");
			taskService.complete(travelApproval.getId(), params);
			System.out.println("Task '"+ travelApproval.getName() + "' completed.");
			System.out.println(travelApproval.getDescription());
			
			Task insertNewAmount = taskService.createTaskQuery().taskDefinitionKey("insertNewAmount")
					.includeProcessVariables()
					.includeTaskLocalVariables()
					.singleResult();
			if (insertNewAmount != null) {
				params.clear();
				params.put("amount", new Long(500));
				taskService.complete(insertNewAmount.getId(), params);
				System.out.println("Task '"+ insertNewAmount.getName() + "' completed.");
				
				travelApproval = taskService.createTaskQuery().taskDefinitionKey("travelApproval")
						.includeProcessVariables()
						.includeTaskLocalVariables()
						.singleResult();
				params.clear();
				params.put("travelApproved", "true");
				taskService.complete(travelApproval.getId(), params);
				System.out.println("Task '"+ travelApproval.getName() + "' completed.");
			}
		}
	}
}
