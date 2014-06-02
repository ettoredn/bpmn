package me.ettoredelnegro.snippets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import me.ettoredelnegro.ActivitiDeployment;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class DatabaseAccessService implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Connection connection = ActivitiDeployment.getConnection();
		
		//---------------- Create table ----------------
		Statement s = connection.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS stuff (" +
				"`id` int NOT NULL AUTO_INCREMENT," +
				"`voter` varchar(255) NOT NULL,"+
				"`vote` tinyint(4) NOT NULL,"+
				"PRIMARY KEY (`id`) )"
		);
		s.close();
		
		//---------------- Insert with generated keys ----------------
		PreparedStatement insert = connection.prepareStatement("INSERT INTO stuff(voter, vote) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
		insert.setString(1, "ettore");
		insert.setBoolean(2, true);
		insert.executeUpdate();
		ResultSet ids = insert.getGeneratedKeys();
		ids.first();
		Long voteId = ids.getLong(1);
		insert.close();
		
		System.out.println("Added entry with id "+ voteId);

		
		//---------------- Query ----------------
		PreparedStatement ps = connection.prepareStatement("SELECT voter,vote FROM stuff WHERE id=?");
		ps.setLong(1, voteId);
		ResultSet rs = ps.executeQuery();
		
		// Returns false if the cursor is not before the first record or if _there are no rows in the ResultSet_.
		if (rs.isBeforeFirst()) {
			// There are results
		} else {
			// No results
		}
		
		// Parse results
		while (rs.next()) {
			System.out.println(""+ rs.getString(1) +" has voted "+ rs.getBoolean(2));
		}
		rs.close();

		//---------------- Close connection ----------------
		connection.close();
	}
}
