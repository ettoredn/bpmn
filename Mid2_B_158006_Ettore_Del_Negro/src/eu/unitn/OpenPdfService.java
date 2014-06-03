package eu.unitn;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.el.FixedValue;

public class OpenPdfService implements JavaDelegate {

	private FixedValue emergencyId;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String emergencyId = this.emergencyId.getExpressionText();
		
		Process p = Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler c:\\"+ emergencyId +".pdf");
	}
}
