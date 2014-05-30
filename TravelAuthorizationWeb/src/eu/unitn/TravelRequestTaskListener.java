package eu.unitn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class TravelRequestTaskListener implements TaskListener {

	private static final long serialVersionUID = -8125842029517117440L;

	@Override
	public void notify(DelegateTask delegateTask) {
		String studentName = (String) delegateTask.getVariable("studentName");
		String studentEmail = (String) delegateTask.getVariable("studentEmail");
		Long amount = (Long) delegateTask.getVariable("amount");
		String travelMotivation = (String) delegateTask.getVariable("travelMotivation");
		
		Connection connection = ActivitiDeployment.getConnection();
		
		try {
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
			insertStm.setString(1, studentName);
			insertStm.setString(2, studentEmail);
			insertStm.setLong(3, amount);
			insertStm.setString(4, travelMotivation);
			
			insertStm.executeUpdate();
			
			ResultSet ids = insertStm.getGeneratedKeys(); ids.first();
			Long requestId = ids.getLong(1);
			
			// Gets propagated as an execution variables
			delegateTask.setVariable("requestId", requestId);
		} catch (SQLException e) {
			throw new RuntimeException("[TravelRequest Listener] Error executing SQL:"+ e.getMessage());
		}
	}

}
