package me.ettoredelnegro;

import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

/*
 * ProcessInstance wrapper.
 */
public class ActivitiProcessInstance implements ProcessInstance
{		
	protected final ProcessEngine engine;
	protected final ManagementService management;
	protected final RepositoryService repository;
	protected final HistoryService history;
	protected final RuntimeService runtime;
	protected final TaskService taskService;
	protected final ProcessInstance process;
	
	/**
	 * @param process A ProcessInstance instance.
	 */
	public ActivitiProcessInstance(ProcessInstance process, ProcessEngine engine)
	{
		if (process == null)
			throw new RuntimeException("process == null");
		
		this.process = process;
		this.engine = engine;
		
		runtime = engine.getRuntimeService();
		management = engine.getManagementService();
		repository = engine.getRepositoryService();
		history = engine.getHistoryService();
		taskService = engine.getTaskService();
	}
		
	/**
	 * Terminates the process instance.
	 */
	public void terminateProcess()
	{
		try {
			String id = this.process.getProcessInstanceId();
			runtime.deleteProcessInstance(this.process.getProcessInstanceId(), "Process stopped");
			System.out.println("Process instance "+ id +" terminated");
		} catch (ActivitiObjectNotFoundException e) {}
	}
	
	/**
	 * Whether the current process is finished.
	 * @return
	 */
	public boolean isProcessFinished() {
		if (process == null)
			throw new RuntimeException("process == null");

		HistoricProcessInstance process = history.createHistoricProcessInstanceQuery()
				.processInstanceId(this.process.getProcessInstanceId())
				.finished()
				.singleResult();
		
		return process != null;
	}
	
	/**
	 * Retrieves a task by its definition key (i.e. id of the diagram).
	 * 
	 * @param taskDefinitionKey
	 * @return
	 */
	public Task getTask(String taskDefinitionKey) {
		if (process == null)
			throw new RuntimeException("process == null");
		
		Task task = taskService.createTaskQuery()
				.taskDefinitionKey(taskDefinitionKey)
				.processInstanceId(this.process.getProcessInstanceId())
				.singleResult();
		
		if (task == null)
			throw new RuntimeException("Task "+ taskDefinitionKey +" does not exist.");
		
		return task;
	}
	
	/**
	 * Creates a TaskQuery for the managed process instance.
	 * @return
	 */
	public TaskQuery createTaskQuery() {
		if (process == null)
			throw new RuntimeException("process == null");

		return taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId());
	}
	
	/**
	 * Returns the value of a variable in the current process.
	 * 
	 * @param name
	 * @return Variable's value.
	 */
	public Object getVariable(String name) { return runtime.getVariable(process.getProcessInstanceId(), name); }
	
	/**
	 * Sets the value of a variable in the current execution.
	 * @param name
	 * @param value
	 */
	public void setVariable(String name, Object value) { runtime.setVariable(process.getProcessInstanceId(), name, value); } 

	public ProcessEngine getEngine() { return this.engine; }

	@Override
	public String getId() { return process.getId(); }

	@Override
	public boolean isEnded() { return process.isEnded(); }

	@Override
	public String getActivityId() { return process.getActivityId(); }

	@Override
	public String getProcessInstanceId() { return process.getProcessInstanceId(); }

	@Override
	public String getParentId() { return process.getParentId(); }

	@Override
	public String getProcessDefinitionId() { return process.getProcessDefinitionId(); }

	@Override
	public String getBusinessKey() { return process.getBusinessKey(); }

	@Override
	public boolean isSuspended() { return process.isSuspended(); }

	@Override
	public Map<String, Object> getProcessVariables() { return process.getProcessVariables(); }

	@Override
	public String getTenantId() { return process.getTenantId(); }
}
