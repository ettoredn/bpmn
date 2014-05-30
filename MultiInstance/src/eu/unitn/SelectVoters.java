package eu.unitn;

import java.sql.*;
import java.util.*;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SelectVoters implements JavaDelegate
{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Connection connection = ActivitiDeployment.getConnection();
		
		PreparedStatement votersQuery = connection.prepareStatement("SELECT user_id_ FROM act_id_membership WHERE group_id_=?");
		votersQuery.setString(1, "students");
		ResultSet rs = votersQuery.executeQuery();
		
		Collection<String> voters = new ArrayList<String>();
		while (rs.next())
			voters.add(rs.getString(1));
		
		execution.setVariable("voters", voters);
	}
}
