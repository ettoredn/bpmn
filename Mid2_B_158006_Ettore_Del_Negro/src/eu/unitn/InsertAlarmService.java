package eu.unitn;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import java.sql.*;

public class InsertAlarmService implements JavaDelegate {
	
	private Expression deviceId;
	private Expression gasLevel;
	private Expression userId;
	private Expression operatorId;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		try {
			String deviceId = (String) this.deviceId.getValue(execution);
			Long gasLevel = (Long) this.gasLevel.getValue(execution);
			String userId = (String) this.userId.getValue(execution);
			String operatorId = (String) this.operatorId.getValue(execution);
			
			Connection connection = ActivitiDeployment.getConnection();
			
			//---------------- Create table ----------------
			Statement s = connection.createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS ALARMS (" +
					"`alarm_id` int NOT NULL AUTO_INCREMENT," +
					"`device_id` varchar(255) NOT NULL,"+
					"`gas_level` int NOT NULL,"+
					"`user_id` varchar(255) NOT NULL,"+
					"`operator_id` varchar(255) NOT NULL,"+
					"PRIMARY KEY (`alarm_id`) )"
			);
			s.close();
			
			// Insert alarm and update the alarm_id variable
			PreparedStatement insert = connection.prepareStatement("INSERT INTO ALARMS(device_id,gas_level,user_id,operator_id) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			insert.setString(1, deviceId);
			insert.setLong(2, gasLevel);
			insert.setString(3, userId);
			insert.setString(4, operatorId);
			insert.executeUpdate();
			ResultSet ids = insert.getGeneratedKeys();
			ids.first();
			Long alarmId = ids.getLong(1);
			insert.close();
			
			execution.setVariable("alarm_id", alarmId);
			
			System.out.println("Added alarm with id "+ alarmId);
			
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error inserting the alarm into the database.");
			throw new BpmnError("failure");
		}
		
	}

}
