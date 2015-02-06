package quickstart.ws;

import javax.enterprise.context.RequestScoped;

import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.MySession;
import quickstart.api.IWorkflowListener;
import quickstart.ejb.WorkflowBean;

/**
 * Provides the listener to be supplied to the {@link WorkflowBean}
 *
 */

@RequestScoped
// @SessionScoped
public class WorkflowListener implements IWorkflowListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkflowListener.class);

	public WorkflowListener()
	{
	}

	protected IWebSocketConnection getWebSocketConnection()
	{
		// sync mode: OK
		// async mode (@Asynchronous): javax.ejb.EJBException: org.apache.wicket.WicketRuntimeException: There is no application attached to current thread EJB default
		// async mode (Executor/@SessionScoped): WELD-001303: No active contexts for scope type javax.enterprise.context.SessionScoped:
		return MySession.get().getWebSocketConnection();
	}

	@Override
	public void onMessage(String message)
	{
		LOG.info("WorkflowListener#onMessage: " + message);

		IWebSocketConnection connection = this.getWebSocketConnection();

		if (connection != null && connection.isOpen())
		{
			connection.sendMessage(new StatusMessage(String.valueOf(message)));
		}
	}

	@Override
	public void onException(String message)
	{
		LOG.info("WorkflowListener#onException: " + message);

		IWebSocketConnection connection = this.getWebSocketConnection();

		if (connection != null && connection.isOpen())
		{
			connection.sendMessage(new ExceptionMessage(String.valueOf(message)));
		}
	}

	// classes //

	public static class StatusMessage implements IWebSocketPushMessage
	{
		private final String status;

		public StatusMessage(String status)
		{
			this.status = String.valueOf(status);
		}

		public String getStatus()
		{
			return this.status;
		}
	}

	public static class ExceptionMessage implements IWebSocketPushMessage
	{
		private final String exception;

		public ExceptionMessage(String exception)
		{
			this.exception = String.valueOf(exception);
		}

		public String getException()
		{
			return this.exception;
		}
	}
}
