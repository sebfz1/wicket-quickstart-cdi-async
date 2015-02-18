package quickstart.ws;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.MySession;
import quickstart.api.IWorkflowListener;
import quickstart.api.Workflow;

/**
 * Provides the listener to be supplied to the {@link Workflow}
 *
 */
public class WorkflowListener implements IWorkflowListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkflowListener.class);

	/** the current thread context */
	private final ThreadContext threadContext;

	/**
	 * Indicates whether the job is designed to be run in async mode.<br />
	 * It seems that it cannot be guessed by ThreadContext#exists(), probably because of the Session#get() which somehow shortcuts the logic
	 */
	private final boolean asynchronous;

	public WorkflowListener()
	{
		this(false);
	}

	public WorkflowListener(boolean asynchronous)
	{
		this.threadContext = ThreadContext.get(false);
		this.asynchronous = asynchronous;
	}

	// Methods //

	private synchronized IWebSocketConnection getWebSocketConnection()
	{
		try
		{
			if (this.asynchronous)
			{
				ThreadContext.restore(this.threadContext);
			}

			return MySession.get().getWebSocketConnection();
		}
		finally
		{
			if (this.asynchronous)
			{
				ThreadContext.detach();
			}
		}
	}

	// Events //

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

	// Classes //

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
