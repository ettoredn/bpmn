package eu.unitn.jour_name;

import org.activiti.engine.delegate.*;

public class CheckRequest implements JavaDelegate
{
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String studentName = (String) execution.getVariable("studentName");
		String studentSurname = (String) execution.getVariable("studentSurname");
		String certificateDescription = (String) execution.getVariable("certificateDescription");
		String certificateType = (String) execution.getVariable("certificateType");
		
		if (certificateType == "travel") {
			// Check whether the student already did a travel
			if (studentName == "Jun") {
				// She hasn't
				execution.setVariable("checkResult", "no travels found");
				return;
			}
		}
		else {
			// Other types of certificates
		}
		
		execution.setVariable("checkResult", "OK");
	}
}
