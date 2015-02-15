package quickstart.ejb;

import java.util.concurrent.Callable;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CdiContextAwareCallable<V> implements Callable<V>
{
	static final Logger LOG = LoggerFactory.getLogger(CdiContextAwareCallable.class);

	private Callable<V> callable;

	private CdiContextAwareCallable(Callable<V> callable)
	{
		this.callable = callable;
	}

	@Override
	public V call() throws Exception
	{
		ContextControl ctxCtrl = null;

		try
		{
			ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);
			ctxCtrl.startContext(RequestScoped.class);
			ctxCtrl.startContext(SessionScoped.class);
			ctxCtrl.startContext(ConversationScoped.class);
		}
		catch (Exception e)
		{
			LOG.error("Failed starting contexts", e);
			throw e;
		}

		try
		{
			return callable.call();
		}
		finally
		{
			ctxCtrl.stopContext(RequestScoped.class);
			ctxCtrl.stopContext(SessionScoped.class);
			ctxCtrl.stopContext(ConversationScoped.class);
		}
	}
}