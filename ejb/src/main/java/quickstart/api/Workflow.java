package quickstart.api;

import java.io.Serializable;

//@Local
public interface Workflow extends Serializable
{
	/**
	 * 
	 * @param listener the {@link IWorkflowListener}
	 */
	void start(final IWorkflowListener listener);
}
