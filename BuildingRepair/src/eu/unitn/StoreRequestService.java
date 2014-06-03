package eu.unitn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

public class StoreRequestService implements JavaDelegate
{
	private Expression email, roofId, deadline, maintenanceCompany;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String email = (String) this.email.getValue(execution);
		String roofId = (String) this.roofId.getValue(execution);
		Long deadline = (Long) this.deadline.getValue(execution);
		String maintenanceCompany = (String) this.maintenanceCompany.getValue(execution);
		
		Connection connection = ActivitiDeployment.getConnection();
		
		//---------------- Create table ----------------
		Statement s = connection.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS Requests (" +
				"`id` int NOT NULL AUTO_INCREMENT," +
				"`email` varchar(255) NOT NULL,"+
				"`roofId` varchar(255) NOT NULL,"+
				"`deadline` int NOT NULL,"+
				"`maintenanceCompany` varchar(255) NOT NULL,"+
				"`status` int NOT NULL,"+
				"PRIMARY KEY (`id`) )"
		);
		s.close();
		
		// Insert the request
		PreparedStatement insert = connection.prepareStatement("INSERT INTO Requests(email,roofId,deadline,maintenanceCompany,status) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		insert.setString(1, email);
		insert.setString(2, roofId);
		insert.setLong(3, deadline);
		insert.setString(4, maintenanceCompany);
		insert.setLong(5, 1);
		insert.executeUpdate();
		ResultSet ids = insert.getGeneratedKeys(); ids.first();
		Long requestId = ids.getLong(1);
		insert.close();
		
		execution.setVariable("requestId", requestId);
		System.out.println("Created request "+ requestId);
		
		connection.close();
	}

}
