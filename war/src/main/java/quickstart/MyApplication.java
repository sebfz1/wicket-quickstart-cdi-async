package quickstart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
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


	private final ExecutorService executor = Executors.newSingleThreadScheduledExecutor();


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

	public void execute(Runnable task) {
		executor.submit(new TasksRunnable(task));
	}

	@Override
	protected void onDestroy()
	{
		executor.shutdown();
		super.onDestroy();
	}

	private static class TasksRunnable implements Runnable {

		private final Runnable task;
		private final Application application;
		private final Session session;

		public TasksRunnable(Runnable task) {
			this.task = task;

			this.application = Application.get();
			this.session = Session.exists() ? Session.get() : null;
		}

		@Override
		public void run() {
			try
			{
				ThreadContext.setApplication(application);
				ThreadContext.setSession(session);
				task.run();
			} catch (Exception x)
			{
				x.printStackTrace();
			} finally {
				ThreadContext.detach();
			}
		}
	}
}
