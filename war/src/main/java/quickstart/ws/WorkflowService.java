package quickstart.ws;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.wicket.util.io.IClusterable;

import quickstart.api.Workflow;

@RequestScoped
public class WorkflowService implements IClusterable
{
	private static final long serialVersionUID = 1L;

	@Inject
	private Workflow workflow;

	@Inject
	private WorkflowListener listener;

	public Workflow getWorkflow()
	{
		return this.workflow;
	}

	public WorkflowListener getListener()
	{
		return this.listener;
	}
}
