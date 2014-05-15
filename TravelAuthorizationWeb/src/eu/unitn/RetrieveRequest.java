package eu.unitn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class RetrieveRequest implements JavaDelegate
{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Long id = (Long) execution.getVariable("requestId");
		
		Connection conn = ActivitiDeployment.getConnection();
		
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM requests WHERE id=? LIMIT 1");
		ps.setLong(1, id);
		ResultSet results = ps.executeQuery();
		results.first();
		
//		System.out.println("Retrived request "+ results.getLong("id"));
		
		execution.setVariable("studentName", results.getString("student_name"));
		execution.setVariable("studentEmail", results.getString("student_email"));
		execution.setVariable("amount", results.getLong("amount"));
		execution.setVariable("travelMotivation", results.getString("motivation"));
	}
}
