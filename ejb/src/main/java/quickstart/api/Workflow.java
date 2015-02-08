package quickstart.api;

import javax.ejb.Local;

@Local
public interface Workflow
{
	/**
	 * 
	 * @param listener the {@link IWorkflowListener}
	 */
	void startAsynchronous(final IWorkflowListener listener);

    void startExecutorService(final IWorkflowListener listener);

    void startDeltaSpike(final IWorkflowListener listener);

}
