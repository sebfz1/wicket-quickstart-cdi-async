package quickstart.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdiContextAwareRunnable implements Runnable
{
	static final Logger LOG = LoggerFactory.getLogger(CdiContextAwareRunnable.class);

	private final Runnable runnable;
	private final List<Class<? extends Annotation>> contexts;

	public CdiContextAwareRunnable(Runnable runnable)
	{
		this.runnable = runnable;
		this.contexts = new ArrayList<Class<? extends Annotation>>();
	}

	public boolean addContext(Class<? extends Annotation> context)
	{
		return this.contexts.add(context);
	}

	@Override
	public void run()
	{
		ContextControl control = BeanProvider.getContextualReference(ContextControl.class);

		try
		{
			for (Class<? extends Annotation> context : this.contexts)
			{
				control.startContext(context);
			}

			this.runnable.run();
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
