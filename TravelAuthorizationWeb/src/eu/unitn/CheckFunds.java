package eu.unitn;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CheckFunds implements JavaDelegate
{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Long amount = (Long) execution.getVariable("amount");
		
		if (amount > 1000) {
			execution.setVariable("personalFundsAvailable", false);
		} else {
			execution.setVariable("personalFundsAvailable", true);
		}
	}
}
