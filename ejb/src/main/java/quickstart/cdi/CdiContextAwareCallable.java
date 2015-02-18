package quickstart.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiContextAwareCallable<V> implements Callable<V>
{
	static final Logger LOG = LoggerFactory.getLogger(CdiContextAwareCallable.class);

	private final Callable<V> callable;
	private final List<Class<? extends Annotation>> contexts;

	public CdiContextAwareCallable(Callable<V> callable)
	{
		this.callable = callable;
		this.contexts = new ArrayList<Class<? extends Annotation>>();
	}

	public boolean addContext(Class<? extends Annotation> context)
	{
		return this.contexts.add(context);
	}

	@Override
	public V call() throws Exception
	{
		ContextControl control = BeanProvider.getContextualReference(ContextControl.class);

		try
		{
			for (Class<? extends Annotation> context : this.contexts)
			{
				control.startContext(context);
			}

			return this.callable.call();
		}
		finally
		{
			if (control != null)
			{
				for (Class<? extends Annotation> context : this.contexts)
				{
					control.stopContext(context);
				}
			}
		}
	}
}
