import org.activiti.engine.delegate.*;

public class CheckPersonalFund implements JavaDelegate {
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Long amount = (Long) execution.getVariable("amount");
		String studentName = (String) execution.getVariable("studentName");
		
		if ((amount != null) && (amount < 750)) {
			execution.setVariable("personalFundsAvailable", "true");
			System.out.println(execution.getVariable("studentName") + " does not require financing for his travel");
		} else {
			execution.setVariable("personalFundsAvailable", "false");
			System.out.println(execution.getVariable("studentName") + " requires money to finance his travel");
		}
		
		if (studentName.length() < 3) {
			execution.setVariable("personalFundsAvailable", "error");
		}
	}
}
