package eu.unitn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.ettoredelnegro.ActivitiDeployment;
import me.ettoredelnegro.ActivitiProcessInstance;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class LoginTaskListener implements TaskListener {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask loginTask)
	{
		ActivitiProcessInstance process = ActivitiDeployment.getProcessInstance(loginTask.getProcessInstanceId());
		
		if (loginTask.getEventName() == "create") {
			loginTask.setAssignee("system");
			return;
		}
		
		Connection connection = ActivitiDeployment.getConnection();
		String username = (String) loginTask.getVariable("username");
		String password = (String) loginTask.getVariable("password");
		String role = (String) loginTask.getVariable("role");
		
		try {
			Statement stm = connection.createStatement();
			stm.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
					"`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
					"`username` varchar(256) NOT NULL,"+
					"`password` varchar(256) NOT NULL,"+
					"`role` varchar(256) NOT NULL,"+
					"PRIMARY KEY (`id`) )"
			);
			stm.close();
			
			PreparedStatement ps = connection.prepareStatement("SELECT username,role FROM users WHERE username=? AND password=? AND role=? LIMIT 1");
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, role);
			
			ResultSet result = ps.executeQuery();
			
			// Returns false if the cursor is not before the first record or if _there are no rows in the ResultSet_.
			if (result.isBeforeFirst()) {
				process.setVariable("authenticated", true);
				System.out.println("[Login] User "+ username +" successfully authenticated as a "+ role);
			} else {
				process.setVariable("authenticated", false);
				System.out.println("[Login] Authentication failure for user "+ username +" as a "+ role);
			}
		} catch (SQLException e) {
			throw new RuntimeException("[LoginTask Listener] SQL Exception: "+ e.getMessage());
		}
	}
}
