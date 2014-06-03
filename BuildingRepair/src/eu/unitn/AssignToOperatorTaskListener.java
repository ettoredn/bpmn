package eu.unitn;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

public class AssignToOperatorTaskListener implements TaskListener {
	private static final long serialVersionUID = 1L;
	
	private Expression operatorName;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		String operatorName = (String) this.operatorName.getValue(delegateTask.getExecution());
		
		delegateTask.setAssignee(operatorName);
	}

}
