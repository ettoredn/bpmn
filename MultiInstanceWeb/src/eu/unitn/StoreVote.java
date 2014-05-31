package eu.unitn;

import java.sql.*;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

public class StoreVote implements JavaDelegate
{
	private Expression vote;
	private Expression voter;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Boolean vote = (Boolean) this.vote.getValue(execution);
		String voter = (String) this.voter.getValue(execution);
		
		Connection connection = ActivitiDeployment.getConnection();
		
		Statement s = connection.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS votes (" +
				"`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
				"`voter` varchar(255) NOT NULL,"+
				"`vote` tinyint(4) NOT NULL,"+
				"PRIMARY KEY (`id`) )"
		);
		s.close();
		
		PreparedStatement insertVoteSql = connection.prepareStatement("INSERT INTO votes(voter, vote) VALUES(?,?)");
		insertVoteSql.setString(1, voter);
		insertVoteSql.setBoolean(2, vote);
		insertVoteSql.executeUpdate();
		insertVoteSql.close();
		
		connection.close();
	}
}
