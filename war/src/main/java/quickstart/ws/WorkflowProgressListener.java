package quickstart.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickstart.ejb.WorkflowProgress;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

@RequestScoped
public class WorkflowProgressListener {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowProgressListener.class);

    public void onWorkflowProgress(@Observes WorkflowProgress progress) {
        LOG.info("onWorkflowProgress: {}", progress.getMessage());
    }

}
