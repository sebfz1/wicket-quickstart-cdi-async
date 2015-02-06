package quickstart;

import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.ws.WebSocketInfo;

public class MySession extends WebSession
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MySession.class);

	public static final MetaDataKey<WebSocketInfo> WEB_SOCKET_INFO = new MetaDataKey<WebSocketInfo>() {

		private static final long serialVersionUID = 1L;

	};

	public static MySession get()
	{
		return (MySession) Session.get();
	}

	public static IWebSocketConnection getWebSocketConnection(WebSocketInfo wsinfo)
	{
		IWebSocketConnection connection = null;

		if (wsinfo != null)
		{
			Application application = Application.get(wsinfo.getApplicationName());
			WebSocketSettings settings = WebSocketSettings.Holder.get(application);

			connection = settings.getConnectionRegistry().getConnection(application, wsinfo.getSessionId(), wsinfo.getKey());

			if (connection == null)
			{
				LOG.error("WebSocket connection is lost");
			}
		}
		else
		{
			LOG.error("WebSocket client is unknown");
		}

		return connection;
	}

	/**
	 * Constructor
	 */
	public MySession(Request request)
	{
		super(request);

		this.setLocale(Locale.ENGLISH);
	}

	public IWebSocketConnection getWebSocketConnection()
	{
		return MySession.getWebSocketConnection(this.getWebSocketInfo());
	}

	public WebSocketInfo getWebSocketInfo()
	{
		return this.getMetaData(WEB_SOCKET_INFO);
	}

	public void setWebSocketInfo(ConnectedMessage message)
	{
		this.setWebSocketInfo(new WebSocketInfo(message.getApplication().getName(), message.getSessionId(), message.getKey()));
	}

	public void setWebSocketInfo(WebSocketInfo wsinfo)
	{
		this.setMetaData(WEB_SOCKET_INFO, wsinfo);
	}
}
