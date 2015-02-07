package quickstart;

import org.apache.wicket.Page;
import org.apache.wicket.cdi.CdiConfiguration;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

/**
 * 
 */
public class MyApplication extends WebApplication
{
	public static MyApplication get()
	{
		return (MyApplication) WebApplication.get();
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		// CDI //
		new CdiConfiguration().configure(this);
	}

	@Override
	public Class<? extends Page> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	public MySession newSession(Request request, Response response)
	{
		return new MySession(request);
	}
}
