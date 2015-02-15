package quickstart.ejb;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CdiContextAwareRunnable implements Runnable
{
	static final Logger LOG = LoggerFactory.getLogger(CdiContextAwareRunnable.class);
	
	private final Runnable runnable;

	CdiContextAwareRunnable(Runnable runnable)
	{
		this.runnable = runnable;
	}

	@Override
	public void run()
	{
		ContextControl ctxCtrl = null;

		try
		{
			ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);
			ctxCtrl.startContext(RequestScoped.class);
			ctxCtrl.startContext(SessionScoped.class);
			ctxCtrl.startContext(ConversationScoped.class);
		}
		catch (RuntimeException e)
		{
			LOG.error("Failed starting contexts", e);
			throw e;
		}

		try
		{
			runnable.run();
		}
		finally
		{
			ctxCtrl.stopContext(RequestScoped.class);
			ctxCtrl.stopContext(SessionScoped.class);
			ctxCtrl.stopContext(ConversationScoped.class);
		}
	}
}