package quickstart;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import quickstart.api.Workflow;
import quickstart.ws.WorkflowService;

import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.markup.html.link.BookmarkablePageLink;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class HomePage extends AbstractBasePage
{
	private static final long serialVersionUID = 1L;

	@Inject
	private WorkflowService service;

	private final KendoFeedbackPanel feedback;

	public HomePage(PageParameters parameters)
	{
		super(parameters);

		// feedback //
		this.feedback = new KendoFeedbackPanel("feedback");
		this.add(this.feedback.setEscapeModelStrings(false));

		// form //
		final Form<?> form = new Form<Void>("form");
		this.add(form);

		// button //
		form.add(new AjaxButton("button") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, Form<?> form)
			{
				this.info("Started!");
				target.add(feedback);

				onStart(target);
			}
		});

		this.add(new BookmarkablePageLink<Void>("goto", MyPage.class));
	}

	private void onStart(AjaxRequestTarget target)
	{
		Workflow workflow = service.getWorkflow();

		if (workflow != null)
		{
			workflow.start(service.getListener());
			// workflow.start(new WorkflowListener(MySession.get().getWebSocketInfo()));
		}
		else
		{
			this.error("Workflow is undefined");
			target.add(this.feedback);
		}
	}
}
