package quickstart.ejb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiExecutorBean
{
	static final Logger LOG = LoggerFactory.getLogger(CdiExecutorBean.class);

//	@Resource(mappedName = "java:comp/DefaultManagedExecutorService")
//	private ManagedExecutorService executorService;

	/**
	 * Constructor
	 */
	public CdiExecutorBean()
	{
	}

	protected void submit(Runnable runnable)
	{
//		this.executorService.submit(new CdiContextAwareRunnable(runnable));
	}
}
