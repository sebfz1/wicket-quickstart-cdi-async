package quickstart.ejb;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.api.IWorkflowListener;
import quickstart.api.Workflow;
import quickstart.api.WorkflowException;
import quickstart.cdi.CdiContextAwareRunnable;
import quickstart.cdi.CdiExecutorBean;

@Stateless
// @Stateful
// @ApplicationScoped
// @TransactionManagement(TransactionManagementType.BEAN)
public class WorkflowBean extends CdiExecutorBean implements Workflow
{
	public interface IWorkflowStep extends Runnable
	{
	}

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowBean.class);

	private static final String MESSAGE_PATTERN = "<b>%s</b><br/>%s <i>%d%%</i>";
	private static final String EXCEPTION_PATTERN = "<b>%s</b><br/>%s";

	/**
	 * Constructor
	 */
	public WorkflowBean()
	{
	}

	// Workflow //

	/**
	 * Does actually not work
	 */
	@Override
	@Asynchronous
	public void startAsynchronous(final IWorkflowListener listener)
	{
		try
		{
			CdiContextAwareRunnable runnable = new CdiContextAwareRunnable(this.newWorkflowRunnable(listener));
			// runnable.addContext(RequestScoped.class);
			runnable.addContext(SessionScoped.class);
			// runnable.addContext(ApplicationScoped.class);
			runnable.addContext(ConversationScoped.class);

			runnable.run();
		}
		catch (WorkflowException e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void startDeltaSpike(IWorkflowListener listener)
	{
		this.submit(this.newWorkflowRunnable(listener));
	}

	// Factories //

	private Runnable newWorkflowRunnable(final IWorkflowListener listener)
	{
		return new Runnable() {

			@Override
			public void run()
			{
				LOG.info("Workflow started... {}", CDI.current());

				for (IWorkflowStep step : newWorkflowSteps(listener))
				{
					try
					{
						step.run(); /* sync */
					}
					catch (WorkflowException e)
					{
						listener.onException(String.format(EXCEPTION_PATTERN, "Workflow", e.getMessage()));
					}
				}
			}
		};
	}

	private List<IWorkflowStep> newWorkflowSteps(final IWorkflowListener listener)
	{
		List<IWorkflowStep> list = new LinkedList<>();
		list.add(newWorkFlowStep0(listener));

		return list;
	}

	private IWorkflowStep newWorkFlowStep0(final IWorkflowListener listener)
	{
		return new IWorkflowStep() {

			@Override
			public void run()
			{
				try
				{
					for (int i = 0; i <= 100; i += 10)
					{
						LOG.info("IWorkflowStep#run " + i);

						listener.onMessage(String.format(MESSAGE_PATTERN, "Workflow", "Processing...", i));
						Thread.sleep(2000);
					}
				}
				catch (InterruptedException | RuntimeException e)
				{
					LOG.error(e.getMessage(), e);
					throw new WorkflowException(e.getMessage(), e);
				}
			}
		};
	}
}
