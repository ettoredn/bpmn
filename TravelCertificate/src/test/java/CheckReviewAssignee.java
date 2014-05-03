import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.junit.Test;

public class CheckReviewAssignee extends ActivitiTest
{	
	@Test
	public void testPhdSchoolCoordinatorOnly() {
		Map<String, Object> values = new HashMap<String, Object>();
		
		// Fill the travel request
		Task requestCertificate = getTask("requestCertificate");
		values.clear();
		values.put("studentName", "John");
		values.put("studentSurname", "Page");
		values.put("certificateDescription", "My cool travel certificate");
		values.put("certificateType", "travel");
		taskService.complete(requestCertificate.getId(), values); 
		
		// Assign the approval task
		Task assignReviewer = getTask("assignReviewer");
		values.clear();
		values.put("reviewer", "phdSchoolCoordinator");
		System.out.println(assignReviewer.getDescription());
		taskService.complete(assignReviewer.getId(), values);
		
		assertTrue(isTaskFinished("departmentCoordinatorApproval"));
		assertFalse(isTaskFinished("phdSchoolCoordinatorApproval"));
		
		// Approves the request
		Task phdSchoolCoordinatorApproval = getTask("phdSchoolCoordinatorApproval");
		values.clear();
		values.put("phdApproval", false);
		taskService.complete(phdSchoolCoordinatorApproval.getId(), values);
		
		assertTrue(isProcessFinished());
	}
	
	@Test
	public void testDepartmentCoordinatorOnly() {
		Map<String, Object> values = new HashMap<String, Object>();
		
		// Fill the travel request
		Task requestCertificate = getTask("requestCertificate");
		values.clear();
		values.put("studentName", "John");
		values.put("studentSurname", "Page");
		values.put("certificateDescription", "My cool travel certificate");
		values.put("certificateType", "travel");
		taskService.complete(requestCertificate.getId(), values); 
		
		// Assign the approval task
		Task assignReviewer = getTask("assignReviewer");
		values.clear();
		values.put("reviewer", "departmentCoordinator");
		System.out.println(assignReviewer.getDescription());
		taskService.complete(assignReviewer.getId(), values);
		
		assertTrue(isTaskFinished("phdSchoolCoordinatorApproval"));
		assertFalse(isTaskFinished("departmentCoordinatorApproval"));
		
		// Approves the request
		Task departmentCoordinatorApproval = getTask("departmentCoordinatorApproval");
		values.clear();
		values.put("departmentApproval", false);
		taskService.complete(departmentCoordinatorApproval.getId(), values);
		
		assertTrue(isProcessFinished());
	}
	
	@Test
	public void testBothApprove() {
		Map<String, Object> values = new HashMap<String, Object>();
		
		// Fill the travel request
		Task requestCertificate = getTask("requestCertificate");
		values.clear();
		values.put("studentName", "John");
		values.put("studentSurname", "Page");
		values.put("certificateDescription", "My cool travel certificate");
		values.put("certificateType", "travel");
		taskService.complete(requestCertificate.getId(), values); 
		
		// Assign the approval task
		Task assignReviewer = getTask("assignReviewer");
		values.clear();
		values.put("reviewer", "both");
		System.out.println(assignReviewer.getDescription());
		taskService.complete(assignReviewer.getId(), values);
		
		assertFalse(isTaskFinished("phdSchoolCoordinatorApproval"));
		assertFalse(isTaskFinished("departmentCoordinatorApproval"));
		
		// Approves the request by the PhD school coordinator
		Task phdSchoolCoordinatorApproval = getTask("phdSchoolCoordinatorApproval");
		values.clear();
		values.put("phdApproval", true);
		taskService.complete(phdSchoolCoordinatorApproval.getId(), values);
		
		// Approves the request by the department coordinator
		Task departmentCoordinatorApproval = getTask("departmentCoordinatorApproval");
		values.clear();
		values.put("departmentApproval", false);
		taskService.complete(departmentCoordinatorApproval.getId(), values);
		
		assertTrue(isProcessFinished());
	}
}
