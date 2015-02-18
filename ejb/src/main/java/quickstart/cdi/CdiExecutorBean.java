package quickstart.cdi;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiExecutorBean
{
	static final Logger LOG = LoggerFactory.getLogger(CdiExecutorBean.class);

	@Resource(mappedName = "java:comp/DefaultManagedExecutorService")
	private ManagedExecutorService service;

	/**
	 * Constructor
	 */
	public CdiExecutorBean()
	{
	}

	protected void submit(Runnable runnable)
	{
		CdiContextAwareRunnable contextRunnable = new CdiContextAwareRunnable(runnable);
		contextRunnable.addContext(RequestScoped.class);
		contextRunnable.addContext(SessionScoped.class);
		contextRunnable.addContext(ConversationScoped.class);

		this.service.submit(contextRunnable);
	}
}
