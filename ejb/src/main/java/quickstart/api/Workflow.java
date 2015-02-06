package quickstart.api;

import javax.ejb.Local;

@Local
public interface Workflow
{
	/**
	 * 
	 * @param listener the {@link IWorkflowListener}
	 */
	void start(final IWorkflowListener listener);
}
