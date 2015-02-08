package quickstart.ejb;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickstart.api.IWorkflowListener;
import quickstart.api.Workflow;
import quickstart.api.WorkflowException;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

@Stateless
// @Stateful
// @ApplicationScoped
// @TransactionManagement(TransactionManagementType.BEAN)
public class WorkflowBean implements Workflow
{
	public interface IWorkflowStep extends Runnable
	{
	}

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowBean.class);

	private static final String MESSAGE_PATTERN = "<b>%s</b><br/>%s <i>%d%%</i>";
	private static final String EXCEPTION_PATTERN = "<b>%s</b><br/>%s";

    @Resource(mappedName="java:comp/DefaultManagedExecutorService")
    private ManagedExecutorService executorService;

    @Inject
    private Event<WorkflowProgress> eventSource;

	/**
	 * Constructor
	 */
	public WorkflowBean()
	{
	}

	// Workflow //

	@Override
	/* async */
	public void startExecutorService(final IWorkflowListener listener)
	{
        executorService.submit(createTask(listener));
	}

    @Override
    @Asynchronous
    public void startAsynchronous(final IWorkflowListener listener)
    {
        try {
            createTask(listener).call();
        } catch (Exception e) {
            LOG.error("Bähm {}", listener, e);
        }
    }

    @Override
    public void startDeltaSpike(IWorkflowListener listener) {
        executorService.submit(
                new CdiContextAwareCallable<>(createTask(listener))
        );
    }

    private Callable<Void> createTask(final IWorkflowListener listener) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
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

                return null;
            }
        };
    }

	// Factories //

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

    private static class CdiContextAwareCallable<V> implements Callable<V> {

        private Callable<V> callable;

        private CdiContextAwareCallable(Callable<V> callable) {
            this.callable = callable;
        }

        @Override
        public V call() throws Exception {

            ContextControl ctxCtrl = null;
            try {
                ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);
                ctxCtrl.startContext(RequestScoped.class);
                ctxCtrl.startContext(SessionScoped.class);
                ctxCtrl.startContext(ConversationScoped.class);
            } catch (Exception e) {
                LOG.error("Failed starting contexts", e);
                throw e;
            }

            try {
                return callable.call();
            } finally {
                ctxCtrl.stopContext(RequestScoped.class);
                ctxCtrl.stopContext(SessionScoped.class);
                ctxCtrl.stopContext(ConversationScoped.class);
            }
        }
    }
}
