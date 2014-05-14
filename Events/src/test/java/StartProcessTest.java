import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;


public class StartProcessTest extends ActivitiTest {

//	@Test
//	public void testStartEvent() throws Exception
//	{	
//		Calendar calendar = new Calendar.Builder().setCalendarType("gregorian").setLenient(true)
//				.setFields(Calendar.YEAR, 2013, Calendar.MONTH, Calendar.APRIL, Calendar.DAY_OF_MONTH, 30)
//				.setFields(Calendar.HOUR_OF_DAY, 23, Calendar.MINUTE, 59, Calendar.SECOND, 56).build();
//		
//		this.startJobExector(calendar);
//		
//		// After process start, there should be timer created
//		List<Job> jobs = management.createJobQuery().list();
//	    assertEquals(1, jobs.size());
//		
//	    processAllJobs(500L);
//	    
//	    jobs = management.createJobQuery().list();
//	    assertEquals(0, jobs.size());	    
//	}
	
	@Test
	public void testAskFozzie() {
		Calendar calendar = new Calendar.Builder().setCalendarType("gregorian").setLenient(true)
		.setFields(Calendar.YEAR, 2013, Calendar.MONTH, Calendar.MAY, Calendar.DAY_OF_MONTH, 1)
		.setFields(Calendar.HOUR_OF_DAY, 00, Calendar.MINUTE, 00, Calendar.SECOND, 01).build();
		
		this.startProcess(calendar);
		assertFalse(this.isProcessFinished());
		
		Task askFozzie = getTask("askFozzie");
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("onVacation", true);
		taskService.complete(askFozzie.getId(), values);
		
		assertTrue(this.isProcessFinished());
	}
	
	  public void processAllJobs(long intervalMillis)
	  {
		  Calendar calendar = configuration.getClock().getCurrentCalendar();
		  
		  do {
			  try {
				  Thread.sleep(intervalMillis);
			  } catch (InterruptedException e) {}
			  
			  calendar.add(Calendar.SECOND, 1);
			  configuration.getClock().setCurrentCalendar(calendar);
			  System.out.println("Current time: "+ configuration.getClock().getCurrentTime());
		    }
		  while (areJobsAvailable());
	  }
	  
	  public boolean areJobsAvailable() {
		  return !management.createJobQuery().list().isEmpty();
	  }
}
