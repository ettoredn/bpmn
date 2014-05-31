package eu.unitn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class ProcessVotes implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Connection c = ActivitiDeployment.getConnection();
		
		Statement s = c.createStatement();
		ResultSet votes = s.executeQuery("SELECT vote,COUNT(*) FROM votes GROUP BY vote");
		
		int approve = 0, reject = 0;
		
		while (votes.next())
		{
			Boolean approved = votes.getBoolean(1);
			
			if (approved)
				approve = votes.getInt(2);
			else
				reject = votes.getInt(2);
		}
		votes.close();
		s.close();
		c.close();
		
		execution.setVariable("travelApproved", (approve > reject ? true : false));
	}

}
