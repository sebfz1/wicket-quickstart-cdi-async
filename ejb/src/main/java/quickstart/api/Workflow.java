package quickstart.api;

import javax.ejb.Local;

@Local
public interface Workflow
{
	void startAsynchronous(final IWorkflowListener listener);
    void startDeltaSpike(final IWorkflowListener listener);
}
