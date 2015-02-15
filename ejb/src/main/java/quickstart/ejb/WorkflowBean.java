package quickstart.ejb;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickstart.api.IWorkflowListener;
import quickstart.api.Workflow;
import quickstart.api.WorkflowException;

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

//	@Resource(mappedName = "java:comp/DefaultManagedExecutorService")
//	private ManagedExecutorService executorService;

	/**
	 * Constructor
	 */
	public WorkflowBean()
	{
	}

	// Workflow //

//	@Override
//	/* async */
//	public void startExecutorService(final IWorkflowListener listener)
//	{
//		executorService.submit(newWorkflowRunnable(listener));
//	}
//
//	@Override
//	@Asynchronous
//	public void startAsynchronous(final IWorkflowListener listener)
//	{
//		try
//		{
//			newWorkflowRunnable(listener).call();
//		}
//		catch (Exception e)
//		{
//			LOG.error("Bähm {}", listener, e);
//		}
//	}

	@Override
	public void startDeltaSpike(IWorkflowListener listener)
	{
//		this.executorService.submit(this.newWorkflowRunnable(listener));
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
