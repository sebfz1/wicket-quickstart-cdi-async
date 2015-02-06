package quickstart.api;

import java.io.Serializable;

import javax.ejb.Local;

//@Local
public interface Workflow extends Serializable
{
	/**
	 * 
	 * @param listener the {@link IWorkflowListener}
	 */
	void start(final IWorkflowListener listener);
}
