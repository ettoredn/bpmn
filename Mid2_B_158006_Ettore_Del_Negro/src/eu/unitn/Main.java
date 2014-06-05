package eu.unitn;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.task.Task;

import me.ettoredelnegro.ActivitiDeployment;
import me.ettoredelnegro.ActivitiProcessInstance;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("device_id", "My awesome device");
		parameters.put("gas_level", 10);
		parameters.put("user_id", "ettore");
		
		ActivitiProcessInstance process = ActivitiDeployment.createProcessInstance("GasLeakageManagement", parameters);
		
		Task readOperatorIdTask = process.createTaskQuery().taskCandidateGroup("operator").singleResult();
		
		Map<String, Object> v = new HashMap<String, Object>();
		v.put("operator_id", "ettore");
		ActivitiDeployment.completeTask(readOperatorIdTask.getId(), v);
	}
}
