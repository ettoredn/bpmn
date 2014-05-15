package eu.unitn;

import java.sql.*;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class StoreRequest implements JavaDelegate
{
	@Override
	public void execute(DelegateExecution execution) throws Exception
	{
		Connection connection = ActivitiDeployment.getConnection();
		
		Statement stm = connection.createStatement();
		stm.executeUpdate("CREATE TABLE IF NOT EXISTS requests (" +
				"`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
				"`student_name` varchar(60) NOT NULL,"+
				"`student_email` varchar(60) NOT NULL,"+
				"`amount` int(11) unsigned NOT NULL,"+
				"`motivation` text NOT NULL,"+
				"PRIMARY KEY (`id`) )"
		);
		stm.close();
		
		PreparedStatement insertStm = connection.prepareStatement(
				"INSERT INTO requests (student_name, student_email, amount, motivation) values(?,?,?,?)",
				Statement.RETURN_GENERATED_KEYS);
		insertStm.setString(1, (String) execution.getVariable("studentName"));
		insertStm.setString(2, (String) execution.getVariable("studentEmail"));
		insertStm.setLong(3, (Long) execution.getVariable("amount"));
		insertStm.setString(4, (String) execution.getVariable("travelMotivation"));
		
		insertStm.executeUpdate();
		
		ResultSet ids = insertStm.getGeneratedKeys(); ids.first();
		Long requestId = ids.getLong(1);
		execution.setVariable("requestId", requestId);
		
//		System.out.println("Stored request "+ requestId);
	}
}
