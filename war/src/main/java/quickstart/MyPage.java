package quickstart;

import org.apache.wicket.PageReference;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;


public class MyPage extends AbstractBasePage
{
	private static final long serialVersionUID = 1L;

	public MyPage(final PageReference reference)
	{
		super(new PageParameters());
		// button //
		this.add(new Link("back") {

			@Override
			public void onClick()
			{
				setResponsePage(reference.getPage());
			}
		});
	}
}
