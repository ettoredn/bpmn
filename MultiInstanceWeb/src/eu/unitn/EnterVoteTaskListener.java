package eu.unitn;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;

public class EnterVoteTaskListener implements TaskListener
{
	private static final long serialVersionUID = 1L;
	
	private Expression assignee;
	private FixedValue fixedValue;

	@Override
	public void notify(DelegateTask task) {
		String assignee = (String) this.assignee.getValue(task.getExecution());
		String stuff = this.fixedValue.getExpressionText();
		
		task.setAssignee(assignee);
	}
}
