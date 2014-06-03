package eu.unitn;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

public class AssignToOperatorTaskListener implements TaskListener {
	private static final long serialVersionUID = 1L;
	
	private Expression operatorId;

	@Override
	public void notify(DelegateTask delegateTask) {
		String operatorId = (String) this.operatorId.getValue(delegateTask.getExecution());
		
		delegateTask.setAssignee(operatorId);
	}

}
