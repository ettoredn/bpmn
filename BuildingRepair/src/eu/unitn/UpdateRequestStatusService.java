package eu.unitn;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.el.FixedValue;
import java.sql.*;

public class UpdateRequestStatusService implements JavaDelegate {
	
	private Expression requestId;
	private FixedValue status;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Long requestId = (Long) this.requestId.getValue(execution);
		Long status = Long.parseLong(this.status.getExpressionText());
		
		Connection connection = ActivitiDeployment.getConnection();
		
		PreparedStatement updateStatus = connection.prepareStatement("UPDATE Requests SET status=? WHERE id=?");
		updateStatus.setLong(1, status);
		updateStatus.setLong(2, requestId);
		updateStatus.executeUpdate();
		updateStatus.close();
		
		connection.close();
	}

}
