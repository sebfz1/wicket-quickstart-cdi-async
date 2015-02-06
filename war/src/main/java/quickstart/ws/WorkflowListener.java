package quickstart.ws;

import org.apache.wicket.Application;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.api.IWorkflowListener;
import quickstart.ejb.WorkflowBean;

/**
 * Provides the listener to be supplied to the {@link WorkflowBean}
 *
 */

//@RequestScoped
public class WorkflowListener implements IWorkflowListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkflowListener.class);
	private final String sessionId;
	private final IKey pageId;

	public WorkflowListener(String sessionId, IKey pageId)
	{
		this.sessionId = sessionId;
		this.pageId = pageId;
	}

	protected IWebSocketConnection getWebSocketConnection()
	{
		// sync mode: OK
		// async mode (@Asynchronous): javax.ejb.EJBException: org.apache.wicket.WicketRuntimeException: There is no application attached to current thread EJB default
		// async mode (Executor/@SessionScoped): WELD-001303: No active contexts for scope type javax.enterprise.context.SessionScoped:
		Application application = Application.get();
		WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
		IWebSocketConnectionRegistry connectionRegistry = webSocketSettings.getConnectionRegistry();
		IWebSocketConnection connection = connectionRegistry.getConnection(application, sessionId, pageId);
		return connection;
//		return MySession.get().getWebSocketConnection();
	}

	@Override
	public void onMessage(String message)
	{
		LOG.info("WorkflowListener#onMessage: " + message);

		try
		{
			ThreadContext.setApplication(Application.get("cdi-sync"));

			IWebSocketConnection connection = this.getWebSocketConnection();

			if (connection != null && connection.isOpen())
			{
				connection.sendMessage(new StatusMessage(String.valueOf(message)));
			}
		}
		finally
		{
			ThreadContext.detach();
		}
	}

	@Override
	public void onException(String message)
	{
		LOG.info("WorkflowListener#onException: " + message);

		try
		{
			ThreadContext.setApplication(Application.get("cdi-sync"));

			IWebSocketConnection connection = this.getWebSocketConnection();

			if (connection != null && connection.isOpen())
			{
				connection.sendMessage(new ExceptionMessage(String.valueOf(message)));
			}
		}
		finally
		{
			ThreadContext.detach();
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
