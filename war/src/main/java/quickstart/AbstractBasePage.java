package quickstart;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.ws.WorkflowListener.ExceptionMessage;
import quickstart.ws.WorkflowListener.StatusMessage;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.widget.notification.Notification;

public abstract class AbstractBasePage extends WebPage
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBasePage.class);

	/** notifications */
	private final Notification notification;

	public AbstractBasePage(PageParameters parameters)
	{
		super(parameters);

		// websocket notification //
		Options options = new Options();
		options.set("width", 320);
		options.set("hideOnClick", true);
		options.set("autoHideAfter", 10000);
		options.set("position", "{ top: 80, right: 80 }");

		this.notification = new Notification("notification", options);
		this.add(this.notification);

		// websocket behavior //
		this.add(this.newWebSocketBehavior());
	}

	// Factories //

	private final WebSocketBehavior newWebSocketBehavior()
	{
		return new WebSocketBehavior() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onConnect(ConnectedMessage message)
			{
				MySession.get().setWebSocketInfo(message);

				LOG.info("WebSocket client is connected");
			}

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message)
			{
				if (message instanceof StatusMessage)
				{
					String value = ((StatusMessage) message).getStatus();

					notification.show(handler, value, Notification.INFO);
				}

				if (message instanceof ExceptionMessage)
				{
					String value = ((ExceptionMessage) message).getException();

					notification.show(handler, value, Notification.ERROR);
				}
			}

			@Override
			public void onException(Component component, RuntimeException e)
			{
				LOG.warn(e.getMessage(), e);
			}
		};
	}
}
