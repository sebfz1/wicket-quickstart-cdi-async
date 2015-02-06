package quickstart.api;

import java.io.Serializable;

/**
 * Specifies the workflow listener
 *
 */
public interface IWorkflowListener extends Serializable
{
	/**
	 * Triggered when a workflow message is sent
	 * 
	 * @param message
	 */
	void onMessage(String message);

	/**
	 * Triggered when an exception occurs
	 * 
	 * @param message the exception message
	 */
	void onException(String message);
}
