package quickstart.ws;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.apache.wicket.util.io.IClusterable;

import quickstart.api.Workflow;

@SessionScoped
public class WorkflowService implements IClusterable
{
	private static final long serialVersionUID = 1L;

	@Inject
	private Workflow workflow;

	public Workflow getWorkflow()
	{
		return this.workflow;
	}
}
