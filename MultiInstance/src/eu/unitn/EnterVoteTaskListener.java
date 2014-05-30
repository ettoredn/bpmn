package eu.unitn;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class EnterVoteTaskListener implements TaskListener
{
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {
		String assignee = (String) ActivitiDeployment.getProcessInstance(task.getProcessInstanceId()).getVariable("voter");
		
	}

}
