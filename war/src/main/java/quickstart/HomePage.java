package quickstart;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import quickstart.api.Workflow;
import quickstart.ws.WorkflowListener;
import quickstart.ws.WorkflowService;

import com.googlecode.wicket.kendo.ui.form.button.AjaxButton;
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

		this.add(new Link("goto") {

			@Override
			public void onClick()
			{
				setResponsePage(new MyPage(getPageReference()));
			}
		});
	}

	private void onStart(AjaxRequestTarget target)
	{
		final Workflow workflow = service.getWorkflow();
		final String sessionId = getSession().getId();
		final IKey pageId = new PageIdKey(getPageId());

		if (workflow != null)
		{
			Runnable task = new Runnable()
			{
				@Override
				public void run()
				{

					workflow.start(new WorkflowListener(sessionId, pageId));
				}
			};
			MyApplication.get().execute(task);
		}
		else
		{
			this.error("Workflow is undefined");
			target.add(this.feedback);
		}
	}
}
